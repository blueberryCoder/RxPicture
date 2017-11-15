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

/**
 * Created by blueberry on 11/15/2017.
 */

public class PictureSelectException extends RuntimeException {
    public static final int BASE = 40;

    public static final int SELECT_FROM_ALBUM_NO_DATA = BASE + 1;
    public static final int SELECT_FROM_ALBUM_FAIL = BASE + 2;

    public static final int TAKE_PICTURE_FAIL = BASE + 3;

    private int code;
    private String message;

    public PictureSelectException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
