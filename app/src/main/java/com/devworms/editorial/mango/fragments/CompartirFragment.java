package com.devworms.editorial.mango.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.MyBoardsActivity;
import com.devworms.editorial.mango.efectos.RelaxTransformer;
import com.devworms.editorial.mango.main.StarterApplication;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseObject;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.Utils;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CompartirFragment extends Fragment implements View.OnClickListener {

    private ViewPager mViewPager;
    public ParseObject objReceta;
    public Bitmap imgReceta;
    public boolean opcionViral;
    ImageView imgView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_compartir, container, false);

        ImageButton buttonFb = (ImageButton) view.findViewById(R.id.bFacebook);
        ImageButton buttonTw = (ImageButton) view.findViewById(R.id.bTwitter);
        ImageButton buttonPin = (ImageButton) view.findViewById(R.id.bPinterest);
        imgView = (ImageView) view.findViewById(R.id.imgReceta);


        buttonFb.setImageResource(R.drawable.fb);
        buttonTw.setImageResource(R.drawable.twittert);
        buttonPin.setImageResource(R.drawable.pinterest);

        buttonFb.setOnClickListener(this);
        buttonTw.setOnClickListener(this);
        buttonPin.setOnClickListener(this);

        imgView.setImageBitmap(imgReceta);

       // mViewPager = (ViewPager) view.findViewById(R.id.containerCompartir);
       // mViewPager.setPageTransformer(true, new RelaxTransformer());
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


        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://appcocina.parseapp.com"))
                .setContentTitle("Frida te invita")
                .setContentDescription("¡Esta receta me encanta!")
                .setImageUrl(Uri.parse(objReceta.getString("Url_Imagen")))
                .build();

        ShareDialog.show(this.getActivity(), content);


    }

    public void compartirTw()
    {
        try {


            BitmapDrawable image = (BitmapDrawable) imgView.getDrawable();

            OutputStream outStream = null;

            String extStorageDirectory = Environment
                    .getExternalStorageDirectory().toString();

            File file = new File(extStorageDirectory, "image.PNG");

            outStream = new FileOutputStream(file);
            imgReceta.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();


            Uri ImageUri = Uri.fromFile(file);

            //  File myImageFile = new File("/path/to/image");
            // Uri myImageUri = Uri.fromFile(myImageFile);
            TweetComposer.Builder builder = null;

            builder = new TweetComposer.Builder(this.getActivity())
                    .text("¡Me encanta esta receta!")
                    .url(new URL("http://appcocina.parseapp.com"))
                    .image(ImageUri);
            builder.show();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }



    }

    public void compartirPin()
    {

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);


        StarterApplication.pdkClient.login(getActivity(), scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());

                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    imgReceta.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();


                    //Pop intent
                    Intent in1 = new Intent(getActivity(), MyBoardsActivity.class);
                    in1.putExtra("image", filename);
                    startActivity(in1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });


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
