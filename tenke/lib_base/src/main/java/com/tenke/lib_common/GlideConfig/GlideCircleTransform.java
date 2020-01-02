package com.tenke.lib_common.GlideConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class GlideCircleTransform extends BitmapTransformation {
    private static final String ID = GlideCircleTransform.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final int mWidth;
    private final int mColor;
    private Paint mBorderPaint;

    public GlideCircleTransform(int width,int color) {
        mWidth = width;
        mColor = color;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);


       if(mBorderPaint == null){
           mBorderPaint = new Paint();
           mBorderPaint.setStyle(Paint.Style.STROKE);
           mBorderPaint.setStrokeWidth(mWidth);
           mBorderPaint.setColor(mColor);
           mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
           mBorderPaint.setAntiAlias(true);
       }
        float r = size / 2f;
        float r1=(size-2*4)/2f;
        canvas.drawCircle(r, r, r1, paint);
        canvas.drawCircle(r,r,r1, mBorderPaint);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlideCircleTransform) {
            GlideCircleTransform other = (GlideCircleTransform) obj;
            return mColor == other.mColor && mWidth == other.mWidth;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(),
                Util.hashCode(mColor,mWidth));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt(mColor&mWidth).array();
        messageDigest.update(radiusData);

    }


}
