package com.devworms.editorial.mango.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.dialogs.Usuario;
import com.facebook.appevents.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergio on 22/10/15.
 */
public class Login extends AppCompatActivity {


    private boolean registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        try {


            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            registrar = false;


            if (!isNetworkAvailable()){
                ParseUser.logOut();
            }

            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null ) {
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
        TextView usuario = ((TextView)findViewById(R.id.usuario) );
        TextView password = ((TextView)findViewById(R.id.password) );

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
                        Intent intent = new Intent(Login.this, MainActivity.class);
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

    public  void continuar(View view)
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

        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void cancelar(View view){
        this.finish();
    }

    public void recuperarContrasena(View view){
        final Activity actividad = this;
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog

        dialog.setContentView(R.layout.dialog_recuperar_contrasena);
        ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
                dialog.closeOptionsMenu();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {
            private EditText txtCorreo;

            @Override
            public void onClick(View view) {

                txtCorreo = (EditText)dialog.findViewById(R.id.txtCorreo);

                if (txtCorreo.getText() == null || txtCorreo.getText().toString().equals("") ) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                    // set title
                    alertDialogBuilder.setTitle("Error");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Debe ingresar un correo")
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

                    Usuario usuario = new Usuario(txtCorreo, null, null);
                    usuario.recuperarContrasena(actividad,dialog);

                }

            }
        });

        dialog.show();
    }

    public void registrarUsuario(View view)
    {

        final Activity actividad = this;
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog

        dialog.setContentView(R.layout.dialog_usuario);
        ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
                dialog.closeOptionsMenu();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {
            private EditText txtCorreo, txtPass, txtPassConfirm;

            @Override
            public void onClick(View view) {

                txtCorreo = (EditText)dialog.findViewById(R.id.txtCorreo);
                txtPass = (EditText)dialog.findViewById(R.id.txtCorreo);
                txtPassConfirm = (EditText)dialog.findViewById(R.id.txtCorreo);

                if (txtCorreo.getText() == null || txtPass.getText() == null || txtCorreo.getText().toString().equals("") || txtPass.getText().toString().equals("") ) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));


                    // set title
                    alertDialogBuilder.setTitle("Error");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Debe llenar los datos")
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

                    Usuario usuario = new Usuario(txtCorreo, txtPass, txtPassConfirm);
                    usuario.nuevoUsuario(actividad,dialog);

                }

            }
        });

        dialog.show();
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

                    ligarFBconParse(user);
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    ligarFBconParse(user);
                    Intent intent = new Intent(Login.this,MainActivity.class);
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
                    ligarConTwitter(user);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                } else {
                    ligarConTwitter(user);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("MyApp", "User logged in through Twitter!");
                }
            }
        });

    }

    private void ligarConTwitter(final ParseUser user)
    {
        if (!ParseTwitterUtils.isLinked(user)) {
            ParseTwitterUtils.link(user, this, new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseTwitterUtils.isLinked(user)) {
                        Log.d("MyApp", "Woohoo, user logged in with Twitter!");
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
