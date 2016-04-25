package com.devworms.editorial.mango.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;

//import com.parse.Parse;


/**
 * Created by DevWorms on 21/10/15.
 */
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    //Nos indica siu mostramos el login o si mostramos el tutorial
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
                    Boolean slider = preferences.getBoolean("MostrarSlider", true );
                    if(slider)
                    {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("MostrarSlider",false);
                        editor.apply();

                        Intent intent = new Intent(Splash.this,ScreenSlider.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {

                        Intent intent = new Intent(Splash.this,Login.class);
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
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}
