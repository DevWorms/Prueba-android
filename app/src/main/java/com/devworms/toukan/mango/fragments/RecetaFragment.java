package com.devworms.toukan.mango.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.dialogs.CompartirDialog;
import com.devworms.toukan.mango.main.StarterApplication;
import com.devworms.toukan.mango.util.Specs;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

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

    public TargetImageView imagen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_receta, container, false);



        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);


        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.INVISIBLE);


        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        imagen = (TargetImageView) view.findViewById(R.id.imagenreceta);
        FastImageLoader.prefetchImage(objReceta.getString("Url_Imagen"), Specs.IMG_IX_IMAGE);
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
        imagen.loadImage(objReceta.getString("Url_Imagen"), spec.getKey());


        TextView pasosTitulo=(TextView)view.findViewById(R.id.txtrecetaTitulo);
        TextView pasos=(TextView)view.findViewById(R.id.txtreceta);

        TextView tiempo = (TextView) view.findViewById(R.id.textView14);
        TextView porciones = (TextView) view.findViewById(R.id.textView18);

        ImageView iImageViewDificultad = (ImageView) view.findViewById(R.id.imageView12);


        String dificultad = objReceta.getString("Nivel");


        int imageresource = 0;
        switch (dificultad) {

            case "Principiante":
                imageresource = getActivity().getResources().getIdentifier("@drawable/florn1", "drawable", getActivity().getPackageName());

                iImageViewDificultad.setImageResource(imageresource);
                break;
            case "Intermedio":
                imageresource = getActivity().getResources().getIdentifier("@drawable/florn2", "drawable", getActivity().getPackageName());
                iImageViewDificultad.setImageResource(imageresource);
                break;
            case "Avanzado":
                imageresource = getActivity().getResources().getIdentifier("@drawable/florn3", "drawable", getActivity().getPackageName());
                iImageViewDificultad.setImageResource(imageresource);
                break;
            default:
                break;
        }


        tiempo.setText("  " + objReceta.getString("Tiempo"));


        porciones.setText("  " + objReceta.getString("Porciones").replace("personas",""));

        pasosTitulo.setText(objReceta.getString("Nombre"));
        pasos.setText("Ingredientes \n" + (objReceta.getString("Ingredientes")));
        pasos.setText(pasos.getText() + "\n\nProcedimiento\n" + (objReceta.getString("Procedimiento")));

        ImageView buttonCompartir = (ImageView) view.findViewById(R.id.compartir);
        ImageView buttonAnadirFavoritos = (ImageView) view.findViewById(R.id.favoritos);



        buttonCompartir.setOnClickListener(this);
        buttonAnadirFavoritos.setOnClickListener(this);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.whereEqualTo("Recetas", objReceta);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> recetasList, ParseException e) {
                if (e == null) {
                    //Revisa si ese cliente tiene esa receta para mandar un mensaje de error al tratar de añadirla de nuevo
                    if (recetasList.size() > 0) {
                        int imageresource = getActivity().getResources().getIdentifier("@drawable/corazon", "drawable", getActivity().getPackageName());
                        ImageView iImageViewBtnRelease = (ImageView)getActivity().findViewById(R.id.favoritos);
                        iImageViewBtnRelease.setImageResource(imageresource);
                    }

                }
            }
        });


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
        CompartirDialog compartir = new CompartirDialog(getActivity(),this.objReceta, false );
        compartir.show();
    }


    public void anadirFavoritos()
    {


    if(ParseUser.getCurrentUser() == null){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));


        // set title
        alertDialogBuilder.setTitle("Iniciar sesión obligatorio");

        // set dialog message
        alertDialogBuilder
                .setMessage("Para poder añadir esta reseta a Me Gustan es necesario iniciar sesión")
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

    }else{


            ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
            query.whereEqualTo("username", ParseUser.getCurrentUser());
            query.whereEqualTo("Recetas", objReceta);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> recetasList, ParseException e) {
                    if (e == null) {
                        //Revisa si ese cliente tiene esa receta para mandar un mensaje de error al tratar de añadirla de nuevo
                        if (recetasList.size() > 0 ) {
                            String titulo = "¡Esta receta ya fue añadida!";
                            String mensaje = "Tu receta ya esta en la seccion de Me Gustan";



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
                                        titulo = "Añadido a Me Gustan";
                                        mensaje = "¡Tu receta ya esta disponible en la seccion de Me Gustan!";
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

                                                    int imageresource = getActivity().getResources().getIdentifier("@drawable/corazon", "drawable", getActivity().getPackageName());
                                                    ImageView iImageViewBtnRelease = (ImageView)getActivity().findViewById(R.id.favoritos);
                                                    iImageViewBtnRelease.setImageResource(imageresource);
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
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {

        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
        catch (Exception ex){
            Log.e("error", ex.getMessage());
        }
        super.onDetach();
    }

}
