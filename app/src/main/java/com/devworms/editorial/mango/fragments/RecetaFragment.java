package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.MainActivity;
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
        CompartirFragment compartir = new CompartirFragment();
        compartir.objReceta = this.objReceta;
        compartir.imgReceta = this.imgReceta;

        getFragmentManager().beginTransaction()
                .replace(R.id.actividad,(compartir))
                .addToBackStack("RecetaFragment")
                .commit();
    }


    public void obtenerObjetosParse(){


    }

    public void anadirFavoritos()
    {


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.whereEqualTo("Recetas", objReceta);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> recetasList, ParseException e) {
                if (e == null) {
                    //Revisa si ese cliente tiene esa receta para mandar un mensaje de error al tratar de añadirla de nuevo
                    if (recetasList.size() > 0 ) {
                        String titulo = "¡Esta receta ya fue añadida!";
                        String mensaje = "Tu receta ya esta en la seccion de favoritos";



                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));


                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
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
                        query.put("Recetas", objReceta);

                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                String titulo = "";
                                String mensaje = "";

                                if (e == null) {
                                    titulo = "Añadido a favoritos";
                                    mensaje = "¡Tu receta ya esta disponible en la seccion de favoritos!";
                                } else {
                                    titulo = "Error";
                                    mensaje = "Se produjo un error, intente más tarde";
                                }

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));

                                // set title
                                alertDialogBuilder.setTitle(titulo);

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(mensaje)
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                //MainActivity.this.finish();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();

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
