package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.CustomList;
import com.devworms.editorial.mango.componentes.CustomListParse;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.Parse;
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

    public void obtenerObjetosParse(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
        query.whereEqualTo("Activo", true);
        query.orderByAscending("Orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenus = menuList;
                    crearListado(getView());
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

        CustomListParse adapter = ((StarterApplication) this.getActivity().getApplication()).getListaMenuPrincipal();
        if (adapter == null){
            obtenerObjetosParse();
        }
        else{
            crearListado(view);
        }

        return view;
    }

    private void crearListado(View view){

        CustomListParse adapter = ((StarterApplication) this.getActivity().getApplication()).getListaMenuPrincipal();
        if (adapter == null){
            adapter = new
                    CustomListParse(getActivity(), this.lMenus);
            ((StarterApplication) this.getActivity().getApplication()).setListaMenuPrincipal(adapter);
        }

        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        //list.setDivider(null);
        ColorDrawable sage = new ColorDrawable();
        list.setDivider(null);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                ParseObject objParse = lMenus.get(position);
                String tipo = objParse.getString("TipoMenu").toLowerCase();
                ((StarterApplication) getActivity().getApplication()).setImagenReceta(null);

                switch (tipo) {
                    case "gratis": case "pago"://Gratis o de pago
                        CategoriaFragment recetario = new CategoriaFragment();
                        recetario.setMenuSeleccionado(lMenus.get(position));
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
