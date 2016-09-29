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

package com.devworms.toukan.mangofrida.componentes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
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

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.activities.Splash;
import com.devworms.toukan.mangofrida.dialogs.AgregarTarjeta;
import com.devworms.toukan.mangofrida.dialogs.WalletActivity;
import com.devworms.toukan.mangofrida.openpay.OpenPayRestApi;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.Specs;
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
    private List<String> lTipos;


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

    public AdapterRecetarioList(List<ParseObject> mItems, List<String> lTipos, Activity actividad) {
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

        holder.tipoMenu = this.tipoMenu == null ? lTipos.get(position) : this.tipoMenu;
        holder.actividad = actividad;
        holder.mTargetImageView.loadImage(mItems.get(position).getString("Url_Imagen"), spec.getKey());
        holder.setTitulos(mItems.get(position));
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


            iImageViewDificultad = (ImageView) v.findViewById(R.id.imageView12);

        }


        public void setTitulos(ParseObject objReceta) {
            tTextViewNombrereceta.setText(objReceta.getString("Nombre"));
            tTextViewTiempo.setText("  " + objReceta.getString("Tiempo"));
            textViewPorciones.setText("  " + objReceta.getString("Porciones"));


            String dificultad = objReceta.getString("Nivel");


            int imageresource = 0;
            switch (dificultad) {

                case "Principiante":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor1", "drawable", actividad.getPackageName());

                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                case "Intermedio":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor2", "drawable", actividad.getPackageName());
                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                case "Avanzado":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor3", "drawable", actividad.getPackageName());
                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                default:
                    break;
            }


        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mTargetImageView.getContext();
            if (activity != null) {

                final ImageView imageView = (ImageView) v;

                if (tipoMenu.equals("pago")) {


                    if (ParseUser.getCurrentUser() != null) {
                        consultarSuscripcion(activity, imageView);
                    } else {

                        String titulo = "Inicio de sesión necesario";
                        String mensaje = "Tienes que iniciar sesión para poder suscribirte y acceder a esta receta";


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));


                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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

                } else {
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

        public void consultarSuscripcion(final Activity activity, final ImageView imageView) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
            query.whereEqualTo("username", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> clientes, ParseException e) {
                    if (e == null) {

                        if (clientes.size() > 0) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Calendar cal = sdf.getCalendar();

                            if (clientes.get(0).getBoolean("Suscrito")) {
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
                            else{


                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                                query.whereEqualTo("cliente", clientes.get(0));
                                query.findInBackground(new FindCallback<ParseObject>() {

                                    public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                        if (e == null) {


                                            if (listaTarjetas.size() > 0) {

                                                Intent intent = new Intent(activity.getApplicationContext(), WalletActivity.class);
                                                activity.startActivity(intent);

                                            } else {

                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                                Boolean slider = preferences.getBoolean("Mostrardiasprueba", true );
                                                if(slider) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putBoolean("Mostrardiasprueba", false);
                                                    editor.apply();

                                                    mostrarAnuncioDiasPrueba();


                                                }else{

                                                    addtarjetacred();
                                                }

                                            }
                                        }
                                    }
                                });



                            }

                        } else {
                          //////////

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                            Boolean slider = preferences.getBoolean("Mostrardiasprueba", true );
                            if(slider) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("Mostrardiasprueba", false);
                                editor.apply();

                                mostrarAnuncioDiasPrueba();


                                if (!dialog.isShowing()) {
                                    dialog.show();
                                }
                            }
                            else{

                                addtarjetacred();
                            }

                            ///////////
                        }
                    }
                }

                public void mostrarAnuncioDiasPrueba(){
                    if (dialog == null) {
                        dialog = new Dialog(activity);
                        dialog.setCancelable(true);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //Aqui haces que tu layout se muestre como dialog

                        dialog.setContentView(R.layout.dialog_producto);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        ((ImageView) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                dialog.cancel();
                                dialog.closeOptionsMenu();


                            }
                        });

                        ((ImageView) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                dialog.cancel();
                                //continuar();
                                addtarjetacred();

                            }
                        });
                    }

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
                public void addtarjetacred() {

                    Intent intent = new Intent(activity.getApplicationContext(), AgregarTarjeta.class);
                    activity.startActivity(intent);

                }
            });

            ////////*******

        }

        //pagos


    }
}