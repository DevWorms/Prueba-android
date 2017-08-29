package com.devworms.toukan.mangofrida.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devworms.toukan.mangofrida.R;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenSlider extends AppCompatActivity {
    private static Integer[] images = {
            R.drawable.imagenslider1,
            R.drawable.imagenslider2,
            R.drawable.imagenslider3
    };

    private static Integer[] bolitas = {
            R.drawable.bolitasslider1,
            R.drawable.bolitasslider2,
            R.drawable.bolitasslider3
    };

    private static Integer[] textouno = {
            R.drawable.cocinamexicana,
            R.drawable.daleprobadita,
            R.drawable.veinteanosdeexperiencia
    };

    private static Integer[] textodos = {
            R.drawable.cocinamexicana2,
            R.drawable.cocinatradicional,
            R.drawable.todoeltalento
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slider);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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

    public void loguearse(View view) {
        Intent intent = new Intent(ScreenSlider.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void crearcuenta(View view) {
        Intent intent = new Intent(ScreenSlider.this, RegistroActivity.class);
        startActivity(intent);
        finish();
    }

    public void continuar(View view) {
        Intent intent = new Intent(ScreenSlider.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            View rootView = inflater.inflate(R.layout.fragment_screen_slider, container, false);

            try {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageSlides);
                ImageView imageViewTextUno = (ImageView) rootView.findViewById(R.id.textouno);
                ImageView imageViewBolita = (ImageView) rootView.findViewById(R.id.bolita);
                ImageView imageViewTextDos = (ImageView) rootView.findViewById(R.id.textodos);
                ImageView imageViewBolitas = (ImageView) rootView.findViewById(R.id.bolitas);

                imageView.setImageResource(images[getArguments().getInt(ARG_SECTION_NUMBER)]);
                imageViewTextUno.setImageResource(textouno[getArguments().getInt(ARG_SECTION_NUMBER)]);
                imageViewBolita.setImageResource(R.drawable.bolita);
                imageViewTextDos.setImageResource(textodos[getArguments().getInt(ARG_SECTION_NUMBER)]);
                imageViewBolitas.setImageResource(bolitas[getArguments().getInt(ARG_SECTION_NUMBER)]);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }

            return rootView;
        }
    }
}
