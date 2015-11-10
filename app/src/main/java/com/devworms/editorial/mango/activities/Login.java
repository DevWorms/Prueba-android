package com.devworms.editorial.mango.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.facebook.appevents.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergio on 22/10/15.
 */
public class Login extends AppCompatActivity {

    private TextView tCorreo;
    private boolean registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        try {


            tCorreo = (TextView) findViewById(R.id.correo);
            registrar = false;


            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // show the signup or login screen


            }
        }
        catch(Exception ex)
        {

        }
    }

    public void loguearConMail(View view)
    {
        String userName=( (TextView)findViewById(R.id.usuario) ).getText().toString();
        String pass=( (TextView)findViewById(R.id.password) ).getText().toString();

        if(!registrar) {
            ParseUser.logInInBackground(userName, pass, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                    }
                }
            });
        }
        else
        {
            String mail=( (TextView)findViewById(R.id.correo) ).getText().toString();

            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(pass);
            user.setEmail(mail);

            // other fields can be set just like with ParseObject
            //user.put("phone", "650-253-0000");

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        registrar=false;
                    } else {
                        Toast.makeText(getApplicationContext(),"Ocurrio error al registrar",Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    public  void continuar(View view)
    {
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void registrarUsuario(View view)
    {
        tCorreo.setVisibility(View.VISIBLE);

        registrar=true;

    }

    public void loguearConFacebook(View view)
    {
        List<String> permissions = Arrays.asList("user_birthday", "user_location", "user_friends", "email", "public_profile");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions , new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });


        //******Redes sociales*/

       /* ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void loguearConTwitter(View view)
    {

        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                } else {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("MyApp", "User logged in through Twitter!");
                }
            }
        });

    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }*/
}
