package com.devworms.editorial.mango.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.fragments.ConsejosFragment;
import com.devworms.editorial.mango.fragments.CreditosFragment;
import com.devworms.editorial.mango.fragments.CuentaFragment;
import com.devworms.editorial.mango.fragments.FavoritosFragment;
import com.devworms.editorial.mango.fragments.MenuFragment;
import com.parse.ParseAnalytics;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ///***************Barra***************************************************
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ///***************Barra***************************************************

        ///***************Menu***************************************************
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ///***************Menu***************************************************

        ///***************Fragment***************************************************
        getFragmentManager().beginTransaction()
                .replace(R.id.actividad, new MenuFragment()).commit();
        ///***************Fragment***************************************************


    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
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

}
