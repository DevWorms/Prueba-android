package com.devworms.toukan.mango.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.main.StarterApplication;
import com.devworms.toukan.mango.openpay.OpenPayRestApi;
import com.devworms.toukan.mango.util.Specs;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.util.List;

/**
 * Created by loajrla on 06/03/16.
 */
public class WalletActivity extends Activity {

    TextView holderNameEt, cardNumberEt, brandEt;


    ParseObject objCliente;
    ParseObject objTarjeta;

    public void initControls(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        objCliente = listaClientes.get(0);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", objCliente);
                        query.findInBackground(new FindCallback<ParseObject>() {

                            public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                if (e == null) {
                                    if (listaTarjetas.size()>0) {
                                        objTarjeta = listaTarjetas.get(0);

                                        holderNameEt = ((TextView) findViewById(R.id.txt_nombre_tarje));
                                        cardNumberEt = ((TextView) findViewById(R.id.txt_numero_tarjeta));
                                        brandEt = ((TextView) findViewById(R.id.txt_tipo_tarje));

                                        holderNameEt.setText(objCliente.getString("nombre"));
                                        brandEt.setText(objTarjeta.getString("brand"));
                                        cardNumberEt.setText(objTarjeta.getString("numero"));




                                    }


                                }
                            }
                        });
                    }
                    else{
                        //Imprimir un error diciendo que no hay ningun cliente y no se puede
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
        setContentView(R.layout.dialog_descripcion_compra);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        initControls();


    }



    public void pagarConTarjeta(View view){
        OpenPayRestApi.suscribirsePlan(this);
        this.finish();
    }

    public void pagarEnTienda(View view){
        final Dialog dialog = new Dialog(this);
        final Activity activity = this;
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog
        dialog.setContentView(R.layout.dialog_pago_tienda);


        ((EditText) dialog.findViewById(R.id.nombreEt)).setText(objCliente.getString("nombre"));
        ((EditText) dialog.findViewById(R.id.correoEt)).setText(objCliente.getString("email"));
        ((EditText) dialog.findViewById(R.id.telefonoEt)).setText(objCliente.getString("numero"));


        ((Button) dialog.findViewById(R.id.btn_regre)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog.cancel();


            }
        });

        ((Button) dialog.findViewById(R.id.btn_pagar_tienda)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String[] resultados = OpenPayRestApi.pagarEnTienda(StarterApplication.PRECIO_MEMBRESIA, objCliente, activity); // att2t0hjg6qricd6ezgc corresponde al id de un cliente de openpay de la cuenta de openpya para desarrollo de devworms

                System.out.println(resultados[0]);
                ((TextView) dialog.findViewById(R.id.lb_barCode)).setText(resultados[1]);


                TargetImageView imgBar = ((TargetImageView) dialog.findViewById(R.id.img_barcode));
                //  dialog.cancel();
                FastImageLoader.prefetchImage(resultados[0], Specs.IMG_IX_UNBOUNDED);
                ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);
                imgBar.loadImage(resultados[0], spec.getKey());

            }
        });
        dialog.show();
    }


}
