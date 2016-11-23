package com.devworms.toukan.mangofrida.dialogs;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.activities.MyBoardsActivity;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.Specs;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
import java.util.Calendar;
import java.util.List;


public class CompartirDialog extends Dialog implements View.OnClickListener {


    public ParseObject objReceta;
    TargetImageView imgView;
    private PDKClient pdkClient;
    private FragmentActivity context;

    public CompartirDialog(FragmentActivity context, ParseObject objReceta) {
        super(context);
        this.context = context;

        setCancelable(true);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog


        //Aqui haces que tu layout se muestre como dialog
        setContentView(R.layout.dialog_compartir);


        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        ImageView buttonFb = (ImageView) findViewById(R.id.bFacebook);
        ImageView buttonTw = (ImageView) findViewById(R.id.bTwitter);
        ImageView buttonPin = (ImageView) findViewById(R.id.bPinterest);
        imgView = (TargetImageView) findViewById(R.id.imgReceta);


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

        if(StarterApplication.isDesdeMenuPrincipal){
            ((ImageView)findViewById(R.id.imgComparte)).setImageResource(R.drawable.trofeoc);
        }else{
            ((ImageView)findViewById(R.id.imgComparte)).setImageResource(R.drawable.compartec);
        }

        this.objReceta = objReceta;


    }



    @Override
    public void cancel() {
        super.cancel();

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
                .setContentUrl(Uri.parse("http://newmobage.com/app_cocina/"))
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

                if(StarterApplication.isDesdeMenuPrincipal) {


                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(cal.YEAR);
                    int month = cal.get(cal.MONTH)+1;
                    int trimestre = (int)(((month)/3) + 0.7);

                    ParseObject query = new ParseObject("Regalos");
                    query.put("username", ParseUser.getCurrentUser());
                    query.put("Anio", year);
                    query.put("Mes", month);
                    query.put("Trimestre", trimestre);
                    query.put("Recetario", objReceta);

                    query.saveInBackground(new SaveCallback() {
                       @Override
                       public void done(ParseException e) {


                           RecetarioFragment recetario = new RecetarioFragment();
                           recetario.setMenuSeleccionado(objReceta);
                           recetario.setTipoMenu("gratis");

                           context.getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.actividad,recetario)
                                   .addToBackStack("MenuFragment")
                                   .commit();



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
            if(!StarterApplication.isDesdeMenuPrincipal){
                final Intent intent = new TweetComposer.Builder(context)
                        .text("¡Me encanta esta receta!")
                        .url(new URL("http://newmobage.com/app_cocina/"))
                        .image(ImageUri).createIntent();
                StarterApplication.bCompartidoTwitter = true;
                final int TWEET_COMPOSER_REQUEST_CODE = 100;
                ((Activity) context).startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
            }else {
                final Intent intent = new TweetComposer.Builder(context)
                        .text("¡Me encanta esta receta!")
                        .url(new URL("http://newmobage.com/app_cocina/"))
                        .image(ImageUri).createIntent();

                final int TWEET_COMPOSER_REQUEST_CODE = 100;
                StarterApplication.objReceta = objReceta;
                StarterApplication.bCompartido = true;
                StarterApplication.bCompartidoTwitter = true;


                ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
                query.whereEqualTo("Menu", objReceta);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(final List<ParseObject> recetasList, ParseException e) {
                        if (e == null) {

                            if (recetasList.size() > 0) {

                                ((Activity) context).startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
                            }

                        }

                    }
                });


            }
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


                StarterApplication.bViral =  false;


                try {

                        //Pop intent
                        Intent in1 = new Intent(context, MyBoardsActivity.class);
                        in1.putExtra("url_imagen", objReceta.getString("Url_Imagen"));
                        context.startActivity(in1);

                } catch (Exception e) {
                    Log.e("error", e.getMessage());

                }

            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                StarterApplication.objReceta = null;
                StarterApplication.bCompartido =  false;
                StarterApplication.bViral =  false;
            }
        });
    }

}
