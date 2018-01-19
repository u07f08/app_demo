package com.example.flowmahuang.kotlinpractice.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.flowmahuang.kotlinpractice.R;

import java.io.InputStream;

/**
 * Created by flowmaHuang on 2016/11/16.
 */

public class GetThumbnailsTool {
    public static Bitmap getBitmapThumbnails(Uri uri, Context context, int itemSampleSize) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            //first decode , get length and width of image
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            if (in != null) {
                BitmapFactory.decodeStream(in, null, opts);
                in.close();

            }
            int sampleSize = 1;

            if (opts.outWidth > itemSampleSize) {
                sampleSize = (opts.outWidth / itemSampleSize) + 1;
                if (sampleSize > 18) sampleSize = 18;
            }

            //second decode,get thumbnail
            in = context.getContentResolver().openInputStream(uri);
            opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;
            Bitmap bmp = BitmapFactory.decodeStream(in, null, opts);
            in.close();

            return bmp;
        } catch (Exception err) {
            return null;
        }
    }

    public static Bitmap getVideoThumbnails(String path) {
        return ThumbnailUtils.createVideoThumbnail(
                path,
                MediaStore.Images.Thumbnails.MICRO_KIND);
    }

    public static Bitmap getAudioBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.music);
    }
}
