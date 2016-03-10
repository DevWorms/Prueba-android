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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.dialogs.AgregarTarjeta;
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

import java.util.List;


public final class AdapterRecetarioList extends RecyclerView.Adapter<AdapterRecetarioList.ViewHolder> {

    private List<ParseObject> mItems;
    private String tipoMenu;


    public AdapterRecetarioList(List<ParseObject> mItems, String tipoMenu) {
        this.mItems = mItems;
        this.tipoMenu = tipoMenu;

        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {
                FastImageLoader.prefetchImage(parseObject.getString("Url_Imagen"), Specs.IMG_IX_UNBOUNDED);
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
        holder.tipoMenu = this.tipoMenu;

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


        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);

            mTargetImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mTargetImageView.getContext();
            if (activity != null) {

                final ImageView imageView = (ImageView) v;

                if ( tipoMenu.equals("pago")) {
                    consultarSuscripcion(activity, imageView);
                }
                else{
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

                }

            }


        }

        public void consultarSuscripcion(final Activity activity, final ImageView imageView){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
            query.whereEqualTo("username", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> clientes, ParseException e) {
                    if (e == null) {

                        if (clientes.size() > 0 && clientes.get(0).getBoolean("Suscrito")) {

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

                        } else {
                            final Dialog dialog = new Dialog(activity);
                            dialog.setCancelable(false);
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

                            dialog.show();
                        }
                        Log.d("score", "Retrieved " + clientes.size() + " scores");

                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }

                public void continuar() {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setCancelable(false);
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
                    dialog.setCancelable(false);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //Aqui haces que tu layout se muestre como dialog
                    dialog.setContentView(R.layout.dialog_pago_tienda);
                    ((Button) dialog.findViewById(R.id.btn_regre)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            dialog.cancel();
                            continuar();

                        }
                    });

                    ((Button) dialog.findViewById(R.id.btn_pagar_tienda)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String[] resultados = OpenPayRestApi.pagarEnTienda(50, "2016-03-20T13:45:00", "att2t0hjg6qricd6ezgc"); // att2t0hjg6qricd6ezgc corresponde al id de un cliente de openpay de la cuenta de openpya para desarrollo de devworms

                            System.out.println(resultados[0]);
                            ((TextView) dialog.findViewById(R.id.lb_barCode)).setText(resultados[1]);

                            //  dialog.cancel();
                        }
                    });
                    dialog.show();
                }

            });

        }

    }

    //pagos




}