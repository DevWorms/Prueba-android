package com.devworms.toukan.mangofrida.componentes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.dialogs.AgregarTarjeta;
import com.devworms.toukan.mangofrida.dialogs.WalletActivity;
import com.devworms.toukan.mangofrida.fragments.RecetaFragment;
import com.devworms.toukan.mangofrida.fragments.RecetarioFragment;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.devworms.toukan.mangofrida.util.Specs;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.devworms.toukan.mangofrida.openpay.OpenPayRestApi.conultarStatusSuscripcion;


public final class AdapterRecetarioList extends RecyclerView.Adapter<AdapterRecetarioList.ViewHolder> implements BillingProcessor.IBillingHandler  {
    private List<ParseObject> mItems;
    private String tipoMenu;
    private Activity actividad;
    private List<String> lTipos;
    static BillingProcessor bp;
    static String TAG = "InAppBilling";
    static IabHelper mHelper;
    static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwMPI5U2E7s8mNJiCTK53UiZ1WE/bSqvfASGu8SbpPrInis56J2pn6uaxIJIPBfleiSCN4fd9O2uK8/Vt6cpztfvvUWHbDZ6MLtMh3hBSFDZjYxpIYsanA2R02kklnD6NDs1ONb3XDXgl0NbYPKFgoIPgoMMa6wH7WLZQjh9oCKl8cOMQxOjVQcJwR7voZHAUU0gSofg463ztFIa2CzW0gbZ80tSq7+vQerDx2rdcs/t28fOt9gRKzK0JTdN/lv5umSBFCsVlIBseiswmdjNCqzYf6hkYIq1KZ5llbUeXctTNWAXKve/3qRfc5LC/oVkuFS69V2I6WrWIBGNDySqp1wIDAQAB";
    static String ITEM_SKU = "com.devworms.toukan.mangofrida.suscripcion";
    Context ctx;

    public AdapterRecetarioList(List<ParseObject> mItems, String tipoMenu, Activity actividad) {
        this.mItems = mItems;
        this.tipoMenu = tipoMenu;
        this.actividad = actividad;


        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {
                FastImageLoader.prefetchImage(parseObject.getString("Url_Imagen"), Specs.IMG_IX_UNBOUNDED);
            }
        }
    }

    public AdapterRecetarioList(List<ParseObject> mItems, List<String> lTipos, Activity actividad) {
        this.mItems = mItems;
        this.lTipos = lTipos;
        this.actividad = actividad;

        if (StarterApplication.mPrefetchImages) {
            for (ParseObject parseObject : mItems) {
                FastImageLoader.prefetchImage(parseObject.getString("Url_Imagen"), Specs.IMG_IX_IMAGE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecetarioList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recetario, parent, false);
        bp = new BillingProcessor(v.getContext(), base64EncodedPublicKey, this);
        ctx = v.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_UNBOUNDED);
        holder.objReceta = mItems.get(position);

        holder.tipoMenu = this.tipoMenu == null ? lTipos.get(position) : this.tipoMenu;
        holder.actividad = actividad;
        holder.mTargetImageView.loadImage(mItems.get(position).getString("Url_Imagen"), spec.getKey());
        holder.setTitulos(mItems.get(position));
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        notification("comprado");
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        notification("error");
    }

    @Override
    public void onBillingInitialized() {
        //notification(bp.getSubscriptionListingDetails(ITEM_SKU).toString());
        /*
        if (bp.getPurchaseListingDetails(ITEM_SKU).isSubscription) {
            Log.d("Suscripción", "Activa");
        } else {
            Log.d("Suscripción", "NO activa");
        }
        */
    }



    public void notification(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TargetImageView mTargetImageView;
        ParseObject objReceta;
        public String tipoMenu;
        public Activity actividad;
        public TextView tTextViewNombrereceta;
        public TextView textViewPorciones;
        public TextView tTextViewTiempo;
        public ImageView iImageViewDificultad;
        private Dialog dialog;

        public ViewHolder(View v) {
            super(v);

            mTargetImageView = (TargetImageView) v.findViewById(R.id.image_view);

            mTargetImageView.setOnClickListener(this);

            tTextViewNombrereceta = (TextView) v.findViewById(R.id.textViewNombrereceta);
            tTextViewTiempo = (TextView) v.findViewById(R.id.textViewTiempo);
            textViewPorciones = (TextView) v.findViewById(R.id.textViewPorciones);


            iImageViewDificultad = (ImageView) v.findViewById(R.id.imageView12);
        }

        public void setTitulos(ParseObject objReceta) {
            tTextViewNombrereceta.setText(objReceta.getString("Nombre"));
            tTextViewTiempo.setText("  " + objReceta.getString("Tiempo"));
            textViewPorciones.setText("  " + objReceta.getString("Porciones"));

            String dificultad = objReceta.getString("Nivel");

            int imageresource = 0;
            switch (dificultad) {

                case "Principiante":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor1", "drawable", actividad.getPackageName());

                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                case "Intermedio":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor2", "drawable", actividad.getPackageName());
                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                case "Avanzado":
                    imageresource = actividad.getResources().getIdentifier("@drawable/flor3", "drawable", actividad.getPackageName());
                    iImageViewDificultad.setImageResource(imageresource);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mTargetImageView.getContext();
            if (activity != null) {

                final ImageView imageView = (ImageView) v;

                if (tipoMenu.equals("pago")) {
                    if (ParseUser.getCurrentUser() != null) {
                        consultarSuscripcion(activity, imageView);
                    } else {
                        String titulo = "Inicio de sesión necesario";
                        String mensaje = "Tienes que iniciar sesión para poder suscribirte y acceder a esta receta";

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                        // set title
                        alertDialogBuilder.setTitle(titulo);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        //MainActivity.this.finish();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                } else {
                    if (objReceta != null) {
                        RecetarioFragment recetario = new RecetarioFragment();
                        recetario.setMenuSeleccionado(objReceta);


                        View drawingView = imageView;
                        drawingView.buildDrawingCache(true);

                        RecetaFragment receta = new RecetaFragment();
                        receta.setObjReceta(objReceta);

                        activity.getFragmentManager().beginTransaction()
                                .replace(R.id.actividad, receta)
                                .addToBackStack("MenuFragment")
                                .commit();
                    }
                }
            }
        }

        public void consultarSuscripcion(final Activity activity, final ImageView imageView) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
            query.whereEqualTo("username", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> clientes, ParseException e) {
                    if (e == null) {
                        if (clientes.size() > 0) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Calendar cal = sdf.getCalendar();

                            boolean suscripcion = conultarStatusSuscripcion(clientes.get(0));

                            if (clientes.get(0).getBoolean("Suscrito") && suscripcion) {
                                RecetarioFragment recetario = new RecetarioFragment();
                                recetario.setMenuSeleccionado(objReceta);

                                RecetaFragment receta = new RecetaFragment();
                                receta.setObjReceta(objReceta);

                                activity.getFragmentManager().beginTransaction()
                                        .replace(R.id.actividad, receta)
                                        .addToBackStack("MenuFragment")
                                        .commit();
                            } else {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                                query.whereEqualTo("cliente", clientes.get(0));
                                query.findInBackground(new FindCallback<ParseObject>() {

                                    public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                        if (e == null) {
                                            if (listaTarjetas.size() > 0) {
                                                Intent intent = new Intent(activity.getApplicationContext(), WalletActivity.class);
                                                activity.startActivity(intent);
                                            } else {
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                                Boolean slider = preferences.getBoolean("Mostrardiasprueba", true);
                                                if (slider) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putBoolean("Mostrardiasprueba", true);
                                                    editor.apply();

                                                    mostrarAnuncioDiasPrueba();
                                                } else {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putBoolean("Mostrardiasprueba", true);
                                                    editor.apply();

                                                    addtarjetacred();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                            Boolean slider = preferences.getBoolean("Mostrardiasprueba", true);
                            if (slider) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("Mostrardiasprueba", true);
                                editor.apply();

                                mostrarAnuncioDiasPrueba();

                                if (!dialog.isShowing()) {
                                    dialog.show();
                                }
                            } else {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("Mostrardiasprueba", true);
                                editor.apply();

                                addtarjetacred();
                            }
                        }
                    }
                }

                public void mostrarAnuncioDiasPrueba() {
                    if (dialog == null) {
                        dialog = new Dialog(activity);
                        dialog.setCancelable(true);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //Aqui haces que tu layout se muestre como dialog

                        dialog.setContentView(R.layout.dialog_producto);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                        ((ImageView) dialog.findViewById(R.id.btn_can)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                                dialog.closeOptionsMenu();
                            }
                        });

                        ((ImageView) dialog.findViewById(R.id.btn_con)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                dialog.cancel();
                                mHelper = new IabHelper(dialog.getContext(), base64EncodedPublicKey);

                                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                    public void onIabSetupFinished(IabResult result) {
                                        if (!result.isSuccess()) {
                                            Log.d(TAG, "In-app Billing setup failed: " +
                                                    result);
                                        } else {
                                            try {
                                                //mHelper.launchPurchaseFlow(activity, ITEM_SKU, 10001, mPurchaseFinishedListener);
                                                bp.subscribe(activity, ITEM_SKU);
                                            } catch (Exception ex) {
                                                Log.d("item", ex.getMessage());
                                            }
                                        }
                                    }
                                });
                            }
                        });

                    }

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }

                public void addtarjetacred() {
                    Intent intent = new Intent(activity.getApplicationContext(), AgregarTarjeta.class);
                    activity.startActivity(intent);
                }
            });
        }

        private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            }
        };

        private IabHelper.OnIabPurchaseFinishedListener mPurchasedFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            }
        };
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase) {
                if (result.isFailure()) {
                    Log.d("Error", result.getMessage());
                    return;
                } else if (purchase.getSku().equals(ITEM_SKU)) {
                    consumeItem();
                    // buyButton.setEnabled(false);
                }

            }
        };

        public void consumeItem() {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        }

        IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (!result.isFailure()) {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                            mConsumeFinishedListener);
                }
            }
        };

        IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase,
                                          IabResult result) {

            }
        };

    }


}