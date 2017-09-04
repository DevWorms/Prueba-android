package com.devworms.toukan.mangofrida.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.componentes.AdapterMenuList;
import com.devworms.toukan.mangofrida.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuFragment extends Fragment {

    List<ParseObject> lMenus;
    private AdapterMenuList mAdapterMenuList;
    IInAppBillingService mService;
    static String ITEM_SKU = "com.devworms.toukan.mangofrida.suscripcion";
    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        StarterApplication.mPrefetchImages = !StarterApplication.mPrefetchImages;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("prefetch", StarterApplication.mPrefetchImages).apply();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recetaViewRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.barraPincipal));

        ImageView imgFrida = (ImageView) getActivity().findViewById(R.id.img_frida);
        imgFrida.setVisibility(View.VISIBLE);

        ImageView imgTexto = (ImageView) getActivity().findViewById(R.id.img_texto);
        imgTexto.setVisibility(View.VISIBLE);

        ctx = view.getContext();

        /*
        ImageView imgFondoBarra = (ImageView) getActivity().findViewById(R.id.img_fondo_barra);
        imgFondoBarra.setVisibility(View.VISIBLE);
        imgFondoBarra.setImageResource(R.drawable.fonsobar);
        */

        obtenerObjetosParse(recyclerView);

        return view;
    }

    // El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
        super.onDetach();
    }

    public void obtenerObjetosParse(final RecyclerView recyclerView) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
        query.whereEqualTo("Activo", true);
        query.orderByAscending("Orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> menuList, ParseException e) {
                if (e == null) {
                    lMenus = menuList;
                    mAdapterMenuList = new AdapterMenuList(menuList);
                    mAdapterMenuList.setSuscribe(checkSuscription(mService));
                    recyclerView.setAdapter(mAdapterMenuList);
                }
            }
        });
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
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public boolean checkSuscription(IInAppBillingService service) {
        Boolean isSuscribed = false;

        try {
            Bundle ownedItems = service.getPurchases(3, getActivity().getPackageName(), "subs", null);

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
