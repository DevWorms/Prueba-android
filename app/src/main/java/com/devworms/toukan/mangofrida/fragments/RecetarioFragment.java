package com.devworms.toukan.mangofrida.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
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

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.componentes.AdapterRecetarioList;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class RecetarioFragment extends Fragment {
    private ParseObject objParse;
    private List<ParseObject> lMenusRecetas;
    private AdapterRecetarioList mAdapterMenuList;
    Boolean isSuscribed = false;

    private String tipoMenu;

    public void setMenuSeleccionado(ParseObject objParse) {
        this.objParse = objParse;
    }

    public void obtenerObjetosParse(final RecyclerView recyclerView) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
        query.whereEqualTo("Menu", objParse);
        query.whereEqualTo("Activada", true);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenusRecetas = menuList;

                    mAdapterMenuList = new AdapterRecetarioList(menuList, tipoMenu, getActivity(), isSuscribed);
                    recyclerView.setAdapter(mAdapterMenuList);

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recetario, container, false);

        TextView txtNombreRecetario = (TextView) view.findViewById(R.id.textViewNombreRecetario);
        String nombreMenu = objParse.getString("NombreMenu");
        txtNombreRecetario.setText(nombreMenu);

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);

        /*
        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);
        */

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.INVISIBLE);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        obtenerObjetosParse(recyclerView);

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean slider = true;//preferences.getBoolean("Mostrarcarrusel", true );

        if(slider){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("Mostrarcarrusel", false);
            editor.apply();
            mostrarAnuncioDiasPrueba();
        }*/

        return view;
    }

    public void setSuscribed(boolean suscribed) {
        isSuscribed = suscribed;
    }

    /*
    public void mostrarAnuncioDiasPrueba(){
        FragmentManager fm = getChildFragmentManager();
        CarruselSlider cs = new CarruselSlider();

        cs.show(fm, "carrusel");
    }
*/

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception ex) {

        }
        super.onDetach();
    }

    public void setTipoMenu(String tipoMenu) {
        this.tipoMenu = tipoMenu;
    }
}
