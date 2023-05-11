package com.example.cookieclicker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int score = 0;
    private int perSec = 0;
    private String SCORE_STRING = "Score: %d";
    private int scoreMultiplier = 1;
    private int upgradeCost = 10;
    private int upgradeLevel = 1;
    private int passiveIncome = 1;
    private Handler handler;
    private TextView scoreText, perSecText, titleText;
    private ImageView coffeeImage;
    private ImageView miniBeanImage;
    private Animation spinInAnimation;
    private Animation spinOutAnimation;
    private Animation zoomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreText = findViewById(R.id.score);
        perSecText = findViewById(R.id.persec);
        coffeeImage = findViewById(R.id.imageView3);
        miniBeanImage = findViewById(R.id.miniBeanImage);

        spinInAnimation = AnimationUtils.loadAnimation(this, R.anim.spin_in);
        spinOutAnimation = AnimationUtils.loadAnimation(this, R.anim.spin_out);

        final Button upgradeButton = findViewById(R.id.upgrade1);
        upgradeButton.setText("Upgrade: +" + upgradeLevel + " PPS for " + upgradeCost + " coffee");


        coffeeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score+=1;
                scoreText.setText("Score: " + score);
                animateCoffee();
            }
        });

        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = findViewById(R.id.textAddOne);
                tv.setText("+1");
                if (score >= upgradeCost) {
                    score -= upgradeCost;
                    upgradeLevel++;
                    passiveIncome += 5;
                    upgradeCost += upgradeLevel;
                    upgradeButton.setText("Upgrade: +" + upgradeLevel + " PPS for " + upgradeCost + " coffee");
                    scoreText.setText("Score: " + score);
                    perSecText.setText("Per second: " + perSec);
                    miniBeanImage.setVisibility(View.VISIBLE);
                    miniBeanImage.startAnimation(spinInAnimation);
                    Animation floatUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up);
                    miniBeanImage.startAnimation(floatUp);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            miniBeanImage.startAnimation(spinOutAnimation);
                            miniBeanImage.setVisibility(View.INVISIBLE);
                        }
                    }, spinInAnimation.getDuration());


                    // Update the Purchased Upgrades TextView
                    TextView purchasedUpgradesTextView = findViewById(R.id.textView5);
                    String currentText = purchasedUpgradesTextView.getText().toString();
                    String newUpgradeName = "Upgrade " + upgradeLevel;
                    if (currentText.isEmpty()) {
                        purchasedUpgradesTextView.setText(newUpgradeName);
                    } else {
                        purchasedUpgradesTextView.setText(currentText + ", " + newUpgradeName);
                    }
                }
            }
        });

        Button score2xButton = findViewById(R.id.upgrade2);
        score2xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (score >= 500) {
                    score -= 500;
                    TextView tv = findViewById(R.id.textAddOne);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("+2");
                    scoreText.setText(String.format(Locale.getDefault(), SCORE_STRING, score));
                    // Multiply score by 2 for 5 seconds
                    scoreMultiplier = 2;
                    score *=2;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText("+2");
                            scoreMultiplier = 1;
                        }
                    }, 5000);
                }
            }
        });



        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (upgradeLevel > 1) {
                    score += passiveIncome;
                   perSec = passiveIncome;
                    scoreText.setText("Score: " + score);
                    perSecText.setText("Per second: " + perSec);
                    // animatePerSec();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current score value
        outState.putInt("score", score);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Save the current score value
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("score", score);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore score if it was previously saved
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        score = prefs.getInt("score", 0);
        scoreText.setText(String.format(Locale.getDefault(), SCORE_STRING, score));
    }

    private void animateCoffee() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView tv = findViewById(R.id.textAddOne);
                tv.setVisibility(View.VISIBLE);
                // Set random x coordinate for "+1"
                int x = (int) (Math.random() * (coffeeImage.getWidth()));
                tv.setX(x);
                Animation floatUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up);
                tv.startAnimation(floatUp);
                floatUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Do nothing
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Do nothing
                    }
                });
                scoreText.setText("Score: " + score);
                // Add the animated score to the current score
                score += passiveIncome;
                scoreText.setText("Score: " + score);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        coffeeImage.startAnimation(shake);
    }



}
