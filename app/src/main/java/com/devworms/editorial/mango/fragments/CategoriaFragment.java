package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.CustomListListImagesAndText;

/**
 * Created by sergio on 21/10/15.
 */
public class CategoriaFragment extends Fragment {
    ListView list;
    String[] web = {
            "Nombre del platillo:Hot dog 1\nPorciones: 1 persona\nTiempo: 20 min",
            "Nombre del platillo:Hot dog 2\nPorciones: 1 persona\nTiempo: Entrada",
            "Nombre del platillo:Hot dog 3\nPorciones: 1 persona\nTiempo: Primero",
            "Nombre del platillo:Hot dog 4\nPorciones: 1 persona\nTiempo: etc",
            "Nombre del platillo:Hot dog 5 ffff\nPorciones: 1 persona\nTiempo: ejemplo",
    } ;
    Integer[] imageId = {
            R.drawable.hot_dog,
            R.drawable.hot_dog,
            R.drawable.hot_dog,
            R.drawable.hot_dog,
            R.drawable.hot_dog,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_categoria, container, false);

        //Imagen

        ImageView imagen =(ImageView) view.findViewById(R.id.imagenCategoria);
        imagen.setImageResource(R.drawable.comida);


        //Menu
        CustomListListImagesAndText adapter = new
                CustomListListImagesAndText(getActivity(), web, imageId);
        list=(ListView)view.findViewById(R.id.listreceta);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                getFragmentManager().beginTransaction()
                        .replace(R.id.actividad, new RecetaFragment())
                        .addToBackStack("MenuFragment")
                        .commit();


            }
        });

        //list.setDivider(null);
        list.setDivider(null);
        return view;
    }
}
