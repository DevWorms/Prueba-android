package com.devworms.toukan.mangofrida.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.dialogs.CompartirDialog;
import com.devworms.toukan.mangofrida.fragments.CreditosFragment;
import com.devworms.toukan.mangofrida.fragments.CuentaFragment;
import com.devworms.toukan.mangofrida.fragments.FavoritosFragment;
import com.devworms.toukan.mangofrida.fragments.MenuFragment;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RegalosFragment;
import com.devworms.toukan.mangofrida.fragments.SearchResultsFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {


    private static final long serialVersionUID = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ///***************Barra***************************************************
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.barraPincipal));

        /*ImageView imgFrida = (ImageView) findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.VISIBLE);
        ImageView imgTexto = (ImageView) findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.VISIBLE);


        ImageView imgFondoBarra = (ImageView) findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.VISIBLE);
        imgFondoBarra.setImageResource(R.drawable.fonsobar);

*/
        ///***************Barra***************************************************

        ///***************Menu***************************************************
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        View hView =
                navigationView.inflateHeaderView(R.layout.nav_header_main);

        ImageView imagen = (ImageView) hView.findViewById(R.id.globo);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                getFragmentManager().beginTransaction()
                        .replace(R.id.actividad, new MenuFragment()).commit();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.menuItem));


        ///***************Menu***************************************************

        ///***************Fragment***************************************************

        getFragmentManager().beginTransaction()
                .replace(R.id.actividad, new MenuFragment()).commit();
        ///***************Fragment***************************************************



        if(StarterApplication.bViral){
            CompartirDialog compartirDialog = new CompartirDialog(this, StarterApplication.objReceta, true);
            compartirDialog.show();
        }

        //esto esta aqui porq pinterest recarga el activity
        abrirReceta();

        handleIntent(getIntent());

    }

    public void abrirReceta(){
        if(StarterApplication.bCompartido && StarterApplication.objReceta != null)
        {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(cal.YEAR);
            int month = cal.get(cal.MONTH)+1;
            int trimestre = (int)(((month)/3) + 0.7);

            ParseObject query = new ParseObject("Regalos");
            query.put("username", ParseUser.getCurrentUser());
            query.put("Anio", year);
            query.put("Mes", month);
            query.put("Mes", month);
            query.put("Trimestre", trimestre);
            query.put("Mes", month);
            query.put("Recetario", StarterApplication.objReceta);

            query.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {


                    final RecetaFragment receta = new RecetaFragment();

                    final ParseObject objParse = StarterApplication.objReceta;

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
                    query.whereEqualTo("Menu", objParse);
                    query.whereEqualTo("Activada", true);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> menuList, ParseException e) {
                            if (e == null) {
                                if (menuList.size() > 0) {
                                    ParseObject parseObjectTemp = menuList.get(0);
                                    receta.setObjReceta(parseObjectTemp);
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.actividad, receta).commit();

                                    StarterApplication.bViral = false;
                                    StarterApplication.objReceta = null;
                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }

                            StarterApplication.bCompartido = false;
                        }
                    });
                }
            });

        }

    }


    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() == 0) {
            //super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    };




    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            Log.v("query", query);

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


        if(StarterApplication.pdkClient != null) {
            StarterApplication.pdkClient.onOauthResponse(requestCode, resultCode,
                    data);
        }

        if(StarterApplication.callbackManager != null){
            StarterApplication.callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        int TWEET_COMPOSER_REQUEST_CODE = 100;

        if(requestCode == TWEET_COMPOSER_REQUEST_CODE && StarterApplication.bCompartidoTwitter && resultCode == 1)
        {
            StarterApplication.bCompartidoTwitter = false;
            if(resultCode == RESULT_OK){
                final RecetaFragment receta = new RecetaFragment();

                final ParseObject objParse = StarterApplication.objReceta;

                abrirReceta();

            }
        }
        else{
            StarterApplication.bCompartido = false;
        }


    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_inicio) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new MenuFragment()).commit();
        } else if (id == R.id.nav_favoritos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new FavoritosFragment()).commit();
        }
            else if (id == R.id.nav_regalos) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.actividad, new RegalosFragment()).commit();

/*        } else if (id == R.id.nav_consejos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new ConsejosFragment()).commit();

*/        } else if (id == R.id.nav_cuenta) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new CuentaFragment()).commit();

        }  else if (id == R.id.nav_creditos) {
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

        if(StarterApplication.bCompartido && StarterApplication.objReceta != null)
        {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);//Start the same Activity
            finish(); //
            StarterApplication.dialogoCompartir.cancel();
            StarterApplication.dialogoCompartir.invalidateOptionsMenu();

        }
    }
}
