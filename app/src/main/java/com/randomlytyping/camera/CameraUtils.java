/*
 * Copyright 2014 Randomly Typing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.randomlytyping.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * The CameraUtils class contains several static utility methods for working with the hardware
 * {@link android.hardware.Camera} and its data.
 * <p/>
 * Created by Huyen Tue Dao on 04/27/14.
 *
 * @author Huyen Tue Dao
 */
public class CameraUtils {
    /**
     * Class tag for logging.
     */
    @SuppressWarnings("unused")
    private static final String TAG = "CameraUtils";

    /**
     * Creates a {@link android.graphics.Bitmap} from raw image byte data. This will usually come
     * from a {@link android.hardware.Camera.PictureCallback} returning byte data from the {@link
     * android.hardware.Camera}.
     *
     * @param data   Image data as a byte array.
     * @param width  Requested width for the created {@link android.graphics.Bitmap}.
     * @param height Requested height for the created {@link android.graphics.Bitmap}.
     *
     * @return A {@link android.graphics.Bitmap} containing the given image data best fit to a given
     * width and height.
     */
    public static Bitmap bitmapFromRawBytes(byte[] data, int width, int height) {
        /*  Decode the dimensions of the bitmap data so we can determine a sample size that fits the
        requested width and height. */
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        final int actualWidth = options.outWidth;
        final int actualHeight = options.outHeight;

        // Calculate the sample size as power of 2 that will satisfy the requested width and height.
        int inSampleSize = 1;
        if (actualWidth > width || actualHeight > height) {
            int shift = 0;
            while ((actualWidth >> shift + 1) > width && (actualHeight >> shift + 1) > height) {
                shift++;
            }
            if (shift > 0) {
                inSampleSize <<= shift;
            }
        }

        /*  Reset BitmapFactory options to actually decode the byte array at the calculated sample
        size. */
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }


    public static void getSensorCoordinates(float[] coordinates, Rect viewCoordinatesRange,
                                            int displayOrientation, boolean frontFacing) {
        final Matrix matrix = new Matrix();
        matrix.setTranslate(-viewCoordinatesRange.left, -viewCoordinatesRange.top);
        matrix.postScale(frontFacing ? -1 : 1, 1);
        matrix.postScale(2000f / viewCoordinatesRange.width(), 2000f / viewCoordinatesRange.height());
        matrix.postTranslate(-1000f, -1000f);
        matrix.postRotate(-displayOrientation);
        matrix.mapPoints(coordinates);
    }

    public static void getViewCoordinates(Point coordinates, int displayOrientation,
                                          boolean frontFacing, Rect viewCoordinatesRange) {
        float[] coordinateArray = {coordinates.x, coordinates.y};
        getViewCoordinates(coordinateArray, displayOrientation, frontFacing,
                viewCoordinatesRange);
        coordinates.x = Math.round(coordinateArray[0]);
        coordinates.y = Math.round(coordinateArray[1]);
    }

    public static void getViewCoordinates(float[] coordinates, int displayOrientation,
                                          boolean frontFacing, Rect viewCoordinatesRange) {
        final Matrix matrix = new Matrix();
        matrix.setScale(frontFacing ? -1 : 1, 1);
        matrix.postRotate(displayOrientation);
        final int width = viewCoordinatesRange.width();
        final int height = viewCoordinatesRange.height();
        matrix.postScale(width / 2000f, height / 2000f);
        matrix.postTranslate(width * 0.5f, height * 0.5f);
        matrix.mapPoints(coordinates);
    }


    //
    // Constructor
    //

    /**
     * Private constructor for type safety.
     */
    private CameraUtils() {
    }
}
