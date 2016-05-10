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

package com.devworms.toukan.mango.componentes;

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

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.fragments.RecetaFragment;
import com.devworms.toukan.mango.fragments.RecetarioFragment;
import com.devworms.toukan.mango.main.StarterApplication;
import com.devworms.toukan.mango.util.Specs;
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


    public AdapterFavoritoList(List<ParseObject> mItems) {
        this.mItems = mItems;

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
        ParseObject parseObject = mItems.get(position);


        parseObject.getParseObject("Recetas").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject receta, ParseException e) {
                holder.objReceta = receta;
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



        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);

            mTargetImageView.setOnClickListener(this);
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