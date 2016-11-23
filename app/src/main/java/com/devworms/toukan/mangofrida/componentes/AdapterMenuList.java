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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.dialogs.CompartirDialog;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.SoporteMultiplesPantallas;
import com.devworms.toukan.mangofrida.util.Specs;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.util.HashMap;
import java.util.List;


public final class AdapterMenuList extends RecyclerView.Adapter<AdapterMenuList.ViewHolder> {

    private List<ParseObject> mItems;
    private HashMap<ParseObject, Integer> numRecetasPorMenu;


    public AdapterMenuList(List<ParseObject> mItems) {
        this.mItems = mItems;

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
    public AdapterMenuList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
        holder.objMenu = mItems.get(position);
        holder.contarRecetas(mItems.get(position));
        holder.numRecetasPorMenu = numRecetasPorMenu;
        holder.mTargetImageView.loadImage(mItems.get(position).getString("Url_Imagen"), spec.getKey());


        if(position == 0){
            holder.imageViewDeviderbottom.setVisibility(View.INVISIBLE);
            if(mItems.size()==1)
                holder.imageViewDeviderTop.setVisibility(View.INVISIBLE);
            else
                holder.imageViewDeviderTop.setVisibility(View.VISIBLE);

        }
        else {
            if( mItems.size()-1 == position){
                holder.imageViewDeviderbottom.setVisibility(View.VISIBLE);
                holder.imageViewDeviderTop.setVisibility(View.INVISIBLE);
            }
            else{
                holder.imageViewDeviderbottom.setVisibility(View.VISIBLE);
                holder.imageViewDeviderTop.setVisibility(View.VISIBLE);
            }
        }

    }

    //region: Inner class: ViewHolder

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //public final TextView mUrlTextView;

        public HashMap<ParseObject, Integer> numRecetasPorMenu;

        public final TargetImageView mTargetImageView;
        public TextView tTextViewNumeroRecetas;
        public TextView tTextViewTipoPaquete;
        public TextView tTextViewNombrePlatillo;
        public ImageView imageViewCinta;
        public ImageView imageViewTipoPaquete;
        public ImageView imageViewDeviderTop;
        public ImageView imageViewDeviderbottom;

        public FragmentActivity activity;

        ParseObject objMenu;



        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);
            mTargetImageView.setOnClickListener(this);
            this.activity = (FragmentActivity) mTargetImageView.getContext();

            tTextViewNumeroRecetas =(TextView) v.findViewById(R.id.textViewNumeroRecetas);
            tTextViewTipoPaquete = (TextView) v.findViewById(R.id.textViewTipoPaquete);
            tTextViewNombrePlatillo = (TextView) v.findViewById(R.id.textViewNombrePlatillo);


            imageViewCinta = (ImageView) v.findViewById(R.id.imageViewCinta);
            imageViewTipoPaquete = (ImageView) v.findViewById(R.id.imageViewTipoPaquete);

            imageViewDeviderbottom = (ImageView) v.findViewById(R.id.imageViewDeviderbottom);
            imageViewDeviderTop = (ImageView) v.findViewById(R.id.imageViewDeviderTop);



        }

        public void contarRecetas(final ParseObject objMenu){

            String tipo = "PAQUETE";
            if (objMenu.getString("TipoMenu").toLowerCase().equals("gratis")||objMenu.getString("TipoMenu").toLowerCase().equals("pago")){
                tTextViewTipoPaquete.setText(tipo);
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
            query.whereEqualTo("Menu", objMenu);
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        // The count request succeeded. Log the count
                        if (true){//objMenu.getString("TipoMenu").toLowerCase().equals("gratis")||objMenu.getString("TipoMenu").toLowerCase().equals("pago")){
                            tTextViewNumeroRecetas.setText(count + " receta" );
                            if (count > 1){
                                tTextViewNumeroRecetas.setText(tTextViewNumeroRecetas.getText()+"s");
                            }

                            tTextViewTipoPaquete.setVisibility(View.VISIBLE);
                            imageViewCinta.setVisibility(View.VISIBLE);
                            imageViewTipoPaquete.setVisibility(View.VISIBLE);
                            tTextViewNumeroRecetas.setVisibility(View.VISIBLE);


                            String tipomenu = objMenu.getString("TipoMenu").toLowerCase();
                            int imageresource = 0;
                            switch (tipomenu){
                                case "gratis":
                                    imageresource = activity.getResources().getIdentifier("@drawable/paquetesgratis", "drawable", activity.getPackageName());

                                    imageViewTipoPaquete.setImageResource(imageresource);

                                    break;
                                case "pago":
                                    imageresource = activity.getResources().getIdentifier("@drawable/premium", "drawable", activity.getPackageName());

                                    imageViewTipoPaquete.setImageResource(imageresource);
                                    break;
                                case "viral":
                                    imageresource = activity.getResources().getIdentifier("@drawable/viral", "drawable", activity.getPackageName());

                                    imageViewTipoPaquete.setImageResource(imageresource);
                                    break;
                            }


                        }else{
                            tTextViewTipoPaquete.setVisibility(View.GONE);
                            imageViewCinta.setVisibility(View.GONE);
                            imageViewTipoPaquete.setVisibility(View.GONE);
                            tTextViewNumeroRecetas.setVisibility(View.GONE);
                        }

                        String nombre = objMenu.getString("NombreMenu");
                        tTextViewNombrePlatillo.setText(nombre);

                    } else {
                        // The request failed
                    }
                }
            });

        }


        @Override
        public void onClick(View v) {
            final FragmentActivity activity = (FragmentActivity) mTargetImageView.getContext();
            if (activity != null) {

                String tipo = objMenu.getString("TipoMenu").toLowerCase();



                switch (tipo) {
                    case "gratis": case "pago"://Gratis o de pagoç

                        RecetarioFragment recetario = new RecetarioFragment();
                        recetario.setMenuSeleccionado(objMenu);
                        recetario.setTipoMenu(tipo);

                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.actividad,recetario)
                                .addToBackStack("MenuFragment")
                                .commit();

                        break;

                    case "viral":

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
                        query.whereEqualTo("Menu", objMenu);

                        query.findInBackground(new FindCallback<ParseObject>() {
                               public void done(final List<ParseObject> recetasList, ParseException e) {
                                   if (e == null) {

                                       if (recetasList.size() > 0) {

                                           ParseQuery<ParseObject> query = ParseQuery.getQuery("Regalos");
                                           query.whereEqualTo("username", ParseUser.getCurrentUser());
                                           query.whereEqualTo("Recetario", objMenu);

                                           query.findInBackground(new FindCallback<ParseObject>() {
                                               public void done(List<ParseObject> recetasList, ParseException e) {
                                                   if (e == null) {
                                                       //Si la receta ya esta en favoritos significa que ya fue compartida
                                                       if (recetasList.size() > 0) {

                                                           RecetarioFragment recetario = new RecetarioFragment();
                                                           recetario.setMenuSeleccionado(objMenu);
                                                           recetario.setTipoMenu("gratis");

                                                           activity.getSupportFragmentManager().beginTransaction()
                                                                   .replace(R.id.actividad,recetario)
                                                                   .addToBackStack("MenuFragment")
                                                                   .commit();
                                                       }
                                                       else{

                                                           StarterApplication.isDesdeMenuPrincipal = true;
                                                           CompartirDialog compartirDialog = new CompartirDialog(activity, objMenu);

                                                           compartirDialog.show();
                                                           StarterApplication.dialogoCompartir = compartirDialog;

                                                       }


                                                   }
                                               }
                                           });

                                       }
                                   }
                               }
                           });




                        break;
                }

            }
        }



    }
    //endregion
}