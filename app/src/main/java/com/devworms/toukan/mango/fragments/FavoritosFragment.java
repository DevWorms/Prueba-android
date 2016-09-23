package com.devworms.toukan.mango.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.componentes.AdapterFavoritoList;
import com.devworms.toukan.mango.main.StarterApplication;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class FavoritosFragment extends Fragment {


    private AdapterFavoritoList mAdapterFavoritosList;
    private List<ParseObject> mListToDelete;

    public void obtenerObjetosParse(final RecyclerView recyclerView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        //query.include("Receta");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> recetasList, ParseException e) {
                if (e == null) {

                    mAdapterFavoritosList = new AdapterFavoritoList(recetasList);
                    recyclerView.setAdapter(mAdapterFavoritosList);
                    mListToDelete = new ArrayList<ParseObject>();

                    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {


                            return false;
                        }

                        @Override
                        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                            final int adapterPosition = viewHolder.getAdapterPosition();
                            final ParseObject mReceta = recetasList.get(adapterPosition);
                            Snackbar snackbar = Snackbar
                                    .make(recyclerView, "Receta borrada de tu seccion de favoritos", Snackbar.LENGTH_LONG)
                                    .setAction("Deshacer", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int mAdapterPosition = adapterPosition;
                                            recetasList.add(mAdapterPosition, mReceta);
                                            mAdapterFavoritosList.notifyItemInserted(mAdapterPosition);
                                            recyclerView.scrollToPosition(mAdapterPosition);
                                            mListToDelete.remove(mReceta);
                                        }
                                    });
                            snackbar.show();
                            recetasList.remove(adapterPosition);
                            mAdapterFavoritosList.notifyItemRemoved(adapterPosition);
                            mListToDelete.add(mReceta);

                        }



                    };

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    itemTouchHelper.attachToRecyclerView(recyclerView);



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
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);


        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);


        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.INVISIBLE);

        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        if(ParseUser.getCurrentUser() == null){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));


            // set title
            alertDialogBuilder.setTitle("Iniciar sesión obligatorio");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Inicia sesión para poder añadir recetas a esta sección")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            //MainActivity.this.finish();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else {
            obtenerObjetosParse(recyclerView);
        }


        return view;
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mListToDelete != null) {
            for (ParseObject receta : mListToDelete) {
                receta.deleteInBackground();
            }
        }
    }
}
