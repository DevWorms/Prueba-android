package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.Login;
import com.devworms.editorial.mango.activities.MainActivity;
import com.parse.ParseUser;

//import com.parse.ParseUser;

/**
 * Created by sergio on 21/10/15.
 */
public class CuentaFragment extends Fragment{
    ListView list;
    String[] web = {
            "Comida 1",
            "Comida 2",
            "Comida 3",
            "Comida 4",
            "Comida 5",
    } ;
    Integer[] imageId = {
            R.drawable.comida,
            R.drawable.comida,
            R.drawable.comida,
            R.drawable.comida,
            R.drawable.comida,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view=null;

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            view=inflater.inflate(R.layout.fragment_contaco_detalles, container, false);

            Button button = (Button) view.findViewById(R.id.cerrarSesionBtn);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ParseUser.logOut();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, new CuentaFragment())
                            .addToBackStack("cuenta")
                            .commit();

                }
            });
        } else {
            // show the signup or login screen
            view=inflater.inflate(R.layout.fragment_contacto, container, false);


        }

        return view;
    }



}
