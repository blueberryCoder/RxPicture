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

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by blueberry on 11/15/2017.
 */

public class DefaultPictureInfoFactoryImpl implements PictureInfoFactory {

    public String fileProvideAuthority = "com.blueberry.picture.fileprovider";

    public Context mContext;

    public DefaultPictureInfoFactoryImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public PictureInfo generatePictureInfo() {

        File file = new File(mContext.getFilesDir(), "/temp/"
                + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = FileProvider.getUriForFile(mContext, fileProvideAuthority, file);

        PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setPath(file.getAbsolutePath());
        pictureInfo.setUri(imageUri);
        return pictureInfo;
    }
}
