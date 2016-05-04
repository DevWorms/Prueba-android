package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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
import com.devworms.editorial.mango.componentes.DividerItemDecoration;
import com.devworms.editorial.mango.main.StarterApplication;
import com.devworms.editorial.mango.componentes.AdapterMenuList;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class MenuFragment extends Fragment {

    List<ParseObject> lMenus;
    private AdapterMenuList mAdapterMenuList;

    public void obtenerObjetosParse(final RecyclerView recyclerView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
        query.whereEqualTo("Activo", true);
        query.orderByAscending("Orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenus = menuList;
                    mAdapterMenuList = new AdapterMenuList(menuList);
                    recyclerView.setAdapter(mAdapterMenuList);

                    Log.d("score", "Retrieved " + lMenus.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());

                }
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.barraPincipal));

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.VISIBLE);

        TextView txtFrida = (TextView) getActivity().findViewById(R.id.textViewMensajeBienvenida);
        txtFrida.setVisibility(View.VISIBLE);


        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.VISIBLE);
        imgFondoBarra.setImageResource(R.drawable.fonsobar);


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


}
