package com.devworms.toukan.mangofrida.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.componentes.AdapterMenuList;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RegalosFragment extends Fragment {
    private AdapterMenuList mAdapterFavoritosList;

    public void obtenerObjetosParse(final RecyclerView recyclerView) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Regalos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> recetasList, ParseException e) {
                if (e == null) {
                    final List<ParseObject> listaRegalos = new ArrayList<ParseObject>();

                    for (final ParseObject objRegalo : recetasList) {
                        objRegalo.getParseObject("Recetario").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject menu, ParseException e) {
                                listaRegalos.add(menu);

                                if (listaRegalos.size() == recetasList.size()) {
                                    mAdapterFavoritosList = new AdapterMenuList(listaRegalos);
                                    recyclerView.setAdapter(mAdapterFavoritosList);
                                }
                            }
                        });
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_regalos, container, false);

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);

        /*
        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);
        */

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.INVISIBLE);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        if (ParseUser.getCurrentUser() == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));
            // set title
            alertDialogBuilder.setTitle("Iniciar sesión obligatorio");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Inicia sesión para poder añadir recetas a esta sección")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            obtenerObjetosParse(recyclerView);
        }

        return view;
    }

    // El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
