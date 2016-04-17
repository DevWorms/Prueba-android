package com.devworms.editorial.mango.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.componentes.AdapterBuscadorList;
import com.devworms.editorial.mango.fragments.ConsejosFragment;
import com.devworms.editorial.mango.fragments.CreditosFragment;
import com.devworms.editorial.mango.fragments.CuentaFragment;
import com.devworms.editorial.mango.fragments.FavoritosFragment;
import com.devworms.editorial.mango.fragments.MenuFragment;
import com.devworms.editorial.mango.fragments.SearchResultsFragment;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AdapterBuscadorList mAdapterRecetarioList;


    public void obtenerObjetosParse(final RecyclerView recyclerView, String []tags){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tags");

        for(String tag:tags){
            query.whereEqualTo("Tag", tag);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> recetasList, ParseException e) {
                if (e == null) {


                    final List<ParseObject> lItems = new ArrayList<ParseObject>();
                    final List<String> lTipos = new ArrayList<String>();

                    if (recetasList.size() > 0) {
                        for (ParseObject parseObject : recetasList) {

                            parseObject.getParseObject("Receta").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                public void done(final ParseObject receta, ParseException e) {
                                    lItems.add(receta);
                                    receta.getParseObject("Menu").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject menu, ParseException e) {
                                            lTipos.add(menu.getString("TipoMenu").toLowerCase());

                                            if (lTipos.size() == recetasList.size()) {
                                                mAdapterRecetarioList = new AdapterBuscadorList(lItems, lTipos);
                                                recyclerView.setAdapter(mAdapterRecetarioList);
                                            }

                                        }
                                    });

                                }
                            });
                        }
                    }


                    Log.d("score", "Retrieved " + recetasList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ///***************Barra***************************************************
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.barraPincipal));

        ImageView imgFrida = (ImageView) findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.VISIBLE);

        TextView txtFrida = (TextView) findViewById(R.id.textViewMensajeBienvenida);
        txtFrida.setVisibility(View.VISIBLE);


        ImageView imgFondoBarra = (ImageView) findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.VISIBLE);
        imgFondoBarra.setImageResource(R.drawable.fonsobar);


        ///***************Barra***************************************************

        ///***************Menu***************************************************
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(Color.BLACK);
        ///***************Menu***************************************************

        ///***************Fragment***************************************************

        getFragmentManager().beginTransaction()
                .replace(R.id.actividad, new MenuFragment()).commit();
        ///***************Fragment***************************************************


        handleIntent(getIntent());



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
                    .replace(R.id.actividad, fragmentSearch).commit();
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

        } else if (id == R.id.nav_consejos) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.actividad, new ConsejosFragment()).commit();

        } else if (id == R.id.nav_cuenta) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StarterApplication.pdkClient.onOauthResponse(requestCode, resultCode,
                data);
    }

}
