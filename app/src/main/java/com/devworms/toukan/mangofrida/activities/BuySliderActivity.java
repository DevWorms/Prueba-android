package com.devworms.toukan.mangofrida.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.devworms.toukan.mangofrida.R;
import com.devworms.toukan.mangofrida.componentes.IabHelper;
import com.devworms.toukan.mangofrida.componentes.IabResult;

import java.util.Timer;
import java.util.TimerTask;

public class BuySliderActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static Integer[] images = {
            R.drawable.sus_slider_photo_1_1,
            R.drawable.sus_slider_photo_2_2,
            R.drawable.sus_slider_photo_3_3,
            R.drawable.sus_slider_photo_4_4
    };

    private static Integer[] bolitas = {
            R.drawable.sus_slider_position_1,
            R.drawable.sus_slider_position_2,
            R.drawable.sus_slider_position_3,
            R.drawable.sus_slider_position_4
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    ImageView cancelar;
    ImageView buy;
    IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwMPI5U2E7s8mNJiCTK53UiZ1WE/bSqvfASGu8SbpPrInis56J2pn6uaxIJIPBfleiSCN4fd9O2uK8/Vt6cpztfvvUWHbDZ6MLtMh3hBSFDZjYxpIYsanA2R02kklnD6NDs1ONb3XDXgl0NbYPKFgoIPgoMMa6wH7WLZQjh9oCKl8cOMQxOjVQcJwR7voZHAUU0gSofg463ztFIa2CzW0gbZ80tSq7+vQerDx2rdcs/t28fOt9gRKzK0JTdN/lv5umSBFCsVlIBseiswmdjNCqzYf6hkYIq1KZ5llbUeXctTNWAXKve/3qRfc5LC/oVkuFS69V2I6WrWIBGNDySqp1wIDAQAB";
    String ITEM_SKU = "com.devworms.toukan.mangofrida.suscripcion.nueva";
    BillingProcessor bp;
    String TAG = "InAppBilling";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_slider);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        cancelar = (ImageView) findViewById(R.id.btn_can);
        buy = (ImageView) findViewById(R.id.btn_con);
        bp = new BillingProcessor(this, base64EncodedPublicKey, this);
        activity = this;

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprar();
            }
        });

        final Handler handler = new Handler();
        Timer swipeTimer = null;
        final Runnable Update = new Runnable() {
            public void run() {
                Integer pos = 0;
                Integer count = mViewPager.getAdapter().getCount();
                Integer current = mViewPager.getCurrentItem();

                if (current < count - 1) {
                    pos = mViewPager.getCurrentItem() + 1;
                }

                Log.d("Position", pos.toString());

                mViewPager.setCurrentItem(pos);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 4500);
    }

    public void cancelar() {
        finish();
    }

    public void comprar() {
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    try {
                        //mHelper.launchPurchaseFlow(activity, ITEM_SKU, 10001, mPurchaseFinishedListener);
                        bp.subscribe(activity, ITEM_SKU);
                        finish();
                    } catch (Exception ex) {
                        Log.d("item", ex.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_buy_slider, container, false);

            try {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageSlides);
                ImageView imageViewBolitas = (ImageView) rootView.findViewById(R.id.bolitas);

                imageView.setImageResource(images[getArguments().getInt(ARG_SECTION_NUMBER)]);
                imageViewBolitas.setImageResource(bolitas[getArguments().getInt(ARG_SECTION_NUMBER)]);
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
            }

            return rootView;
        }
    }
}
