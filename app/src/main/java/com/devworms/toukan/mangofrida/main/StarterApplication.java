package com.devworms.toukan.mangofrida.main;

import android.app.Application;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.dialogs.CompartirDialog;
import com.devworms.toukan.mangofrida.util.Specs;
import com.facebook.CallbackManager;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.pinterest.android.pdk.PDKClient;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.adapter.IdentityAdapter;
import com.theartofdev.fastimageloader.adapter.ImgIXAdapter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import mx.openpay.android.Openpay;


public class StarterApplication extends Application {

    public static final int INSTAGRAM_IMAGE_SIZE = 640;

    public static final int INSTAGRAM_AVATAR_SIZE = 150;

    public static boolean mPrefetchImages;

    private static Openpay openpay;
    public static final String MERCHANT_ID = "mjwkrerefdmam1vmv6wm";
    public static final String PLAN_ID = "pnhk33plnuoqwuo9vs1e";
    public static final String API_KEY = "pk_3668425ba9d34002b1787620ac3709c6";
    public static final String PRIVATE_KEY = "sk_2ed8cc54b32b46b1800ac9f30e497247";
    public static final String URL = "https://api.openpay.mx/v1/";
    public static final Double PRECIO_MEMBRESIA = 30.0;

    public static final boolean PRODUCTION_MODE = true;

    public static boolean bViral = false;
    public static boolean bCompartido = false;
    public static boolean isDesdeMenuPrincipal = false;
    public static boolean bCompartidoTwitter = false;
    public static ParseObject objReceta = null;

    public static PDKClient pdkClient = null;
    public static CallbackManager callbackManager = null;
    public static CompartirDialog dialogoCompartir = null;


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parse_app_id))
                .server(getResources().getString(R.string.parse_server_url))
                .build());

        ParseFacebookUtils.initialize(this.getApplicationContext());

        /* Twitter */
        TwitterAuthConfig authConfig = new TwitterAuthConfig("af09lpCbgHZv0mDHXjJGT1uq4", "Rmj3opgLofx36g41cI3JakAxGHMSwWIruKwN508RwvrMtQXQdr");
        Fabric.with(this, new Twitter(authConfig));

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

        openpay = new Openpay(MERCHANT_ID, API_KEY, PRODUCTION_MODE);
    }

    public static Openpay getOpenpay() {
        return openpay;
    }

}
