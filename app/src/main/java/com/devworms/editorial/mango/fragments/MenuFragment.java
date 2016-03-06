package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.CustomListParse;
import com.devworms.editorial.mango.main.StarterApplication;
import com.devworms.editorial.mango.util.Adapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class MenuFragment extends Fragment {

    ListView list;
    List<ParseObject> lMenus;
    private Adapter mAdapter;

    public void obtenerObjetosParse(final RecyclerView recyclerView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
        query.whereEqualTo("Activo", true);
        query.orderByAscending("Orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenus = menuList;




                    //crearListado(getView());
                    Log.d("score", "Retrieved " + lMenus.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.content_main, container, false);

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);

        obtenerObjetosParse(recyclerView);


        return view;
    }

    private void crearListado(View view){

        CustomListParse adapter = new CustomListParse(getActivity(), this.lMenus);

        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);

        ColorDrawable sage = new ColorDrawable();
        list.setDivider(null);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                ParseObject objParse = lMenus.get(position);
                String tipo = objParse.getString("TipoMenu").toLowerCase();

                switch (tipo) {
                    case "gratis": case "pago"://Gratis o de pago
                        RecetarioFragment recetario = new RecetarioFragment();
                        recetario.setMenuSeleccionado(lMenus.get(position));

                        final ImageView imageView = (ImageView) view.findViewById(R.id.img);
                        final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                        final Bitmap imgReceta = bitmapDrawable.getBitmap();



                        recetario.setImgMenu(imgReceta);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad,recetario)
                                .addToBackStack("MenuFragment")
                                .commit();
                        break;

                    case "viral":
                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, new CompartirFragment())
                                .addToBackStack("MenuFragment")
                                .commit();
                        break;
                }


            }
        });
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }


}
