package com.tenke.lib_common.GlideConfig;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tenke.baselibrary.R;


public class GlideBinding {

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView view, String url) {
        GlideApp.with(view).load(url).into(view);
    }

    @BindingAdapter({"imageUrl", "placeholderImage"})
    public static void setImageUrl(ImageView view, String url, Drawable placeholder) {
        GlideApp.with(view)
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }

    @BindingAdapter({"imageUrl", "placeholderImage", "roundAsCircle"})
    public static void setImageUrl(ImageView view, String url, Drawable placeholder, boolean roundAsCircle) {
        GlideApp.with(view)
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }

    @BindingAdapter({"imageUrl", "placeholderImage", "roundedCornerRadius"})
    public static void setImageUrl(ImageView view, String url, Drawable placeholder, float roundedCornerRadius) {
        GlideApp.with(view)
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int) roundedCornerRadius)))
                .into(view);
    }

    @BindingAdapter({"roundedCornerRadius"})
    public static void setImageUrl(ImageView view, float roundedCornerRadius) {
        GlideApp.with(view)
                .load(view.getDrawable())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int) roundedCornerRadius)))
                .into(view);
    }

    @BindingAdapter({"imageUrl", "placeholderImage", "roundedCornerRadius", "roundAsCircle", "hasCircleBorder"})
    public static void setImageUrl(ImageView view, String url, Drawable placeholder, float roundedCornerRadius, boolean roundAsCircle, boolean hasCircleBorder) {

        GlideRequest<Drawable> error = GlideApp.with(view)
                .load(url)
                .placeholder(placeholder)
                .error(placeholder);
        if (!roundAsCircle) {
            error.apply(RequestOptions.bitmapTransform(new RoundedCorners((int) roundedCornerRadius)))
                    .into(view);
        } else {
            if (!hasCircleBorder) {
                error.apply(RequestOptions.circleCropTransform())
                        .into(view);
            } else {
                int width = view.getContext().getResources().getDimensionPixelOffset(R.dimen.cover_item_image_border_width);
                int color = view.getContext().getResources().getColor(R.color.gray_484848);
                error.transform(new GlideCircleTransform(width, color))
                        .into(view);
            }
        }
    }


}
