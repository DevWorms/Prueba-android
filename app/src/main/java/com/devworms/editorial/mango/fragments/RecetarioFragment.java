package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
public class RecetarioFragment extends Fragment {
    ListView list;

    private ParseObject objParse;
    private List<ParseObject> lMenusRecetas;
    private Bitmap imgMenu;

    public void setMenuSeleccionado(ParseObject objParse){
        this.objParse = objParse;
    }

    public ParseObject getMenuSeleccionado(){
        return this.objParse;
    }

    public Bitmap getImgMenu() {
        return imgMenu;
    }

    public void setImgMenu(Bitmap imgMenu) {
        this.imgMenu = imgMenu;
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

        ImageView imagen =(ImageView) view.findViewById(R.id.imagenCategoria);
        imagen.setImageBitmap(this.imgMenu);


        CustomListParse adapter = new CustomListParse(getActivity(), this.lMenusRecetas);



        list=(ListView)view.findViewById(R.id.listreceta);
        list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        final ImageView imageView = (ImageView) view.findViewById(R.id.img);
                        final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                        final Bitmap imgReceta = bitmapDrawable.getBitmap();

                        RecetaFragment receta = new RecetaFragment();
                        receta.setObjReceta(lMenusRecetas.get(position));
                        receta.setImgReceta(imgReceta);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, receta)
                                .addToBackStack("MenuFragment")
                                .commit();


                    }
                });

        list.setDivider(null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_categoria, container, false);

        obtenerObjetosParse();


        return view;
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }
}
