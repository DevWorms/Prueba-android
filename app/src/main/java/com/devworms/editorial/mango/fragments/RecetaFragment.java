package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;


/**
 * Created by sergio on 21/10/15.
 */
public class RecetaFragment extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_receta, container, false);

        ImageView imagen = (ImageView) view.findViewById(R.id.imagenreceta);
        imagen.setImageResource(R.drawable.hot_dog);

        TextView pasosTitulo=(TextView)view.findViewById(R.id.txtrecetaTitulo);
        TextView pasos=(TextView)view.findViewById(R.id.txtreceta);
        pasosTitulo.setText("Pasos para preparar un hot dog");
        pasos.setText("Paso numweo 1: Saca la salchicha de la bolsa\n" +
                "Paso numero 2: hierve la salchicha (solo una) con sal\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +
                "Paso numero 3: Sacala del agua\n" +
                "Paso numero 4: calienta el pan\n" +


                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +
                "Paso numero 5: pon la salchica dentro del pan\n" +

                "Paso numero 6: pon condimentos a tu gusto :)");






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
                .replace(R.id.actividad, new CompartirFragment())
                .addToBackStack("RecetaFragment")
                .commit();
    }

    public void anadirFavoritos()
    {

    }


}
