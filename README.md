# 图片选取工具
[ ![Download](https://api.bintray.com/packages/blueberrycoder/maven/rtmplive/images/download.svg) ](https://bintray.com/blueberrycoder/maven/rtmplive/_latestVersion)

## 使用方法

### 添加依赖

```groovy
   
   implementation 'com.blueberry:rxpicture:0.1.1'
   
```
这个工具依赖`Rxjava2`和`AppCompat`包，所有你还需要同时引入

```groovy

   implementation "io.reactivex.rxjava2:rxjava:${rxJavaVersion}"
   implementation "com.android.support:appcompat-v7:${appcompatVersion}"

```

### 从相册中选择图片

```java
 new RxPicture(MainActivity.this)
                        .requestSelectFromAlbum()
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                // s 就是图片的本地路径
                                Glide.with(MainActivity.this)
                                        .load(s)
                                        .into((ImageView) findViewById(R.id.iv));
                            }
                        });
```

### 使用相机拍照

```java
   new RxPicture(MainActivity.this)
                        .requestTakePicture()
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                // s 就是图片的路径
                            }
                        });
```


### 自定义FileProvider

如果你只是想简单的使用，不想那么麻烦，你就直接使用上面的代码就可以了，如果你想自己来实现以下FileProvider请继续看。

拍照我默认写了一个FileProvider,并写了默认的`PictureInfoFactory`的实现类`DefaultPictureInfoFactoryImpl`

```xml
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.blueberry.rxpicture.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"/>
        </provider>
```

```java

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

```

如果要自定义FileProvider,可以写一个类来实现PictureInfoFactory,以及在AndroidManifest.xml中定义一个FileProvider.

要注意的是，如果你在AndroidManifest.xml中重新定义了FileProvider会与我aar中的定义的FileProvider冲突，可以用过
`tool:repleace` 来解决这个问题。

例如我要自定一个FileProvider：

第一步在AndroidManifest.xml中声明我定义的FileProvider.

```xml
       <provider
                android:name="android.support.v4.content.FileProvider"
                android:authorities="com.blueberry.sample.customer.fileprovider"
                android:exported="false"
                android:grantUriPermissions="true"
                tools:replace="android:authorities"
                >
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/custom_file_paths"
                    tools:replace="android:resource,android:name"
                    ></meta-data>
    
            </provider>
```

第二步，自己定义一个类来实现PictureInfoFactory,我这里就简单用一个匿名类来演示好了。

```java

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

```
# License

```
Copyright (c) 2017. blueberry
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0
     
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```