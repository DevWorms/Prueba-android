package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterFavoritoList;
import com.devworms.editorial.mango.componentes.AdapterMenuList;
import com.devworms.editorial.mango.componentes.AdapterRecetarioList;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class FavoritosFragment extends Fragment {


    private AdapterFavoritoList mAdapterFavoritosList;

    public void obtenerObjetosParse(final RecyclerView recyclerView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        //query.include("Receta");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> recetasList, ParseException e) {
                if (e == null) {

                    mAdapterFavoritosList = new AdapterFavoritoList(recetasList);
                    recyclerView.setAdapter(mAdapterFavoritosList);


                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

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
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }
}
