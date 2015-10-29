package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.devworms.editorial.mango.R;


/**
 * Created by sergio on 21/10/15.
 */
public class FavoritosFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_categoria, container, false);



        /*//Menu
        CustomList adapter = new
                CustomList(this.getActivity(), web, imageId);
        list=(ListView)this.getActivity().findViewById(R.id.list);
        list.setAdapter(adapter);
        //list.setDivider(null);
        ColorDrawable sage = new ColorDrawable();
        list.setDivider(null);*/
        return view;
    }
}
