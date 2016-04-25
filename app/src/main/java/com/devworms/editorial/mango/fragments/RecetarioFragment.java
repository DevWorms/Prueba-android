package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterMenuList;
import com.devworms.editorial.mango.componentes.AdapterRecetarioList;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by sergio on 21/10/15.
 */
public class RecetarioFragment extends Fragment {


    private ParseObject objParse;
    private List<ParseObject> lMenusRecetas;
    private AdapterRecetarioList mAdapterMenuList;

    private String tipoMenu;

    public void setMenuSeleccionado(ParseObject objParse){
        this.objParse = objParse;
    }

    public void obtenerObjetosParse(final RecyclerView recyclerView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
        query.whereEqualTo("Menu", objParse);
        query.whereEqualTo("Activada", true);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenusRecetas = menuList;

                    mAdapterMenuList = new AdapterRecetarioList(menuList, tipoMenu, getActivity());
                    recyclerView.setAdapter(mAdapterMenuList);

                    Log.d("score", "Retrieved " + lMenusRecetas.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_recetario, container, false);

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);

        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);

        TextView txtFrida = (TextView) getActivity().findViewById(R.id.textViewMensajeBienvenida);
        txtFrida.setVisibility(View.INVISIBLE);

        TextView txtNombreRecetario = (TextView) view.findViewById(R.id.textViewNombreRecetario);
        String nombreMenu = objParse.getString("NombreMenu");
        txtNombreRecetario.setText(nombreMenu);


        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));



        obtenerObjetosParse(recyclerView);


        return view;
    }



    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
        catch (Exception ex)
        {

        }
        super.onDetach();
    }


    public void setTipoMenu(String tipoMenu) {
        this.tipoMenu = tipoMenu;
    }
}
