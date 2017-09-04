package com.devworms.toukan.mangofrida.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.dialogs.CompartirDialog;
import com.devworms.toukan.mangofrida.fragments.CreditosFragment;
import com.devworms.toukan.mangofrida.fragments.CuentaFragment;
import com.devworms.toukan.mangofrida.fragments.FavoritosFragment;
import com.devworms.toukan.mangofrida.fragments.MenuFragment;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.fragments.RegalosFragment;
import com.devworms.toukan.mangofrida.fragments.SearchResultsFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    IInAppBillingService mService;
    private static final long serialVersionUID = 1L;
    SharedPreferences sp;

    static String ITEM_SKU = "com.devworms.toukan.mangofrida.suscripcion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("user_data", MODE_PRIVATE);

        if(!sp.getBoolean("calificado",false)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("¿Te gusto?  Califícanos!!");

            builder.setPositiveButton("CALIFICAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences.Editor e = sp.edit();

                    e.putBoolean("calificado",true);
                    e.apply();

                    String url = "https://play.google.com/store/apps/details?id=com.devworms.toukan.mangofrida";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
            });
            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }



        //*************************In-App billing
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //*****************************************
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ///***************Barra***************************************************
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.barraPincipal));

        ///***************Menu***************************************************
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ImageView imagen = (ImageView) hView.findViewById(R.id.globo);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                getFragmentManager().beginTransaction().replace(R.id.actividad, new MenuFragment()).commit();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.menuItem));

        ///***************Fragment***************************************************

        getFragmentManager().beginTransaction().replace(R.id.actividad, new MenuFragment()).commit();
        ///***************Fragment***************************************************

        if (StarterApplication.bViral) {
            CompartirDialog compartirDialog = new CompartirDialog(this, StarterApplication.objReceta);
            compartirDialog.show();
        }

        // esto esta aqui porq pinterest recarga el activity
        abrirReceta();

        handleIntent(getIntent());

    }

    public void abrirReceta() {
        if (StarterApplication.bCompartido && StarterApplication.objReceta != null) {

            if (StarterApplication.isDesdeMenuPrincipal) {
                StarterApplication.bViral = false;
                StarterApplication.bCompartido = false;

                Calendar cal = Calendar.getInstance();
                int year = cal.get(cal.YEAR);
                int month = cal.get(cal.MONTH) + 1;
                int trimestre = (int) (((month) / 3) + 0.7);

                ParseObject query = new ParseObject("Regalos");
                query.put("username", ParseUser.getCurrentUser());
                query.put("Anio", year);
                query.put("Mes", month);
                query.put("Trimestre", trimestre);
                query.put("Recetario", StarterApplication.objReceta);

                query.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        RecetarioFragment recetario = new RecetarioFragment();
                        recetario.setSuscribed(checkSuscription(mService));
                        recetario.setMenuSeleccionado(StarterApplication.objReceta);
                        recetario.setTipoMenu("gratis");

                        getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, recetario)
                                .addToBackStack("MenuFragment")
                                .commit();

                        StarterApplication.objReceta = null;
                    }
                });
            } else {
                final RecetaFragment receta = new RecetaFragment();
                ParseObject parseObjectTemp = StarterApplication.objReceta;
                receta.setObjReceta(parseObjectTemp);
                getFragmentManager().beginTransaction()
                        .replace(R.id.actividad, receta).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchResultsFragment fragmentSearch = new SearchResultsFragment();
            fragmentSearch.query = query.split(" ");

            getFragmentManager().beginTransaction()
                    .addToBackStack("MenuFragment")
                    .replace(R.id.actividad, fragmentSearch).commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        if (StarterApplication.pdkClient != null) {
            StarterApplication.pdkClient.onOauthResponse(requestCode, resultCode,
                    data);
        }

        if (StarterApplication.callbackManager != null) {
            StarterApplication.callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        int TWEET_COMPOSER_REQUEST_CODE = 100;

        if (requestCode == TWEET_COMPOSER_REQUEST_CODE && StarterApplication.bCompartidoTwitter) {
            StarterApplication.bCompartidoTwitter = false;
            final RecetaFragment receta = new RecetaFragment();

            final ParseObject objParse = StarterApplication.objReceta;

            if (StarterApplication.dialogoCompartir != null) {
                StarterApplication.dialogoCompartir.cancel();
                StarterApplication.dialogoCompartir.invalidateOptionsMenu();
            }

            abrirReceta();
        } else {
            StarterApplication.bCompartido = false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            getFragmentManager().beginTransaction().replace(R.id.actividad, new MenuFragment()).commit();
        } else if (id == R.id.nav_favoritos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new FavoritosFragment()).commit();
        } else if (id == R.id.nav_regalos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new RegalosFragment()).commit();
        } else if (id == R.id.nav_cuenta) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSuscribed", checkSuscription(mService));

            CuentaFragment fragment = new CuentaFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, fragment).commit();
        } else if (id == R.id.nav_creditos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new CreditosFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (StarterApplication.bCompartido && StarterApplication.objReceta != null) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            finish();
            if (StarterApplication.dialogoCompartir != null) {
                StarterApplication.dialogoCompartir.cancel();
                StarterApplication.dialogoCompartir.invalidateOptionsMenu();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
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

    private void notification(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public Boolean checkSuscription(IInAppBillingService service) {
        Boolean isSuscribed = false;

        try {
            Bundle ownedItems = service.getPurchases(3, getPackageName(), "subs", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String>  signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

                if (purchaseDataList.size() > 0) {
                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        String purchaseData = purchaseDataList.get(i);
                        String sku = ownedSkus.get(i);

                        if (sku.equals(ITEM_SKU)) { // Si ha adquirido la suscribción
                            JSONObject data = new JSONObject(purchaseData);
                            //Date fecha = new Date(Long.parseLong(data.getString("purchaseTime")));
                            Integer status = data.getInt("purchaseState");

                            //notification("Status" + status.toString());

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

    private Integer differenceInDays(Date suscriptionDate) {
        Long now = new Date().getTime();
        Long startTime = suscriptionDate.getTime();
        Long diffDays = (now - startTime) / (1000 * 60 * 60 * 24);
        return Integer.parseInt(diffDays.toString());
    }
}
