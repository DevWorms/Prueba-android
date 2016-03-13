package com.devworms.editorial.mango.fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devworms.editorial.mango.R;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.Utils;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class CompartirFragment extends Fragment implements View.OnClickListener {


    private static Integer[]images =
            {
                    R.drawable.imagen1,
                    R.drawable.imagen2,
                    R.drawable.imagen3,
                    R.drawable.imagen4,
            };


    private ViewPager mViewPager;

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



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.containerCompartir);

        //mViewPager.setPageTransformer(true, new RelaxTransformer());
        // mViewPager.setPageTransformer(true,  new DepthPageTransformer ());




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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images[mViewPager.getCurrentItem()]);

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
        Uri path = Uri.parse("android.resource://"+getActivity().getApplicationContext().getPackageName() +"/"+ images[mViewPager.getCurrentItem()]);

        //  File myImageFile = new File("/path/to/image");
        // Uri myImageUri = Uri.fromFile(myImageFile);
        TweetComposer.Builder builder = new TweetComposer.Builder(this.getActivity())
                .text("¡Me encanta esta receta!")
                .image(path);
        builder.show();

    }

    public void compartirPin()
    {
        String pinImageUrl = "http://pruebas.devworms.com/HOME1.png";
        String board = "ToukanMango";
        String noteText = "Me encanta esta receta";
        if (!Utils.isEmpty(noteText) &&!Utils.isEmpty(board) && !Utils.isEmpty(pinImageUrl)) {
            PDKClient
                    .getInstance().createPin(noteText, board, pinImageUrl,"http://www.toukanmango.com", new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {
                    Log.d(getClass().getName(), response.getData().toString());


                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e(getClass().getName(), exception.getDetailMessage());

                }
            });
        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
