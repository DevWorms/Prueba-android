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

import java.io.Serializable;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    IInAppBillingService mService;
    private static final long serialVersionUID = 1L;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("user_data", MODE_PRIVATE);

        if(!sp.getBoolean("calificado",false)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Te gusto?  CALIFICANOS!!");

            builder.setPositiveButton("CALIFICAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences.Editor e = sp.edit();

                    e.putBoolean("calificado",true);
                    e.apply();

                    String url = "https://play.google.com/store";
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

                Log.d("Here", "1");

                query.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        RecetarioFragment recetario = new RecetarioFragment();
                        recetario.setMenuSeleccionado(StarterApplication.objReceta);
                        recetario.setTipoMenu("gratis");
                        Log.d("Here", "2");

                        getSupportFragmentManager().beginTransaction()
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
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new CuentaFragment()).commit();
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
}
