package com.devworms.editorial.mango.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.devworms.editorial.mango.R;

/**
 * Created by loajrla on 06/03/16.
 */
public class WalletActivity extends Activity {

    //EditText holderNameEt, cardNumberEt, cvvEt, monthEt, yearEt, emailEt, numeroEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setFinishOnTouchOutside(false);
        setContentView(R.layout.dialog_descripcion_compra);


      /*  holderNameEt = ((EditText) this.findViewById(R.id.txt_name_user));
        cardNumberEt = ((EditText) this.findViewById(R.id.txt_numero_tarjeta));
        cvvEt = ((EditText) this.findViewById(R.id.txt_cvv));
        monthEt = ((EditText) this.findViewById(R.id.txt_mm));
        yearEt = ((EditText) this.findViewById(R.id.txt_aa));
        emailEt = ((EditText) this.findViewById(R.id.txt_email));
        numeroEt = ((EditText) this.findViewById(R.id.txt_num));*/
    }

}
