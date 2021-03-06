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

package com.blueberry.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.blueberry.rxpicture.PictureInfo;
import com.blueberry.rxpicture.PictureInfoFactory;
import com.blueberry.rxpicture.RxPicture;
import com.bumptech.glide.Glide;

import java.io.File;

import io.reactivex.functions.Consumer;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                new RxPicture(MainActivity.this)
                        .setPictureInfoFactory(new PictureInfoFactory() {
                            @Override
                            public PictureInfo generatePictureInfo() {
                                File file = new File(MainActivity.this.getFilesDir(),
                                        "/temp1/" + System.currentTimeMillis() + ".jpg");
                                if (!file.getParentFile().exists()) {
                                    file.getParentFile().mkdirs();
                                }
                                Uri imageUri = FileProvider
                                        .getUriForFile(MainActivity.this,
                                                "com.blueberry.sample.customer.fileprovider", file);
                                PictureInfo pictureInfo = new PictureInfo();
                                pictureInfo.setUri(imageUri);
                                pictureInfo.setPath(file.getAbsolutePath());
                                return pictureInfo;
                            }
                        })
                        .requestTakePicture()
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.i(TAG, "path:" + s);
                                Glide.with(MainActivity.this)
                                        .load(s)
                                        .into((ImageView) findViewById(R.id.iv));
                            }
                        });


            }
        });

        findViewById(R.id.btn_select_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RxPicture(MainActivity.this)
                        .requestSelectFromAlbum()
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Glide.with(MainActivity.this)
                                        .load(s)
                                        .into((ImageView) findViewById(R.id.iv));
                            }
                        });
            }
        });
    }
}
