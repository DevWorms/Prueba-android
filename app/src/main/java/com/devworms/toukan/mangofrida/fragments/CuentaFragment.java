package com.devworms.toukan.mangofrida.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.activities.Login;
import com.devworms.toukan.mangofrida.activities.MainActivity;
import com.devworms.toukan.mangofrida.componentes.IabHelper;
import com.devworms.toukan.mangofrida.dialogs.Usuario;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.openpay.OpenPayRestApi;
import com.devworms.toukan.mangofrida.util.Specs;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CuentaFragment extends Fragment implements View.OnClickListener, BillingProcessor.IBillingHandler {

    TargetImageView imgPerfil, imgBarras;
    ImageView imgTarjeta;
    static Activity activity;
    TextView usuario;
    TextView password;

    static BillingProcessor bp;
    static String TAG = "InAppBilling";
    static IabHelper mHelper;
    static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwMPI5U2E7s8mNJiCTK53UiZ1WE/bSqvfASGu8SbpPrInis56J2pn6uaxIJIPBfleiSCN4fd9O2uK8/Vt6cpztfvvUWHbDZ6MLtMh3hBSFDZjYxpIYsanA2R02kklnD6NDs1ONb3XDXgl0NbYPKFgoIPgoMMa6wH7WLZQjh9oCKl8cOMQxOjVQcJwR7voZHAUU0gSofg463ztFIa2CzW0gbZ80tSq7+vQerDx2rdcs/t28fOt9gRKzK0JTdN/lv5umSBFCsVlIBseiswmdjNCqzYf6hkYIq1KZ5llbUeXctTNWAXKve/3qRfc5LC/oVkuFS69V2I6WrWIBGNDySqp1wIDAQAB";
    static String ITEM_SKU = "com.devworms.toukan.mangofrida.suscripcion";
    static Context ctx;
    static IInAppBillingService mService;
    static boolean isSuscribed;


    ImageView btnCerrarSesion, btnCancelarSuscripcion, btnEliminarTarjeta;
    Button btnFb, btnMail, btnTwitter;
    TextView txtNombreUsuario, txtCorreoElectronico, txtSubscripcion, txtReferenciaBarras, txt_brand, txt_holder, txt_card_number, btnNuevoUsuario, btnForgot;
    ;
    LinearLayout ly_barras, ly_tarjeta, ly_botones;

    String clientId;

    public void loadFBProfileImage(String userid) {
        try {
            String url = "http://graph.facebook.com/" + userid + "/picture?type=large";

            StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

            FastImageLoader.prefetchImage(url, Specs.IMG_IX_IMAGE);
            ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);

            imgPerfil.loadImage(url, spec.getKey());
        } catch (Exception ex) {
            imgPerfil.setImageResource(R.drawable.frida);
        }
    }

    public void getFBUserData() {
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
                            txtSubscripcion.setText(checkSuscription(mService) ? "Suscrito" : "Sin inscripción actual");
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

    public void getTWUserData() {
        com.parse.twitter.Twitter twitter = ParseTwitterUtils.getTwitter();
        try {
            JSONObject response = new RequestTwitter().execute(twitter.getUserId()).get();

            String profileImageUrl = response.getString("profile_image_url").replace("_normal", "");

            String fullName = response.getString("name");
            String username = response.getString("screen_name");

            loadTwProfileImage(profileImageUrl);
            txtNombreUsuario.setText(fullName);
            txtCorreoElectronico.setText("@" + username);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFb:
                loguearConFacebook();
                break;
            case R.id.btnTw:
                loguearConTwitter();
                break;
            case R.id.btnMail:
                loguearConMail();
                break;
            case R.id.reg:
                registrarUsuario();
                break;
            case R.id.forgot_password:
                recuperarContrasena();
                break;
        }
    }

    public void loadTwProfileImage(String imageUrl) {
        String url = imageUrl;

        try {
            imgPerfil.setImageBitmap(new GetBitmapFromURL().execute(url).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void getParseUserData() {
        imgPerfil.setImageResource(R.drawable.frida);
        txtCorreoElectronico.setText(ParseUser.getCurrentUser().getEmail());
        txtNombreUsuario.setVisibility(View.INVISIBLE);
        txtSubscripcion.setText(checkSuscription(mService) ? "Suscrito" : "Sin inscripción actual");
    }

    public void cargarInformacion() {
        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            getFBUserData();
        } else if (ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
            getTWUserData();
        } else {
            getParseUserData();
        }
    }

    public void obtenerClienteParse(final View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> clientList, ParseException e) {
                if (e == null) {
                    if (clientList.size() <= 0) {
                        initControls(view, null, null);
                    } else {
                        final ParseObject objCliente = clientList.get(0);
                        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", objCliente);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> tarjetasList, ParseException e) {
                                if (e == null) {
                                    ParseObject objTarjeta = null;

                                    if (tarjetasList.size() > 0) {
                                        objTarjeta = tarjetasList.get(0);
                                    }

                                    initControls(view, objCliente, objTarjeta);
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    public void initControls(View view, ParseObject objCliente, ParseObject objTarjeta) {
        imgPerfil = (TargetImageView) view.findViewById(R.id.imagenPerfil);
        imgBarras = (TargetImageView) view.findViewById(R.id.imagenBarras);

        imgTarjeta = (ImageView) view.findViewById(R.id.imagenTarjeta);

        btnCerrarSesion = (ImageView) view.findViewById(R.id.cerrarSesionBtn);
        btnCancelarSuscripcion = (ImageView) view.findViewById(R.id.btnCancelarSuscripcion);
        btnEliminarTarjeta = (ImageView) view.findViewById(R.id.btnEliminarTarjeta);

        txtNombreUsuario = (TextView) view.findViewById(R.id.txt_nombreUsuario);
        txtCorreoElectronico = (TextView) view.findViewById(R.id.txt_correoElectronico);
        txtSubscripcion = (TextView) view.findViewById(R.id.txt_subscripcion);

        txtReferenciaBarras = (TextView) view.findViewById(R.id.txt_referenciaBarras);
        txt_brand = (TextView) view.findViewById(R.id.txt_brand);
        txt_holder = (TextView) view.findViewById(R.id.txt_holder);
        txt_card_number = (TextView) view.findViewById(R.id.txt_card_number);

        ly_barras = (LinearLayout) view.findViewById(R.id.layout_barras);
        ly_tarjeta = (LinearLayout) view.findViewById(R.id.layout_card);
        ly_botones = (LinearLayout) view.findViewById(R.id.layout_card_buttons);

        ly_barras.setVisibility(View.GONE);
        ly_tarjeta.setVisibility(View.INVISIBLE);
        ly_botones.setVisibility(View.INVISIBLE);

        imgTarjeta.setImageResource(R.drawable.tarjetau);
        activity = getActivity();

        if (objCliente != null) {
            txtSubscripcion.setText(checkSuscription(mService) ? "Suscrito" : "Sin inscripción actual");
            String barras = objCliente.getString("codigobarras");
            if (barras != null && !barras.equals("")) {
                ly_barras.setVisibility(View.VISIBLE);

                clientId = objCliente.getString("clientID");
                FastImageLoader.prefetchImage(barras, Specs.IMG_IX_IMAGE);
                ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);

                imgBarras.loadImage(barras, spec.getKey());
                txtReferenciaBarras.setText(objCliente.getString("referenciaentienda"));
            }

            if (!objCliente.getBoolean("Suscrito")) {
                btnCancelarSuscripcion.setVisibility(View.INVISIBLE);
            }
        }

        if (objTarjeta != null) {
            ly_tarjeta.setVisibility(View.VISIBLE);
            ly_botones.setVisibility(View.VISIBLE);
            txt_brand.setText(objTarjeta.getString("brand"));
            txt_holder.setText(objCliente.getString("nombre"));
            txt_card_number.setText(objTarjeta.getString("numero"));
            btnCancelarSuscripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenPayRestApi.cancelarSuscripcion(activity, txtSubscripcion, v);
                }
            });

            btnEliminarTarjeta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenPayRestApi.eliminarTarjeta(activity, v, ly_tarjeta);
                }
            });
        }

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(getActivity(), Login.class);
                        startActivity(intent);

                        getActivity().finish();
                    }
                });
            }
        });
        cargarInformacion();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        ParseUser currentUser = ParseUser.getCurrentUser();
        isSuscribed = getArguments().getBoolean("isSuscribed");
        activity = getActivity();
        ctx = activity.getApplicationContext();

        bp = new BillingProcessor(ctx, base64EncodedPublicKey, this);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        ctx.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        if (currentUser != null) {
            view = inflater.inflate(R.layout.fragment_contaco_detalles, container, false);
            obtenerClienteParse(view);
        } else {
            view = inflater.inflate(R.layout.fragment_contacto, container, false);
            usuario = ((TextView) view.findViewById(R.id.usuario));
            password = ((TextView) view.findViewById(R.id.password));

            btnFb = ((Button) view.findViewById(R.id.btnFb));
            btnMail = ((Button) view.findViewById(R.id.btnTw));
            btnTwitter = ((Button) view.findViewById(R.id.btnMail));
            btnNuevoUsuario = ((TextView) view.findViewById(R.id.reg));
            btnForgot = ((TextView) view.findViewById(R.id.forgot_password));

            btnFb.setOnClickListener(this);
            btnMail.setOnClickListener(this);
            btnTwitter.setOnClickListener(this);
            btnNuevoUsuario.setOnClickListener(this);
            btnForgot.setOnClickListener(this);
        }

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.INVISIBLE);

        /*
        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.INVISIBLE);
        */

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.INVISIBLE);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setBackgroundColor(getResources().getColor(R.color.barraSecundaria));

        return view;
    }

    public void loguearConMail() {
        final Activity activity = getActivity();
        String userName = "";
        String pass = "";

        if (usuario.getText().toString() == null || password.getText() == null ||
                usuario.getText().toString().toString().equals("") || password.getText().toString().equals("")) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this.getActivity(), R.style.myDialog));

            // set title
            alertDialogBuilder.setTitle("Faltan datos por llenar");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Debe ingresar correo y contraseña")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            userName = usuario.getText().toString();
            pass = password.getText().toString();

            ParseUser.logInInBackground(userName, pass, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, new CuentaFragment())
                                .addToBackStack("cuenta")
                                .commit();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));

                        // set title
                        alertDialogBuilder.setTitle("Error");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Revise sus datos o su conexion a internet")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                }
            });
        }
    }

    public void recuperarContrasena() {
        final Activity actividad = this.getActivity();
        final Dialog dialog = new Dialog(actividad);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog

        dialog.setContentView(R.layout.dialog_recuperar_contrasena);
        ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
                dialog.closeOptionsMenu();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {
            private EditText txtCorreo;

            @Override
            public void onClick(View view) {

                txtCorreo = (EditText) dialog.findViewById(R.id.txtCorreo);

                if (txtCorreo.getText() == null || txtCorreo.getText().toString().equals("")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                    // set title
                    alertDialogBuilder.setTitle("Error");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Debe ingresar un correo")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    Usuario usuario = new Usuario(txtCorreo, null, null);
                    usuario.recuperarContrasena(actividad, dialog);
                }

            }
        });

        dialog.show();
    }

    public void registrarUsuario() {

        final Activity actividad = this.getActivity();
        final Dialog dialog = new Dialog(actividad);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Aqui haces que tu layout se muestre como dialog

        dialog.setContentView(R.layout.dialog_usuario);
        ((Button) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
                dialog.closeOptionsMenu();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {
            private EditText txtCorreo, txtPass, txtPassConfirm;

            @Override
            public void onClick(View view) {

                txtCorreo = (EditText) dialog.findViewById(R.id.txtCorreo);
                txtPass = (EditText) dialog.findViewById(R.id.password);
                txtPassConfirm = (EditText) dialog.findViewById(R.id.passwordConfirm);

                if (txtCorreo.getText() == null || txtPass.getText() == null || txtCorreo.getText().toString().equals("") || txtPass.getText().toString().equals("")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                    // set title
                    alertDialogBuilder.setTitle("Error");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Debe llenar los datos")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    Usuario usuario = new Usuario(txtCorreo, txtPass, txtPassConfirm);
                    usuario.nuevoUsuario(actividad);
                }
            }
        });

        dialog.show();
    }

    public void loguearConFacebook() {
        List<String> permissions = Arrays.asList("user_birthday", "user_location", "user_friends", "email", "public_profile");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");

                    ligarFBconParse(user);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, new CuentaFragment())
                            .addToBackStack("cuenta")
                            .commit();
                } else {
                    ligarFBconParse(user);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, new CuentaFragment())
                            .addToBackStack("cuenta")
                            .commit();

                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }


        });


    }

    private void ligarFBconParse(final ParseUser user) {
        List<String> permissions = Arrays.asList("user_birthday", "user_location", "user_friends", "email", "public_profile");

        if (!ParseFacebookUtils.isLinked(user)) {
            ParseFacebookUtils.linkWithReadPermissionsInBackground(user, getActivity(), permissions, new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseFacebookUtils.isLinked(user)) {
                        Log.d("MyApp", "Woohoo, user logged in with Facebook!");
                    }
                }
            });
        }
    }

    public void loguearConTwitter() {
        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    ligarConTwitter(user);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, new CuentaFragment())
                            .addToBackStack("cuenta")
                            .commit();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                } else {
                    ligarConTwitter(user);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.actividad, new CuentaFragment())
                            .addToBackStack("cuenta")
                            .commit();
                    Log.d("MyApp", "User logged in through Twitter!");
                }
            }
        });
    }

    private void ligarConTwitter(final ParseUser user) {
        if (!ParseTwitterUtils.isLinked(user)) {
            ParseTwitterUtils.link(user, getActivity(), new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseTwitterUtils.isLinked(user)) {
                        Log.d("MyApp", "Woohoo, user logged in with Twitter!");
                    }
                }
            });
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    // conexion a internet
    private class RequestTwitter extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet verifyGet = new HttpGet(
                        "https://api.twitter.com/1.1/users/show.json?user_id=" + params[0]);
                ParseTwitterUtils.getTwitter().signRequest(verifyGet);
                HttpResponse response = client.execute(verifyGet);
                InputStream is = response.getEntity().getContent();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                JSONObject responseJson = new JSONObject(responseStrBuilder.toString());

                return responseJson;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // conexion a internet
    private class GetBitmapFromURL extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void notification(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    public static boolean checkSuscription(IInAppBillingService service) {
        Boolean isSuscribed = false;

        try {
            Bundle ownedItems = service.getPurchases(3, activity.getPackageName(), "subs", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                if (purchaseDataList.size() > 0) {
                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        String purchaseData = purchaseDataList.get(i);
                        String sku = ownedSkus.get(i);

                        if (sku.equals(ITEM_SKU)) { // Si ha adquirido la suscribción
                            JSONObject data = new JSONObject(purchaseData);
                            //Date fecha = new Date(Long.parseLong(data.getString("purchaseTime")));
                            Integer status = data.getInt("purchaseState");

                            //if (differenceInDays(fecha) > 7) {
                            if (status.equals(0)) {
                                // Ya a adquirido la suscripcion, el tiempo de prueba ya paso, y su suscripcion está activa
                                isSuscribed = true;
                            }
                            /*} else {
                                // Ya a adquirido la suscripción, pero se encuentra en el periodo de prueba
                                isSuscribed = false;
                            }*/
                            break;
                        }
                    }
                } else { // Nunca ha adquirido ninguna suscripción
                    Log.e("Subscription", "Sin elementos");
                }
            } else { // Código de respuesta != 0
                Log.e("Subscription", "Respuesta: " + response);
            }
        } catch (RemoteException | JSONException e) {
            Log.e("Subscription", e.getMessage());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return isSuscribed;
    }
}
