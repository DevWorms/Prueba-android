package com.devworms.editorial.mango.activities;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterBuscadorList;
import com.devworms.editorial.mango.componentes.AdapterRecetarioList;
import com.devworms.editorial.mango.main.StarterApplication;
import com.devworms.editorial.mango.util.Specs;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.theartofdev.fastimageloader.FastImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends Fragment {



    public  String query;
    private AdapterBuscadorList mAdapterRecetarioList;


    public void obtenerObjetosParse(final RecyclerView recyclerView, String []tags){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tags");

        for(String tag:tags){
            query.whereEqualTo("Tag", tag);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> recetasList, ParseException e) {
                if (e == null) {


                    final List<ParseObject> lItems = new ArrayList<ParseObject>();
                    final List<String> lTipos = new ArrayList<String>();

                    if (recetasList.size() > 0) {
                        for (ParseObject parseObject : recetasList) {

                            parseObject.getParseObject("Receta").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                public void done(final ParseObject receta, ParseException e) {
                                    lItems.add(receta);
                                    receta.getParseObject("Menu").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject menu, ParseException e) {
                                            lTipos.add(menu.getString("TipoMenu").toLowerCase());

                                            if (lTipos.size() == recetasList.size()) {
                                                mAdapterRecetarioList = new AdapterBuscadorList(lItems, lTipos);
                                                recyclerView.setAdapter(mAdapterRecetarioList);
                                            }

                                        }
                                    });

                                }
                            });
                        }
                    }


                    Log.d("score", "Retrieved " + recetasList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_result, container, false);
        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        String []tags = query.split(" ");

        obtenerObjetosParse(recyclerView, tags);
        return view;
    }


}