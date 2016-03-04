package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.CustomList;
import com.devworms.editorial.mango.componentes.CustomListListImagesAndText;
import com.devworms.editorial.mango.componentes.CustomListParse;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sergio on 21/10/15.
 */
public class CategoriaFragment extends Fragment {
    ListView list;

    private ParseObject objParse;
    private List<ParseObject> lMenusRecetas;


    public void setMenuSeleccionado(ParseObject objParse){
        this.objParse = objParse;
    }

    public ParseObject getMenuSeleccionado(){
        return this.objParse;
    }

    public void obtenerObjetosParse(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
        query.whereEqualTo("Menu", objParse);
        query.whereEqualTo("Activada", true);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenusRecetas = menuList;
                    crearListado(getView());
                    Log.d("score", "Retrieved " + lMenusRecetas.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void crearListado(View view){
        //Imagen

        ImageView imagen =(ImageView) view.findViewById(R.id.imagenCategoria);
        Bitmap bt = null;//((StarterApplication) getActivity().getApplication()).getImagenReceta();
        if (bt==null){
            new DownloadImageTask(imagen).execute(objParse.getString("Url_Imagen"));
        }
        else{
            imagen.setImageBitmap(bt);
        }

        //Menu
        //HashMap<ParseObject, CustomListParse> hashMap = ((StarterApplication) this.getActivity().getApplication()).getlistaRecetasPorMenu();
        // if (hashMap.get(this.objParse) == null){
            CustomListParse adapter = new
                    CustomListParse(getActivity(), this.lMenusRecetas);

          /*  hashMap.put(objParse, adapter);
            // ((StarterApplication) this.getActivity().getApplication()).setlistaRecetasPorMenu(hashMap);
        }*/


        list=(ListView)view.findViewById(R.id.listreceta);
        list.setAdapter(adapter);//hashMap.get(this.objParse));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        RecetaFragment receta = new RecetaFragment();
                        receta.objReceta = lMenusRecetas.get(position);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, receta)
                                .addToBackStack("MenuFragment")
                                .commit();


                    }
                });

        //list.setDivider(null);
        list.setDivider(null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_categoria, container, false);
        //HashMap<ParseObject, CustomListParse> hashMap = ((StarterApplication) this.getActivity().getApplication()).getlistaRecetasPorMenu();
      //  if (hashMap.get(this.objParse) == null){
            obtenerObjetosParse();
        //}
        //else{*/
            //crearListado(view);
        //}


        return view;
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String urldisplay;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.invalidate();
           // ((StarterApplication) getActivity().getApplication()).setImagenReceta(result);
        }

    }
}
