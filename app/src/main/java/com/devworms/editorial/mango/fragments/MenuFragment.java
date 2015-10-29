package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
public class MenuFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.content_main, container, false);

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

        return view;
    }


}
