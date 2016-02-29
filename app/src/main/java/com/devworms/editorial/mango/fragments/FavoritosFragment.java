package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.CustomList;


/**
 * Created by sergio on 21/10/15.
 */
public class FavoritosFragment extends Fragment {
    ListView list;
    String[] web = {
            "Favorito 1",
            "Favorito 2",

    } ;
    Integer[] imageId = {
            R.drawable.comidag,
            R.drawable.comidag,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_favoritos, container, false);

        //Menu
        CustomList adapter = new
                CustomList(getActivity(), web, imageId);
        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        //list.setDivider(null);
        ColorDrawable sage = new ColorDrawable();
        list.setDivider(null);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.actividad, new CategoriaFragment())
                        .addToBackStack("MenuFragment")
                        .commit();


            }
        });

        //list.setDivider(null);
        list.setDivider(null);
        return view;
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }
}
