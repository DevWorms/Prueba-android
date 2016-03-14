package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.activities.Login;
import com.devworms.editorial.mango.activities.MainActivity;
import com.devworms.editorial.mango.util.Specs;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

//import com.parse.ParseUser;

/**
 * Created by sergio on 21/10/15.
 */
public class CuentaFragment extends Fragment{

    TargetImageView imgPerfil, imgBarras;
    ImageView imgTarjeta;

    Button btnCerrarSesion;
    TextView txtNombreUsuario, txtCorreoElectronico, txtSubscripcion, txtReferenciaBarras, txt_brand, txt_holder, txt_card_number;
    LinearLayout ly_barras, ly_tarjeta;

    String clientId;

    public void loadFBProfileImage(String userid){

            URL img_value = null;

            String url = "http://graph.facebook.com/"+userid+"/picture?type=large";

            FastImageLoader.prefetchImage(url, Specs.IMG_IX_IMAGE);
            ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);

            imgPerfil.loadImage(url, spec.getKey());

    }

    public void getFBUserData(){

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String id = response.getJSONObject().getString("id");
                            txtNombreUsuario.setText(response.getJSONObject().getString("name"));
                            txtCorreoElectronico.setText(response.getJSONObject().getString("email"));
                            loadFBProfileImage(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void getTWUserData(){

    }

    public void getParseUserData(){

    }

    public void cargarInformacion(){
        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            getFBUserData();
        }else if(ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())){
            getTWUserData();
        }
        else{
            getParseUserData();
        }

    }

    public void obtenerClienteParse(final View view){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> clientList, ParseException e) {
                if (e == null) {

                    final ParseObject objCliente = clientList.get(0);

                    if (objCliente == null) {
                        initControls(view, objCliente, null);
                    }
                    else{


                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", objCliente);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> tarjetasList, ParseException e) {
                                    if (e == null) {
                                        ParseObject objTarjeta = tarjetasList.get(0);
                                        initControls(view, objCliente, objTarjeta);
                                    }
                                }
                            });
                    }
                }
            }
        });

    }


    public void initControls(View view, ParseObject objCliente,ParseObject objTarjeta){
        imgPerfil = (TargetImageView)view.findViewById(R.id.imagenPerfil);
        imgBarras = (TargetImageView)view.findViewById(R.id.imagenBarras);

        imgTarjeta = (ImageView)view.findViewById(R.id.imagenTarjeta);

        btnCerrarSesion = (Button)view.findViewById(R.id.cerrarSesionBtn);

        txtNombreUsuario = (TextView)view.findViewById(R.id.txt_nombreUsuario);
        txtCorreoElectronico = (TextView)view.findViewById(R.id.txt_correoElectronico);
        txtSubscripcion = (TextView)view.findViewById(R.id.txt_subscripcion);
        txtReferenciaBarras = (TextView)view.findViewById(R.id.txt_referenciaBarras);
        txt_brand = (TextView)view.findViewById(R.id.txt_brand);
        txt_holder = (TextView)view.findViewById(R.id.txt_holder);
        txt_card_number = (TextView)view.findViewById(R.id.txt_card_number);

        ly_barras = (LinearLayout)view.findViewById(R.id.layout_barras);
        ly_tarjeta = (LinearLayout)view.findViewById(R.id.layout_card);

        ly_barras.setVisibility(View.GONE);
        ly_tarjeta.setVisibility(View.GONE);


        imgTarjeta.setImageResource(R.mipmap.tarjeta);



        if (objCliente != null){
            txtSubscripcion.setText(objCliente.getBoolean("Suscrito") ? "Suscrito":"Sin inscripción actual");
            String barras = objCliente.getString("codigobarras");
            if (barras != null && !barras.equals("")){
                ly_barras.setVisibility(View.VISIBLE);

                clientId = objCliente.getString("clientID");
                FastImageLoader.prefetchImage(barras, Specs.IMG_IX_IMAGE);
                ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);

                imgBarras.loadImage(barras, spec.getKey());
                txtReferenciaBarras.setText(objCliente.getString("referenciaentienda"));

            }
        }

        if (objTarjeta != null){
            ly_tarjeta.setVisibility(View.VISIBLE);
            txt_brand.setText(objTarjeta.getString("brand"));
            txt_holder.setText(objCliente.getString("nombre"));
            txt_card_number.setText(objTarjeta.getString("numero"));
        }

        btnCerrarSesion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, new CuentaFragment())
                                .addToBackStack("cuenta")
                                .commit();
                    }
                });


            }
        });

        cargarInformacion();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view=null;

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            view=inflater.inflate(R.layout.fragment_contaco_detalles, container, false);
            obtenerClienteParse(view);
        } else {
            view=inflater.inflate(R.layout.fragment_contacto, container, false);
        }

        return view;
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }



}
