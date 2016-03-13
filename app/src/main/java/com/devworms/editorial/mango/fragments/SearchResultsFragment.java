package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterBuscadorList;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsFragment extends Fragment {



    public  String query;
    private AdapterBuscadorList mAdapterRecetarioList;


    public void obtenerObjetosParse(final RecyclerView recyclerView, String []tags){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tags");

        query.whereContainedIn("Tag", Arrays.asList(tags));


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

        View view = inflater.inflate(R.layout.fragment_result, container, false);
        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        String []tags = query.split(" ");

        obtenerObjetosParse(recyclerView, tags);
        return view;
    }


}