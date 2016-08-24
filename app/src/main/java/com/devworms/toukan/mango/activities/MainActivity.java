package com.devworms.toukan.mango.activities;

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

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.dialogs.CompartirDialog;
import com.devworms.toukan.mango.fragments.CreditosFragment;
import com.devworms.toukan.mango.fragments.CuentaFragment;
import com.devworms.toukan.mango.fragments.FavoritosFragment;
import com.devworms.toukan.mango.fragments.MenuFragment;
import com.devworms.toukan.mango.fragments.RecetaFragment;
import com.devworms.toukan.mango.fragments.SearchResultsFragment;
import com.devworms.toukan.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;

import java.io.Serializable;
import java.util.ArrayList;
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

      /*  ImageView imgFrida = (ImageView) findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.VISIBLE);
*/


        /*ImageView imgFondoBarra = (ImageView) findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.VISIBLE);
        imgFondoBarra.setImageResource(R.drawable.fonsobar);
*/

        ///***************Barra***************************************************

        ///***************Menu***************************************************
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        abrirReceta();

        handleIntent(getIntent());

    }

    public void abrirReceta(){
        if(StarterApplication.bCompartido && StarterApplication.objReceta != null)
        {

            final RecetaFragment receta = new RecetaFragment();

            final ParseObject objParse = StarterApplication.objReceta;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Recetas");
            query.whereEqualTo("Menu", objParse);
            query.whereEqualTo("Activada", true);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> menuList, ParseException e) {
                    if (e == null) {
                        if(menuList.size()>0) {
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

        if(requestCode == TWEET_COMPOSER_REQUEST_CODE && StarterApplication.bCompartidoTwitter)
        {
            StarterApplication.bCompartidoTwitter = false;
            if(resultCode == RESULT_OK){
                final RecetaFragment receta = new RecetaFragment();

                final ParseObject objParse = StarterApplication.objReceta;

                abrirReceta();

            }
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
