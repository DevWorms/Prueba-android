package com.devworms.editorial.mango.componentes;

/**
 * Created by DevWorms S.A. de C.V. on 20/10/15.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;


public class CustomListListImagesAndText extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    public CustomListListImagesAndText(Activity context,
                                       String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_steps, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtstep);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgstep);
        txtTitle.setText(web[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}