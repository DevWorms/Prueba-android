package com.devworms.editorial.mango.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.MyBoardsActivity;
import com.devworms.editorial.mango.fragments.RecetaFragment;
import com.devworms.editorial.mango.main.StarterApplication;
import com.devworms.editorial.mango.util.Specs;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class CompartirDialog extends Dialog implements View.OnClickListener {


    public ParseObject objReceta;
    TargetImageView imgView;
    private PDKClient pdkClient;
    private Context context;
    public boolean desadeMenuPrincipal = false;

    public  CompartirDialog dialogo;
    public CompartirDialog(Context context, ParseObject objReceta, boolean desadeMenuPrincipal) {
        super(context);
        this.context = context;
        this.desadeMenuPrincipal = desadeMenuPrincipal;
        setCancelable(true);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Aqui haces que tu layout se muestre como dialog
        setContentView(R.layout.dialog_compartir);

        ImageButton buttonFb = (ImageButton) findViewById(R.id.bFacebook);
        ImageButton buttonTw = (ImageButton) findViewById(R.id.bTwitter);
        ImageButton buttonPin = (ImageButton) findViewById(R.id.bPinterest);
        imgView = (TargetImageView) findViewById(R.id.imgReceta);
        dialogo = this;

        buttonFb.setImageResource(R.drawable.fb);
        buttonTw.setImageResource(R.drawable.twittert);
        buttonPin.setImageResource(R.drawable.pinterest);

        buttonFb.setOnClickListener(this);
        buttonTw.setOnClickListener(this);
        buttonPin.setOnClickListener(this);

        FastImageLoader.prefetchImage(objReceta.getString("Url_Imagen"), Specs.IMG_IX_IMAGE);
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
        imgView.loadImage(objReceta.getString("Url_Imagen"), spec.getKey());


        // mViewPager = (ViewPager) view.findViewById(R.id.containerCompartir);
        // mViewPager.setPageTransformer(true, new RelaxTransformer());
        // mViewPager.setPageTransformer(true,  new DepthPageTransformer ());
        // this.listaRecetasPorMenu = new HashMap<>();

        pdkClient = PDKClient.configureInstance(context, "4815040272566075428");
        pdkClient.onConnect(context);
        pdkClient.setDebugMode(true);

        this.objReceta = objReceta;
    }



    @Override
    public void cancel() {
        super.cancel();

        StarterApplication.objReceta = null;
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

        ShareDialog shareDialog = new ShareDialog((Activity) context);
        StarterApplication.callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(StarterApplication.callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                cancel();

                if(desadeMenuPrincipal) {

                    View drawingView = imgView;
                    drawingView.buildDrawingCache(true);
                    final Bitmap imgReceta = drawingView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                    drawingView.destroyDrawingCache();


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
                    query.whereEqualTo("Menu", objReceta);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> recetasList, ParseException e) {
                            if (e == null) {

                                if (recetasList.size() > 0) {

                                    ParseObject objRecetaLocal = recetasList.get(0);
                                    RecetaFragment receta = new RecetaFragment();
                                    receta.setObjReceta(objRecetaLocal);
                                    receta.setImgReceta(imgReceta);

                                    ((Activity) context).getFragmentManager().beginTransaction()
                                            .replace(R.id.actividad, receta)
                                            .addToBackStack("MenuFragment")
                                            .commit();
                                }

                            }

                        }
                    });
                }


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if(ShareDialog.canShow((ShareLinkContent.class))){
            shareDialog.show(content);
        }


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

                StarterApplication.objReceta = null;
                StarterApplication.bViral =  false;


                try {


                    //Pop intent
                    Intent in1 = new Intent(context, MyBoardsActivity.class);

                    in1.putExtra("desadeMenuPrincipal", desadeMenuPrincipal);

                    in1.putExtra("actividad",(Serializable) context );

                    in1.putExtra("url_imagen", objReceta.getString("Url_Imagen"));
                    in1.putExtra("idObjetoParse", objReceta.getObjectId());

                    context.startActivity(in1);

                    cancel();


                } catch (Exception e) {
                    Log.e("error", e.getMessage());

                }

            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                StarterApplication.objReceta = null;
                StarterApplication.bViral =  false;
            }
        });
    }
}
