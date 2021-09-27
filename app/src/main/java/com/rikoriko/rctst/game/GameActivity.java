package com.rikoriko.rctst.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rikoriko.rctst.R;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements ImageScrollEnd {

    private ImageScroll image, image2, image3, image4, image5;
    private Button btnStart;
    private int count_done = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btnStart = findViewById(R.id.btnStart);

        image = findViewById(R.id.image);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);

        image.setImageScrollEnd(GameActivity.this);
        image2.setImageScrollEnd(GameActivity.this);
        image3.setImageScrollEnd(GameActivity.this);
        image4.setImageScrollEnd(GameActivity.this);
        image5.setImageScrollEnd(GameActivity.this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);

                startSpin(image, 100);
                startSpin(image2, 700);
                startSpin(image3, 1300);
                startSpin(image4, 1600);
                startSpin(image5, 1900);
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), GameMenu.class);
        startActivity(intent);
        finish();
    }*/

    @Override
    public void scrollEnd(int result, int count) {
        if (count_done < 4) {
            count_done++;
        } else {

            count_done = 0;
            btnStart.setVisibility(View.VISIBLE);

            if (image.getValue() == image2.getValue() && image2.getValue() == image3.getValue() && image3.getValue() == image4.getValue()) {

                customToast("You Win!");

            } else if (image.getValue() == image2.getValue() && image.getValue() == image3.getValue() ||
                    image.getValue() == image3.getValue() && image.getValue() == image4.getValue() ||
                    image2.getValue() == image.getValue() && image2.getValue() == image3.getValue() ||
                    image2.getValue() == image3.getValue() && image2.getValue() == image4.getValue() ||
                    image3.getValue() == image.getValue() && image3.getValue() == image2.getValue() ||
                    image3.getValue() == image2.getValue() && image3.getValue() == image4.getValue() ||
                    image4.getValue() == image.getValue() && image4.getValue() == image2.getValue() ||
                    image4.getValue() == image2.getValue() && image4.getValue() == image3.getValue()) {

                customToast("You Win!");

            } else if (image.getValue() == image2.getValue() ||
                    image.getValue() == image3.getValue() ||
                    image.getValue() == image4.getValue() ||
                    image2.getValue() == image.getValue() ||
                    image2.getValue() == image3.getValue() ||
                    image2.getValue() == image4.getValue() ||
                    image3.getValue() == image.getValue() ||
                    image3.getValue() == image2.getValue() ||
                    image3.getValue() == image4.getValue() ||
                    image4.getValue() == image.getValue() ||
                    image4.getValue() == image2.getValue() ||
                    image4.getValue() == image3.getValue()) {

                customToast("You Win!");

            } else {
                customToast("You Lose!");

            }
        }
    }

    private void startSpin(ImageScroll imageView, int time) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setValueRandom(new Random().nextInt(6), new Random().nextInt((15-5)+1)+5);
            }
        }, time);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void customToast(String text) {
        View toastView = getLayoutInflater().inflate(R.layout.activity_toast_custom_view, null);
        TextView customToastText = (TextView) toastView.findViewById(R.id.customToastText);
        customToastText.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0,0);
        toast.show();
    }

}