package com.devworms.editorial.mango.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.efectos.DepthPageTransformer;
import com.devworms.editorial.mango.efectos.RelaxTransformer;
import com.devworms.editorial.mango.efectos.ZoomOutPageTransformer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sergio on 08/11/15.
 */
public class ScreenSlider extends AppCompatActivity {
    private static Integer[]images =
            {
                    R.drawable.imagen1,
                    R.drawable.imagen2,
                    R.drawable.imagen3,
                    R.drawable.imagen4,
            };

    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slider);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //mViewPager.setPageTransformer(true, new RelaxTransformer());
       // mViewPager.setPageTransformer(true,  new DepthPageTransformer ());


        final Handler handler = new Handler();
        Timer swipeTimer =null;
        final Runnable Update = new Runnable() {
            public void run() {
                Integer pos=0;
                Integer count=mViewPager.getAdapter().getCount();
                Integer current=mViewPager.getCurrentItem();



                if (current < count-1) {
                    pos=mViewPager.getCurrentItem() + 1;
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
        }, 2000, 2500);


    }

    public void loguearse(View view)
    {
        Intent intent = new Intent(ScreenSlider.this,Login.class);
        startActivity(intent);
        finish();
    }

    public void continuar(View view)
    {
        Intent intent = new Intent(ScreenSlider.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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

    /**
     * A placeholder fragment containing a simple view.
     */

    public static class PlaceholderFragment extends Fragment {



        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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
                imageView.setImageResource(images[getArguments().getInt(ARG_SECTION_NUMBER)]);
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
            return rootView;
        }
    }
}
