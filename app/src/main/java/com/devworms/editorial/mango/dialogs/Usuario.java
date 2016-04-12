package com.devworms.editorial.mango.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.Login;
import com.devworms.editorial.mango.activities.MainActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by loajrla on 10/04/16.
 */
public class Usuario{

    private EditText txtCorreo, txtPass, txtPassConfirm;

    public Usuario(EditText txtCorreo,EditText txtPass,EditText txtPassConfirm){
        this.txtCorreo = txtCorreo;
        this.txtPass = txtPass;
        this.txtPassConfirm = txtPassConfirm;

    }


    public void recuperarContrasena(final Activity activity, final Dialog dialogo){

        ParseUser.requestPasswordResetInBackground(this.txtCorreo.getText().toString(), new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));

                String mensaje = "";

                if (e == null) {
                    String titulo = "Reestablecer cuenta";
                    mensaje = "Le hemos mandado un correo electronico";


                    // set title
                    alertDialogBuilder.setTitle(titulo);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(mensaje)
                            .setCancelable(false)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialogo.cancel();
                                }
                            });


                } else {
                    String titulo = "Error al reestablecer cuenta";
                    if (e.getCode() == 125) {

                        mensaje = "Correo inválido";
                    }
                    else{
                        mensaje = "Ha ocurrido un error, revise sus datos o su conexión a internet";
                    }



                    // set title
                    alertDialogBuilder.setTitle(titulo);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(mensaje)
                            .setCancelable(false)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                }
                            });

                }



                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

    }

    public void nuevoUsuario (final Activity activity, final Dialog dialogo){


        if (txtPass.getText().toString().equals(txtPassConfirm.getText().toString())) {
            ParseUser user = new ParseUser();
            user.setUsername(txtCorreo.getText().toString().toLowerCase());
            user.setPassword(txtPass.getText().toString());
            user.setEmail(txtCorreo.getText().toString().toLowerCase());
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));

                    String titulo = "";
                    String mensaje = "";

                    if (e == null) {
                        titulo = "Registro exitoso";
                        mensaje = "Inicia sesión con este correo y contraseña creadas";


                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        ParseUser.logOut();
                                        dialogo.cancel();
                                    }
                                });


                    } else {
                        titulo = "Error";
                        mensaje = "Revisa que tu correo y pasword ingresados sean correctos";


                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                    }
                                });

                    }



                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));


            // set title
            alertDialogBuilder.setTitle("Error");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Contraseñas no coinciden")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }


    }
}
