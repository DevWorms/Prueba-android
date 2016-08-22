package com.devworms.toukan.mango.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.dialogs.Usuario;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sergio on 21/08/16.
 */
public class RegistroActivity extends AppCompatActivity{

    private EditText txtCorreo, txtPass, txtPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }


    public void loguearConFacebook(View view)
    {
        if(!isNetworkAvailable()){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));

            // set title
            alertDialogBuilder.setTitle("Sin acceso a internet");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Necesita conexión internet para poder iniciar sesión")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            return;
        }

        List<String> permissions = Arrays.asList("user_birthday", "user_location", "user_friends", "email", "public_profile");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions , new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");

                    ligarFBconParse(user);
                    Intent intent = new Intent(RegistroActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    ligarFBconParse(user);
                    Intent intent = new Intent(RegistroActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });



    }



    private void ligarFBconParse(final ParseUser user)
    {
        List<String> permissions = Arrays.asList("user_birthday", "user_location", "user_friends", "email", "public_profile");

        if (!ParseFacebookUtils.isLinked(user)) {
            ParseFacebookUtils.linkWithReadPermissionsInBackground(user, this, permissions, new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseFacebookUtils.isLinked(user)) {
                        Log.d("MyApp", "Woohoo, user logged in with Facebook!");
                    }
                }
            });
        }

    }


    public void loguearConMail(View view)
    {
        if(!isNetworkAvailable()){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));

            // set title
            alertDialogBuilder.setTitle("Sin acceso a internet");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Necesita conexión internet para poder iniciar sesión")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            return;
        }

        final Activity activity = this;
        String userName = "";
        String pass = "";
        TextView usuario = ((TextView)findViewById(R.id.editTextMail) );
        TextView password = ((TextView)findViewById(R.id.editTextContrasena) );

        if (usuario.getText().toString() == null ||  password.getText() == null ||
                usuario.getText().toString().toString().equals("") ||  password.getText().toString().equals("")) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));

            // set title
            alertDialogBuilder.setTitle("Faltan datos por llenar");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Debe ingresar correo y contraseña")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else {

            userName = usuario.getText().toString();
            pass = password.getText().toString();



            ParseUser.logInInBackground(userName, pass, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));


                        // set title
                        alertDialogBuilder.setTitle("Error");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Revise sus datos o su conexion a internet")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                }
            });


        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
