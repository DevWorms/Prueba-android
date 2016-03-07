package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterRecetarioList;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class RecetaFragment extends Fragment implements View.OnClickListener{

    private ParseObject objReceta;
    private Bitmap imgReceta;


    public ParseObject getObjReceta() {
        return objReceta;
    }

    public void setObjReceta(ParseObject objReceta) {
        this.objReceta = objReceta;
    }

    public Bitmap getImgReceta() {
        return imgReceta;
    }

    public void setImgReceta(Bitmap imgReceta) {
        this.imgReceta = imgReceta;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_receta, container, false);


        ImageView imagen = (ImageView) view.findViewById(R.id.imagenreceta);
        imagen.setImageBitmap(this.getImgReceta());



        TextView pasosTitulo=(TextView)view.findViewById(R.id.txtrecetaTitulo);
        TextView pasos=(TextView)view.findViewById(R.id.txtreceta);
        pasosTitulo.setText(objReceta.getString("Nombre"));
        pasos.setText("Ingredientes \n" + (objReceta.getString("Ingredientes")));
        pasos.setText(pasos.getText() + "\n\nProcedimiento\n" + (objReceta.getString("Procedimiento")));

        FloatingActionButton buttonCompartir = (FloatingActionButton) view.findViewById(R.id.compartir);
        FloatingActionButton buttonAnadirFavoritos = (FloatingActionButton) view.findViewById(R.id.favoritos);


        buttonCompartir.setOnClickListener(this);
        buttonAnadirFavoritos.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.compartir:
                compartir();
                break;
            case R.id.favoritos:
                anadirFavoritos();
                break;
        }
    }

    public void compartir()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.actividad,(new CompartirFragment()))
                .addToBackStack("RecetaFragment")
                .commit();
    }


    public void obtenerObjetosParse(){


    }

    public void anadirFavoritos()
    {


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.whereEqualTo("Receta", objReceta);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> recetasList, ParseException e) {
                if (e == null) {
                    //Revisa si ese cliente tiene esa receta para mandar un mensaje de error al tratar de añadirla de nuevo
                    if (recetasList.size() > 0 ) {
                        Toast.makeText(getActivity(),"¡Esta receta ya fue añadida!\",\n" + "message: \"Tu receta ya esta en la seccion de favoritos",Toast.LENGTH_LONG);
                    }
                    else{

                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(cal.YEAR);
                        int month = cal.get(cal.MONTH)+1;
                        int trimestre = (int)(((month)/3) + 0.7);


                        ParseObject query = new ParseObject("Favoritos");
                        query.put("username", ParseUser.getCurrentUser());
                        query.put("Anio", year);
                        query.put("Mes", month);
                        query.put("Mes", month);
                        query.put("Trimestre", trimestre);
                        query.put("Mes", month);
                        query.add("Receta", objReceta);

                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(getActivity(),"¡Esta receta ya fue añadida!\",\n" + "message: \"Tu receta ya esta en la seccion de favoritos",Toast.LENGTH_LONG);
                            }
                        });
                    }


                } else {
                    Log.d("receta", "Error: " + e.getMessage());
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
