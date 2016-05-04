// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.devworms.editorial.mango.componentes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.dialogs.AgregarTarjeta;
import com.devworms.editorial.mango.dialogs.WalletActivity;
import com.devworms.editorial.mango.openpay.OpenPayRestApi;
import com.devworms.editorial.mango.fragments.RecetaFragment;
import com.devworms.editorial.mango.fragments.RecetarioFragment;
import com.devworms.editorial.mango.main.StarterApplication;
import com.devworms.editorial.mango.util.Specs;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public final class AdapterRecetarioList extends RecyclerView.Adapter<AdapterRecetarioList.ViewHolder> {

    private List<ParseObject> mItems;
    private String tipoMenu;
    private Activity actividad;
    private List<String>lTipos;


    public AdapterRecetarioList(List<ParseObject> mItems, String tipoMenu, Activity actividad) {
        this.mItems = mItems;
        this.tipoMenu = tipoMenu;
        this.actividad = actividad;


        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {
                FastImageLoader.prefetchImage(parseObject.getString("Url_Imagen"), Specs.IMG_IX_UNBOUNDED);
            }
        }
    }

    public AdapterRecetarioList(List<ParseObject> mItems, List<String>lTipos, Activity actividad) {
        this.mItems = mItems;
        this.lTipos = lTipos;
        this.actividad = actividad;

        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {
                FastImageLoader.prefetchImage(parseObject.getString("Url_Imagen"), Specs.IMG_IX_IMAGE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecetarioList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recetario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);
        holder.objReceta = mItems.get(position);
        holder.setTitulos(mItems.get(position));
        holder.tipoMenu = this.tipoMenu == null ?  lTipos.get(position):this.tipoMenu;
        holder.actividad = actividad;
        holder.mTargetImageView.loadImage(mItems.get(position).getString("Url_Imagen"), spec.getKey());
    }

    //region: Inner class: ViewHolder

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TargetImageView mTargetImageView;
        ParseObject objReceta;
        public String tipoMenu;
        public Activity actividad;
        public TextView tTextViewNombrereceta;
        public TextView textViewPorciones;
        public TextView tTextViewTiempo;
        public ImageView iImageViewDificultad;

        private Dialog dialog;



        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);

            mTargetImageView.setOnClickListener(this);

            tTextViewNombrereceta = (TextView) v.findViewById(R.id.textViewNombrereceta);
            tTextViewTiempo = (TextView) v.findViewById(R.id.textViewTiempo);
            textViewPorciones = (TextView) v.findViewById(R.id.textViewPorciones);

           // iImageViewDificultad = (ImageView) v.findViewById(R.id.textViewNombrereceta);

        }


        public void setTitulos(ParseObject objReceta){
            tTextViewNombrereceta.setText(objReceta.getString("Nombre"));
            tTextViewTiempo.setText("  " + objReceta.getString("Tiempo"));
            textViewPorciones.setText("  " + objReceta.getString("Porciones"));
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mTargetImageView.getContext();
            if (activity != null) {

                final ImageView imageView = (ImageView) v;

                if ( tipoMenu.equals("pago")) {


                    if (ParseUser.getCurrentUser() != null ){
                        consultarSuscripcion(activity, imageView);
                    }else {

                        String titulo = "Inicio de sesión necesario";
                        String mensaje = "Tienes que iniciar sesión para poder suscribirte y acceder a esta receta";



                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));


                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        //MainActivity.this.finish();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }

                }

                else{
                    RecetarioFragment recetario = new RecetarioFragment();
                    recetario.setMenuSeleccionado(objReceta);


                    View drawingView = imageView;
                    drawingView.buildDrawingCache(true);
                    Bitmap imgReceta = drawingView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                    drawingView.destroyDrawingCache();

                    RecetaFragment receta = new RecetaFragment();
                    receta.setObjReceta(objReceta);
                    receta.setImgReceta(imgReceta);

                    activity.getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, receta)
                            .addToBackStack("MenuFragment")
                            .commit();

                }

            }


        }

        public void consultarSuscripcion(final Activity activity, final ImageView imageView){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
            query.whereEqualTo("username", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> clientes, ParseException e) {
                    if (e == null) {

                        if (clientes.size() > 0)
                        {
                            boolean suscripcion = false;

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {


                            Calendar cal = sdf.getCalendar();

                            if(clientes.get(0).getBoolean("Suscrito")){
                                suscripcion = true;
                            }else{
                                //si no esta suscrito quiere decir que posiblemente hizo un pago en tienda
                                if(OpenPayRestApi.validarPagoEnTienda(clientes.get(0))){
                                    suscripcion = true;
                                }
                            }



                            //validamos la caducidad de suscripcion, valido para pago en tienda


                            if(suscripcion){

                                Date dateSuscripcion = null, dateNow = null;
                                if(clientes.get(0).getString("Caducidad") != null && !clientes.get(0).getString("Caducidad").equals(""))
                                {
                                    dateSuscripcion = sdf.parse(clientes.get(0).getString("Caducidad"));//caducidad en la base
                                    Calendar calendar = Calendar.getInstance();
                                    dateNow =  calendar.getTime();
                                }

                                if (dateNow != null && dateSuscripcion != null){
                                    if (dateNow.after(dateSuscripcion)) {
                                        suscripcion = false;
                                        clientes.get(0).put("Suscrito", false);
                                        clientes.get(0).put("Caducidad", "");
                                        clientes.get(0).saveInBackground();

                                    }
                                }else{
                                    suscripcion = false; //quiere decir que no hay caducidad
                                    clientes.get(0).put("Suscrito", false);
                                    clientes.get(0).put("Caducidad", "");
                                    clientes.get(0).saveInBackground();
                                }
                            }


                            } catch (java.text.ParseException e1) {
                                e1.printStackTrace();
                            }


                            if(suscripcion)
                            {

                                RecetarioFragment recetario = new RecetarioFragment();
                                recetario.setMenuSeleccionado(objReceta);


                                final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                                final Bitmap imgReceta = bitmapDrawable.getBitmap();

                                RecetaFragment receta = new RecetaFragment();
                                receta.setObjReceta(objReceta);
                                receta.setImgReceta(imgReceta);

                                activity.getFragmentManager().beginTransaction()
                                        .replace(R.id.actividad, receta)
                                        .addToBackStack("MenuFragment")
                                        .commit();
                            }  else{

                                if(dialog == null){
                                    dialog = new Dialog(activity);
                                    dialog.setCancelable(true);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    //Aqui haces que tu layout se muestre como dialog

                                    dialog.setContentView(R.layout.dialog_producto);
                                    ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            dialog.cancel();
                                            dialog.closeOptionsMenu();


                                        }
                                    });

                                    ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            dialog.cancel();
                                            continuar();

                                        }
                                });
                                }

                                if(!dialog.isShowing()) {
                                    dialog.show();
                                }

                            }

                        } else {


                            if (clientes.size() >0){
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                                query.whereEqualTo("cliente", clientes.get(0));
                                query.findInBackground(new FindCallback<ParseObject>() {

                                    public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                        if (e == null) {



                                            if (listaTarjetas.size()>0) {

                                                Intent intent = new Intent(activity.getApplicationContext(), WalletActivity.class);
                                                activity.startActivity(intent);

                                            }
                                            else{

                                                if(dialog == null){
                                                    dialog = new Dialog(activity);
                                                    dialog.setCancelable(true);
                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    //Aqui haces que tu layout se muestre como dialog

                                                    dialog.setContentView(R.layout.dialog_producto);
                                                    ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View view) {

                                                            dialog.cancel();
                                                            dialog.closeOptionsMenu();


                                                        }
                                                    });

                                                    ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View view) {

                                                            dialog.cancel();
                                                            continuar();

                                                        }
                                                    });
                                                }

                                                if(!dialog.isShowing()) {
                                                    dialog.show();
                                                }


                                            }



                                        }
                                    }
                                });
                            }
                            else{

                                if(dialog == null){
                                    dialog = new Dialog(activity);
                                    dialog.setCancelable(true);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    //Aqui haces que tu layout se muestre como dialog

                                    dialog.setContentView(R.layout.dialog_producto);
                                    ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            dialog.cancel();
                                            dialog.closeOptionsMenu();


                                        }
                                    });

                                    ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            dialog.cancel();
                                            continuar();

                                        }
                                    });
                                }

                                if(!dialog.isShowing()) {
                                    dialog.show();
                                }

                            }


                        }
                        Log.d("score", "Retrieved " + clientes.size() + " scores");

                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }

                public void continuar() {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setCancelable(true);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //Aqui haces que tu layout se muestre como dialog
                    dialog.setContentView(R.layout.dialog_forma_pago);
                    ((Button) dialog.findViewById(R.id.btn_addTarjeta)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            addtarjetacred();

                        }
                    });
                    ((Button) dialog.findViewById(R.id.btn_pagTienda)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            dialog.cancel();
                            pagarTienda();

                        }
                    });

                    dialog.show();
                }

                public void addtarjetacred() {

                    Intent intent = new Intent(activity.getApplicationContext(), AgregarTarjeta.class);
                    activity.startActivity(intent);

                }

                public void pagarTienda() {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setCancelable(true);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //Aqui haces que tu layout se muestre como dialog
                    dialog.setContentView(R.layout.dialog_pago_tienda);


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
                    query.whereEqualTo("username", ParseUser.getCurrentUser());
                    query.findInBackground(new FindCallback<ParseObject>() {

                        public void done(List<ParseObject> listaClientes, ParseException e) {
                            if (e == null) {

                                ParseObject objCliente = null;
                                if (listaClientes.size() > 0) {
                                    objCliente = listaClientes.get(0);
                                    ((EditText) dialog.findViewById(R.id.nombreEt)).setText(objCliente.getString("nombre"));
                                    ((EditText) dialog.findViewById(R.id.correoEt)).setText(objCliente.getString("email"));
                                    ((EditText) dialog.findViewById(R.id.telefonoEt)).setText(objCliente.getString("numero"));
                                } else {
                                    objCliente = OpenPayRestApi.crearCliente(
                                            ((EditText) dialog.findViewById(R.id.nombreEt)).getText().toString(),
                                            ((EditText) dialog.findViewById(R.id.correoEt)).getText().toString(),
                                            ((EditText) dialog.findViewById(R.id.telefonoEt)).getText().toString(),
                                            true
                                    );
                                }


                                //se asignan las operaciones normales

                                ((Button) dialog.findViewById(R.id.btn_regre)).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        dialog.cancel();
                                        continuar();

                                    }
                                });

                                final ParseObject finalObjCliente = objCliente;
                                ((Button) dialog.findViewById(R.id.btn_pagar_tienda)).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        String[] resultados = OpenPayRestApi.pagarEnTienda(StarterApplication.PRECIO_MEMBRESIA, finalObjCliente, activity); // att2t0hjg6qricd6ezgc corresponde al id de un cliente de openpay de la cuenta de openpya para desarrollo de devworms

                                        ((TextView) dialog.findViewById(R.id.lb_barCode)).setText(resultados[1]);


                                        TargetImageView imgBar = ((TargetImageView) dialog.findViewById(R.id.img_barcode));
                                        //  dialog.cancel();
                                        FastImageLoader.prefetchImage(resultados[0], Specs.IMG_IX_UNBOUNDED);
                                        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);
                                        imgBar.loadImage(resultados[0], spec.getKey());

                                        //  dialog.cancel();
                                    }
                                });


                            }
                        }
                    });


                        dialog.show();
                    }

                });

            }

        }

                //pagos


                }