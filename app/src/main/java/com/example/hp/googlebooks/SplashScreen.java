package com.example.hp.googlebooks;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setLogoTypeFace();
        try {
            splash_thread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make the Splash Screen Activity full screen
     */
    private void makeFullScreen()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
    }

    private void setLogoTypeFace()
    {
        logo = (TextView)findViewById(R.id.logo);
        Typeface typeface_logo = Typeface.createFromAsset(getAssets(), "fonts/coolvetica.ttf");
        logo.setTypeface(typeface_logo);
    }

    /**
     * Forces the app to freeze on the splash screen for 2500 seconds
     */
    void splash_thread() throws InterruptedException {
        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();

    }

    private void doWork()
    {
        try
        { Thread.sleep(2500);
            } catch (Exception e) {
            e.printStackTrace(); }
    }
    /**
     * Handles what happens after the 2500 seconds (Opening the home activity)
     */

    private void startApp()
    {
        startActivity(new Intent(getApplicationContext(),Home.class));
    }
}
