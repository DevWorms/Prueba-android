package com.devworms.toukan.mangofrida.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.devworms.toukan.mangofrida.R;

public class ConsejosFragment extends Fragment {
    ListView list;
    String[] web = {
            "Comida 1",
            "Comida 2",
            "Comida 3",
            "Comida 4",
            "Comida 5",
    };
    Integer[] imageId = {
            R.drawable.fondo,
            R.drawable.fondo,
            R.drawable.fondo,
            R.drawable.fondo,
            R.drawable.fondo,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consejos, container, false);


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

    // El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }
}
