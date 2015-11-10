package com.devworms.editorial.mango.fragments;

/**
 * Created by sergio on 08/11/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private Integer[]images =
            {
                    R.drawable.imagen1,
                    R.drawable.imagen2,
                    R.drawable.imagen3,
                    R.drawable.imagen4,
            };

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_slider, container, false);
        ImageView imageView =(ImageView) rootView.findViewById(R.id.imageSlides);
        imageView.setImageResource(images[getArguments().getInt(ARG_SECTION_NUMBER)]);

        return rootView;
    }
}

