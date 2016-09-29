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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.Specs;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.util.ArrayList;
import java.util.List;


public final class AdapterFavoritoList extends RecyclerView.Adapter<AdapterFavoritoList.ViewHolder> {

    private List<ParseObject> mItems;
    private Activity actividad;

    public AdapterFavoritoList(List<ParseObject> mItems, Activity actividad) {
        this.mItems = mItems;
        this.actividad = actividad;

        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {


                parseObject.getParseObject("Recetas").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject receta, ParseException e) {

                        FastImageLoader.prefetchImage(receta.getString("Url_Imagen"), Specs.IMG_IX_UNBOUNDED);
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterFavoritoList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recetario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);
        holder.actividad = this.actividad;

        mItems.get(position).getParseObject("Recetas").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject receta, ParseException e) {

                holder.objReceta = receta;

                holder.setTitulos(receta);

                holder.mTargetImageView.loadImage(receta.getString("Url_Imagen"), spec.getKey());
            }
        });


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



        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);

            mTargetImageView.setOnClickListener(this);

            tTextViewNombrereceta = (TextView) v.findViewById(R.id.textViewNombrereceta);
            tTextViewTiempo = (TextView) v.findViewById(R.id.textViewTiempo);
            textViewPorciones = (TextView) v.findViewById(R.id.textViewPorciones);

            iImageViewDificultad = (ImageView) v.findViewById(R.id.imageView12);

        }


        public void setTitulos(ParseObject objReceta){
            tTextViewNombrereceta.setText(objReceta.getString("Nombre"));
            tTextViewTiempo.setText("  " + objReceta.getString("Tiempo"));
            textViewPorciones.setText("  " + objReceta.getString("Porciones"));

            String dificultad = objReceta.getString("Nivel");


            int imageresource = 0;
            switch (dificultad){

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

                RecetarioFragment recetario = new RecetarioFragment();
                recetario.setMenuSeleccionado(objReceta);

                final ImageView imageView = (ImageView) v;
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
    //endregion
}