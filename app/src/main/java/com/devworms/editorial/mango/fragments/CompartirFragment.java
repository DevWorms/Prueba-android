package com.devworms.editorial.mango.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devworms.editorial.mango.R;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class CompartirFragment extends Fragment implements View.OnClickListener {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_compartir, container, false);

        Button buttonFb = (Button) view.findViewById(R.id.bFacebook);
        Button buttonTw = (Button) view.findViewById(R.id.bTwitter);
        Button buttonPin = (Button) view.findViewById(R.id.bPinterest);

        buttonFb.setOnClickListener(this);
        buttonTw.setOnClickListener(this);
        buttonPin.setOnClickListener(this);

        return view;

    }

    //Para asignar accion a los botones dentro del fragment
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bFacebook:
                compartirFb();
                break;
            case R.id.bTwitter:
                compartirTw();
                break;
            case R.id.bPinterest:
                compartirPin();
                break;

        }
    }

    private void compartirFb()
    {
        // compartir una imagen
        //Se saca la imagen de los recursos
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.salchichas);

       SharePhoto photo = new SharePhoto.Builder()
               .setBitmap(bitmap)
               .setUserGenerated(true)
               .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog.show(this.getActivity(), content);


    }

    public void compartirTw()
    {
        Uri path = Uri.parse("android.resource://"+getActivity().getApplicationContext().getPackageName() +"/"+ R.drawable.salchichas);

        //  File myImageFile = new File("/path/to/image");
        // Uri myImageUri = Uri.fromFile(myImageFile);
        TweetComposer.Builder builder = new TweetComposer.Builder(this.getActivity())
                .text("¡Me encanta esta receta!")
                .image(path);
        builder.show();

    }

    public void compartirPin()
    {

    }

}
