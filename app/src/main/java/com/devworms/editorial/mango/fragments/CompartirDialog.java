package com.devworms.editorial.mango.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.MyBoardsActivity;
import com.devworms.editorial.mango.main.StarterApplication;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseObject;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CompartirDialog extends Dialog implements View.OnClickListener {

    private ViewPager mViewPager;
    public ParseObject objReceta;
    public Bitmap imgReceta;
    public boolean opcionViral;
    ImageView imgView;
    private PDKClient pdkClient;
    private Context context;

    public CompartirDialog(Context context) {
        super(context);
        this.context = context;

        setCancelable(true);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Aqui haces que tu layout se muestre como dialog
        setContentView(R.layout.dialog_compartir);

        ImageButton buttonFb = (ImageButton) findViewById(R.id.bFacebook);
        ImageButton buttonTw = (ImageButton) findViewById(R.id.bTwitter);
        ImageButton buttonPin = (ImageButton) findViewById(R.id.bPinterest);
        imgView = (ImageView) findViewById(R.id.imgReceta);


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
        // this.listaRecetasPorMenu = new HashMap<>();

        pdkClient = PDKClient.configureInstance(context, "4815040272566075428");
        pdkClient.onConnect(context);
        pdkClient.setDebugMode(true);


    }


    @Override
    public void cancel() {
        super.cancel();

        StarterApplication.objReceta = null;
        StarterApplication.imgReceta =  null;
        StarterApplication.bViral =  false;

    }

    //Para asignar accion a los botones dentro del fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    private void compartirFb() {
        // compartir una imagen

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://appcocina.parseapp.com"))
                .setContentTitle("Frida te invita")
                .setContentDescription("¡Esta receta me encanta!")
                .setImageUrl(Uri.parse(objReceta.getString("Url_Imagen")))
                .build();

        ShareDialog.show((Activity) context, content);


    }

    public void compartirTw() {
        try {

            View drawingView = imgView;
            drawingView.buildDrawingCache(true);
            Bitmap bitmap = drawingView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
            drawingView.destroyDrawingCache();

            OutputStream outStream = null;

            String extStorageDirectory = Environment
                    .getExternalStorageDirectory().toString();

            File file = new File(extStorageDirectory, "image.PNG");

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();


            Uri ImageUri = Uri.fromFile(file);

            //  File myImageFile = new File("/path/to/image");
            // Uri myImageUri = Uri.fromFile(myImageFile);
            TweetComposer.Builder builder = null;

            builder = new TweetComposer.Builder(context)
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

    public void compartirPin() {

        StarterApplication.objReceta = objReceta;
        StarterApplication.imgReceta =  imgReceta;
        StarterApplication.bViral =  true;

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);


        pdkClient.login(context, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());

                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);

                    View drawingView = imgView;
                    drawingView.buildDrawingCache(true);
                    Bitmap bitmap = drawingView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                    drawingView.destroyDrawingCache();

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();

                    StarterApplication.objReceta = null;
                    StarterApplication.imgReceta =  null;
                    StarterApplication.bViral =  false;

                    //Pop intent
                    Intent in1 = new Intent(context, MyBoardsActivity.class);
                    in1.putExtra("image", filename);
                    context.startActivity(in1);


                } catch (Exception e) {
                    Log.e("error", e.getMessage());

                }

            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                StarterApplication.objReceta = null;
                StarterApplication.imgReceta =  null;
                StarterApplication.bViral =  false;
            }
        });
    }
}
