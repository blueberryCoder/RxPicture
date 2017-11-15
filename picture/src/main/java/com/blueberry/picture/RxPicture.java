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

package com.blueberry.picture;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by blueberry on 11/14/2017.
 */

public class RxPicture {
    private static final String TAG = "RxPicture";

    static final String FRAGMENT_TAG = "RxPictureFragmentTag";
    static final Object TRIGGER = new Object();

    private RxPictureFragment mRxPictureFragment;

    private PictureInfoFactory mPictureInfoFactory;

    public RxPicture(AppCompatActivity appCompatActivity) {
        this.mRxPictureFragment = getRxPictureFragment(appCompatActivity);
        this.mPictureInfoFactory = new DefaultPictureInfoFactoryImpl(appCompatActivity);
    }

    private RxPictureFragment getRxPictureFragment(AppCompatActivity appCompatActivity) {
        RxPictureFragment rxPictureFragment = findRxPictureFragment(appCompatActivity);
        if (null == rxPictureFragment) {
            rxPictureFragment = RxPictureFragment.newInstance();
            FragmentManager supportFragmentManager = appCompatActivity.getSupportFragmentManager();
            supportFragmentManager.beginTransaction()
                    .add(rxPictureFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
            supportFragmentManager.executePendingTransactions();
        }
        return rxPictureFragment;
    }

    private RxPictureFragment findRxPictureFragment(AppCompatActivity appCompatActivity) {
        return (RxPictureFragment) appCompatActivity.getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_TAG);
    }

    /**
     * 设置自定义的PictureInfoFactory,用来管理拍照时图片的存储。
     *
     * @param factory
     * @return
     */
    public RxPicture setPictureInfoFactory(PictureInfoFactory factory) {
        this.mPictureInfoFactory = factory;
        return this;
    }

    /**
     * 请求拍照
     *
     * @return
     */
    public Observable<String> requestTakePicture() {
        return Observable.just(TRIGGER).compose(ensureTakePicture());
    }

    /**
     * 请求选择图片
     *
     * @return
     */
    public Observable<String> requestSelectFromAlbum() {
        return Observable.just(TRIGGER).compose(ensureSelectFromAlbum());
    }

    /**
     * 这个可以用来组合别的框架，例如RxView
     *
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<T, String> ensureSelectFromAlbum() {
        return new ObservableTransformer<T, String>() {
            @Override
            public ObservableSource<String> apply(Observable<T> upstream) {
                return upstream.flatMap(new Function<T, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(T t) throws Exception {
                        return selectFromAlbum();
                    }
                });
            }
        };
    }

    /**
     * 这个可以用来组合别的框架。例如RxView
     *
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<T, String> ensureTakePicture() {
        return new ObservableTransformer<T, String>() {
            @Override
            public ObservableSource<String> apply(Observable<T> upstream) {
                return upstream.flatMap(new Function<T, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(T t) throws Exception {
                        return takePicture();
                    }
                });
            }
        };
    }

    private Observable<String> takePicture() {
        PublishSubject<String> subject
                = mRxPictureFragment.getSubjectByKey(RxPictureFragment.TAKE_PICTURE);
        if (subject == null) {
            subject = PublishSubject.create();
            mRxPictureFragment.setSubject(RxPictureFragment.TAKE_PICTURE, subject);
        }

        mRxPictureFragment.takePicture(mPictureInfoFactory);
        return subject;
    }


    private Observable<String> selectFromAlbum() {
        PublishSubject<String> subject = mRxPictureFragment
                .getSubjectByKey(RxPictureFragment.SELECT_FROM_ALBUM);
        if (subject == null) {
            subject = PublishSubject.create();
            mRxPictureFragment.setSubject(RxPictureFragment.SELECT_FROM_ALBUM, subject);
        }
        mRxPictureFragment.selectPhotoFromAlbum();
        return subject;
    }
}