package com.devworms.editorial.mango.componentes;

/**
 * Created by DevWorms S.A. de C.V. on 20/10/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.parse.ParseObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class CustomListParse extends ArrayAdapter<ParseObject> {

    private final Activity context;
    private List<ParseObject> lParseObjects;
    private ProgressDialog mDialog;
    private HashMap<String, Bitmap>listaImagenes;

    public CustomListParse(Activity context, List<ParseObject> lParseObjects) {
        super(context, R.layout.list_single, lParseObjects);
        this.context = context;
        this.lParseObjects = lParseObjects;
        this.listaImagenes = new HashMap<>();
        this.mDialog = new ProgressDialog(context);
        this.mDialog.setMessage("Descargando");
        this.mDialog.setCancelable(false);
        this.mDialog.show();

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        //TextView txtTitle = (TextView) rowView.findViewById(R.id.txtstep);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        // txtTitle.setText(web[position]);

        ParseObject objParse = this.lParseObjects.get(position);

        if (this.listaImagenes.get(objParse.getString("Url_Imagen")) == null)
            new DownloadImageTask(imageView).execute(objParse.getString("Url_Imagen"));
        else
            imageView.setImageBitmap(this.listaImagenes.get(objParse.getString("Url_Imagen")));




        return rowView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String urldisplay;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            listaImagenes.put(urldisplay, result);
            bmImage.setImageBitmap(result);
            bmImage.invalidate();
            mDialog.cancel();

        }

    }

}