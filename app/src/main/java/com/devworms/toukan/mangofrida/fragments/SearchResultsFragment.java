package com.devworms.toukan.mangofrida.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.componentes.AdapterRecetarioList;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.Specs;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsFragment extends Fragment {



    public  String[] query;
    private AdapterRecetarioList mAdapterRecetarioList;
    private TextView txtNombreRecetario;

    private ProgressBar progressBar;

    public void obtenerObjetosParse(final RecyclerView recyclerView, String []tags){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tags");

        query.whereContainedIn("Tag", Arrays.asList(tags));

        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> recetasList, ParseException e) {
                if (e == null) {


                    final List<ParseObject> lItems = new ArrayList<ParseObject>();
                    final List<String> lTipos = new ArrayList<String>();
                    progressBar.setVisibility(View.INVISIBLE);

                    if (recetasList.size() > 0) {
                        for (ParseObject parseObject : recetasList) {

                            parseObject.getParseObject("Receta").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                public void done(final ParseObject receta, ParseException e) {
                                    lItems.add(receta);
                                    receta.getParseObject("Menu").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject menu, ParseException e) {
                                            lTipos.add(menu.getString("TipoMenu").toLowerCase());

                                            if (lTipos.size() == recetasList.size()) {
                                                mAdapterRecetarioList = new AdapterRecetarioList(lItems, lTipos, getActivity());
                                                recyclerView.setAdapter(mAdapterRecetarioList);

                                            }

                                        }
                                    });

                                }
                            });
                        }
                    }else{
                        txtNombreRecetario.setText("No hay resultados");
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

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        String []tags = query;

        for (int i = 0; i<query.length;i++){
            tags[i] = "#"+tags[i];
        }

        obtenerObjetosParse(recyclerView, tags);


      /*  ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);
*/
        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);


        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));


         txtNombreRecetario = (TextView) view.findViewById(R.id.textViewNombreRecetario);
        String nombreMenu = "Resultados";
        txtNombreRecetario.setText(nombreMenu);

        return view;
    }





}