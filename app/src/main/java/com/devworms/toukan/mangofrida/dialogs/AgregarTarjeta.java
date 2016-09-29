package com.devworms.toukan.mangofrida.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.openpay.OpenPayRestApi;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import mx.openpay.android.model.Card;
import mx.openpay.android.validation.CardValidator;


/**
 * Created by Vale on 03/02/16.
 */
public class AgregarTarjeta extends Activity {

    EditText holderNameEt, cardNumberEt, cvvEt, monthEt, yearEt;
    //emailEt, numeroEt;
    ParseObject objCliente;

    public void initControls(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {

                    if (listaClientes.size()>0) {
                        objCliente = listaClientes.get(0);
                        holderNameEt.setText(objCliente.getString("nombre"));
                        //emailEt.setText(objCliente.getString("email"));
                        //numeroEt.setText(objCliente.getString("numero"));
                    }
                    Log.d("score", "Retrieved scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setFinishOnTouchOutside(false);
        setContentView(R.layout.dialog_addtarjeta);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        holderNameEt = ((EditText) this.findViewById(R.id.txt_name_user));
        cardNumberEt = ((EditText) this.findViewById(R.id.txt_numero_tarjeta));
        cvvEt = ((EditText) this.findViewById(R.id.txt_cvv));
        monthEt = ((EditText) this.findViewById(R.id.txt_mm));
        yearEt = ((EditText) this.findViewById(R.id.txt_aa));
       // emailEt = ((EditText) this.findViewById(R.id.txt_email));
        //numeroEt = ((EditText) this.findViewById(R.id.txt_num));
        initControls();

    }

    public void addTarjeta(View view) {


        final Card card = new Card();
        boolean isValid = true;

        final String holderName = holderNameEt.getText().toString();
        card.holderName(holderName);
        if (!CardValidator.validateHolderName(holderName)) {
            holderNameEt.setError("Titular es requerido");
            isValid = false;
        }

        final String cardNumber = cardNumberEt.getText().toString();
        card.cardNumber(cardNumber);
        if (!CardValidator.validateNumber(cardNumber)) {
            cardNumberEt.setError("Número inválido");
            isValid = false;
        }

        final String cvv = cvvEt.getText().toString();
        card.cvv2(cvv);
        if (!CardValidator.validateCVV(cvv, cardNumber)) {
            cvvEt.setError("Código de seguridad inválido");
            isValid = false;
        }

        final Integer month = this.getInteger(monthEt.getText().toString());
        card.expirationMonth(month);
        final Integer year = this.getInteger(yearEt.getText().toString());
        card.expirationYear(year);
        if (!CardValidator.validateExpiryDate(month, year)) {
            monthEt.setError("Mes inválido");
            yearEt.setError("Año inválido");
            isValid = false;
        }

        if (isValid) {
            OpenPayRestApi.crearClienteConTarjeta(card, "", "",objCliente, this);
            finish();
            clearData();

        }

    }

    public void cancelar(View view){
        finish();
    }

    private void clearData() {

        holderNameEt.setText("");
        cardNumberEt.setText("");
        cvvEt.setText("");
        monthEt.setText("");
        yearEt.setText("");

    }

    private Integer getInteger(final String number) {
        try {
            return Integer.valueOf(number);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }


}
