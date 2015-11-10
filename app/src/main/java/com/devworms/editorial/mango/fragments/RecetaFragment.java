package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;


/**
 * Created by sergio on 21/10/15.
 */
public class RecetaFragment extends Fragment {
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
        return view;
    }
}
