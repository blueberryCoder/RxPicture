/*
 *    Copyright 2017 blueberry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.blueberry.rxpicture;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by blueberry on 11/14/2017.
 */

public class RxPictureFragment extends Fragment {

    private static final String TAG = "RxPictureFragment";

    public static final String TAKE_PICTURE = "take_picture";
    public static final String SELECT_FROM_ALBUM = "select_from_album";

    public static final int TAKE_PICTURE_CODE = 66;
    public static final int SELECT_PICTURE_CODE = 67;

    private HashMap<String, PublishSubject<String>> mSubjects = new HashMap<>();

    private String tempTakePicturePath;

    public static RxPictureFragment newInstance() {

        Bundle args = new Bundle();

        RxPictureFragment fragment = new RxPictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * 拍照。
     *
     * @return 文件路径。
     */
    public void takePicture(PictureInfoFactory factory) {

        PictureInfo pictureInfo = factory.generatePictureInfo();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureInfo.getUri());
        startActivityForResult(intent, TAKE_PICTURE_CODE);
        tempTakePicturePath = pictureInfo.getPath();
    }


    /**
     * 从相册选择
     */
    public void selectPhotoFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_CODE);
    }

    public void getImagePathFromAlbumData(Intent data, PublishSubject<String> subject) {
        Uri uri = data.getData();
        if (uri == null) {
            subject.onError(new PictureSelectException(PictureSelectException.SELECT_FROM_ALBUM_NO_DATA,
                    "从相册获取的data为空"));
            return;
        }
        if (uri.getScheme().equals("file")) {
            String path = uri.getPath();
            // output path
            subject.onNext(path);
            subject.onComplete();
        } else {
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri,
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(0);
                    //output path
                    subject.onNext(path);
                    subject.onComplete();
                }
            } finally {
                close(cursor);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE_CODE) {
            handleTakePictureFinished(resultCode);
        } else if (requestCode == SELECT_PICTURE_CODE) {
            handleImageFromAlbum(resultCode, data);
        }
    }

    private void handleImageFromAlbum(int resultCode, Intent data) {
        PublishSubject<String> subject = getPublishSubjectFromKey(SELECT_FROM_ALBUM);
        if (subject == null) return;
        if (resultCode == Activity.RESULT_OK) {
            getImagePathFromAlbumData(data, subject);
        } else {
            subject.onError(new PictureSelectException(PictureSelectException.SELECT_FROM_ALBUM_FAIL,
                    "选择相册失败了"));
        }
    }

    private void handleTakePictureFinished(int resultCode) {
        PublishSubject<String> subject = getPublishSubjectFromKey(TAKE_PICTURE);
        if (subject == null) return;
        if (resultCode == Activity.RESULT_OK) {
            subject.onNext(tempTakePicturePath);
            subject.onComplete();
        } else {
            subject.onError(new PictureSelectException(PictureSelectException.TAKE_PICTURE_FAIL,
                    "拍照失败了"));
        }
    }

    public PublishSubject<String> getSubjectByKey(String key) {
        return mSubjects.get(key);
    }

    public PublishSubject<String> setSubject(String key, PublishSubject<String> subject) {
        return mSubjects.put(key, subject);
    }

    private PublishSubject<String> getPublishSubjectFromKey(String selectFromAlbum) {
        PublishSubject<String> subject = mSubjects.get(selectFromAlbum);
        if (subject != null) {
            mSubjects.remove(selectFromAlbum);
        }
        return subject;
    }

    private void close(Cursor cursor) {
        if (null != cursor) {
            cursor.close();
        }
    }

}
