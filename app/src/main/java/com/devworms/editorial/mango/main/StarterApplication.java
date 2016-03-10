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
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.util.Specs;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.pinterest.android.pdk.PDKClient;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.adapter.IdentityAdapter;
import com.theartofdev.fastimageloader.adapter.ImgIXAdapter;

import mx.openpay.android.Openpay;


public class StarterApplication extends Application {

  public static final int INSTAGRAM_IMAGE_SIZE = 640;

  public static final int INSTAGRAM_AVATAR_SIZE = 150;

  public static boolean mPrefetchImages;

    private static Openpay openpay;
    public static final String MERCHANT_ID = "mom7qomx3rv93zcwv2vk";
    public static final String API_KEY = "pk_f492b71637e247e4b5a314a1f9366ec9";
    public static  final String PRIVATE_KEY = "sk_7506b8183bc34e038ee9e849e2e52994";
    public static  final String URL = "https://sandbox-api.openpay.mx/v1/";
    public static final boolean PRODUCTION_MODE = false;



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

      // this.listaRecetasPorMenu = new HashMap<>();

      PDKClient.configureInstance(this, "4815040272566075428");
      PDKClient.getInstance().onConnect(this);



    mPrefetchImages = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefetch", true);

    FastImageLoader
            .init(this)
            .setDefaultImageServiceAdapter(new ImgIXAdapter())
            .setWriteLogsToLogcat(true)
            .setLogLevel(Log.DEBUG)
            .setDebugIndicator(false);

    FastImageLoader.buildSpec(Specs.IMG_IX_UNBOUNDED)
            .setUnboundDimension()
            .setPixelConfig(Bitmap.Config.RGB_565)
            .build();

    FastImageLoader.buildSpec(Specs.IMG_IX_IMAGE)
            .setDimensionByDisplay()
            .setHeightByResource(R.dimen.image_height)
            .setPixelConfig(Bitmap.Config.RGB_565)
            .build();

    IdentityAdapter identityUriEnhancer = new IdentityAdapter();
    FastImageLoader.buildSpec(Specs.INSTA_AVATAR)
            .setDimension(INSTAGRAM_AVATAR_SIZE)
            .setImageServiceAdapter(identityUriEnhancer)
            .build();

    FastImageLoader.buildSpec(Specs.INSTA_IMAGE)
            .setDimension(INSTAGRAM_IMAGE_SIZE)
            .setPixelConfig(Bitmap.Config.RGB_565)
            .setImageServiceAdapter(identityUriEnhancer)
            .build();

    FastImageLoader.buildSpec(Specs.UNBOUNDED_MAX)
            .setUnboundDimension()
            .setMaxDensity()
            .build();

  }

    public StarterApplication() {

        openpay = new Openpay(MERCHANT_ID , API_KEY , PRODUCTION_MODE);
    }

    public static Openpay getOpenpay() {
        return openpay;
    }

}
