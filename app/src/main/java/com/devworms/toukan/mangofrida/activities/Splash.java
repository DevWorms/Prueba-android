package com.devworms.toukan.mangofrida.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.devworms.toukan.mangofrida.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Nos indica si mostramos el login o si mostramos el tutorial
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
                    Boolean slider = preferences.getBoolean("MostrarSlider", true);
                    if (slider) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("MostrarSlider", false);
                        editor.apply();

                        Intent intent = new Intent(Splash.this, ScreenSlider.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, Login.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
