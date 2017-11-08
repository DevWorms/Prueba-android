package com.devworms.toukan.mangofrida.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devworms.toukan.mangofrida.R;

import static android.content.Context.MODE_PRIVATE;

public class CalificarApp extends Dialog {

    Context context;
    Button btnCalifica, btnCancelar;
    SharedPreferences sp;
    // Constructor por defecto

    public CalificarApp(Context context ) {
        super(context);
        this.context = context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_app);
        btnCalifica = (Button)findViewById(R.id.btnCalificar);
        sp = context.getSharedPreferences("user_data", MODE_PRIVATE);
        btnCalifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = sp.edit();

                e.putBoolean("calificado",true);
                e.apply();

                String url = "https://play.google.com/store/apps/details?id=com.devworms.toukan.mangofrida";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        btnCancelar=(Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
