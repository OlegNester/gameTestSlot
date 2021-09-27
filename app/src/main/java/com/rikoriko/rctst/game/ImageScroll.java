package com.rikoriko.rctst.game;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rikoriko.rctst.R;

public class ImageScroll extends FrameLayout {

    private static int ANIMATION_DUR = 150;
    public ImageView current_image, next_image;

    private ImageScrollEnd imageScrollEnd;

    private int last_result = 0, old_value = 0;

    public void setImageScrollEnd(ImageScrollEnd imageScrollEnd) {
        this.imageScrollEnd = imageScrollEnd;
    }

    public ImageScroll(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ImageScroll(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.image_view_scrolling, this);
        current_image = (ImageView)getRootView().findViewById(R.id.resourse_1);
        next_image = (ImageView)getRootView().findViewById(R.id.next_img);

        next_image.setTranslationY(getHeight());
    }

    public void setValueRandom(int img, int rotate_count) {
        current_image.animate().translationY(+getHeight()).setDuration(ANIMATION_DUR).start();
        current_image.setVisibility(VISIBLE);
        next_image.setTranslationY(-next_image.getHeight());
        next_image.animate().translationY(0)
                .setDuration(ANIMATION_DUR)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setImage(current_image, old_value);
                        current_image.setTranslationY(0);

                        if (old_value != rotate_count) {
                            current_image.setVisibility(GONE);
                            setValueRandom(img%12, rotate_count);
                            old_value++;
                        } else {
                            current_image.setVisibility(GONE);
                            last_result = 0;
                            old_value = 0;
                            setImage(next_image, img);
                            imageScrollEnd.scrollEnd(img%12, rotate_count);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void setImage(ImageView image_view, int value) {
        if (value == UtilGame.one)
            image_view.setImageResource(R.drawable.resource_1);
        else if (value == UtilGame.two)
            image_view.setImageResource(R.drawable.resource_2);
        else if (value == UtilGame.three)
            image_view.setImageResource(R.drawable.resource_3);
        else if (value == UtilGame.four)
            image_view.setImageResource(R.drawable.resource_4);
        else if (value == UtilGame.five)
            image_view.setImageResource(R.drawable.resource_5);
        else if (value == UtilGame.six)
            image_view.setImageResource(R.drawable.resource_6);
        else if (value == UtilGame.seven)
            image_view.setImageResource(R.drawable.resource_7);
        else if (value == UtilGame.eight)
            image_view.setImageResource(R.drawable.resource_8);
        else if (value == UtilGame.nine)
            image_view.setImageResource(R.drawable.resource_9);

        image_view.setTag(value);
        last_result = value;
    }

    public int getValue() {
        return Integer.parseInt(next_image.getTag().toString());
    }

}