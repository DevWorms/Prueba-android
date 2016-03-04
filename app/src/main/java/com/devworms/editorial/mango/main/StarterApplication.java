/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.devworms.editorial.mango.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.devworms.editorial.mango.componentes.CustomListParse;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.pinterest.android.pdk.PDKClient;

import java.util.HashMap;


public class StarterApplication extends Application {


    private CustomListParse listaMenuPrincipal;
    private HashMap<ParseObject, CustomListParse>listaRecetasPorMenu;
    private Bitmap imagenReceta;

    public Bitmap getImagenReceta() {
        return imagenReceta;
    }

    public void setImagenReceta(Bitmap imagenReceta) {
        this.imagenReceta = imagenReceta;
    }

    public HashMap<ParseObject, CustomListParse> getlistaRecetasPorMenu() {
        return listaRecetasPorMenu;
    }

    public void setlistaRecetasPorMenu(HashMap<ParseObject, CustomListParse> listaRecetasPorMenu) {
        this.listaRecetasPorMenu = listaRecetasPorMenu;
    }


  public CustomListParse getListaMenuPrincipal() {
    return listaMenuPrincipal;
  }

  public void setListaMenuPrincipal(CustomListParse listaMenuPrincipal) {
    this.listaMenuPrincipal = listaMenuPrincipal;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    Parse.initialize(this, "Rv2InCwEE4RJowtNJVaYqlLw0VpjPLEePcfpHMsw", "oYALR4CrZhDOYlrOk7zCLszZXixJEXsDtOV4e0zt");
    ParseInstallation.getCurrentInstallation().saveInBackground();

    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    ParseFacebookUtils.initialize(this.getApplicationContext());

    ParseTwitterUtils.initialize("af09lpCbgHZv0mDHXjJGT1uq4", "Rmj3opgLofx36g41cI3JakAxGHMSwWIruKwN508RwvrMtQXQdr");

      this.listaRecetasPorMenu = new HashMap<>();

      PDKClient.configureInstance(this, "4815040272566075428");
      PDKClient.getInstance().onConnect(this);



  }
}
