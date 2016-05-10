package com.devworms.toukan.mango.efectos;

import android.support.v4.view.ViewPager;
import android.view.View;
/**
 * Created by sergio on 11/11/15.
 */
public class RelaxTransformer implements ViewPager.PageTransformer {


    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]


            view.setTranslationX((float) (-(1 - position) * 0.5 * pageWidth));
            view.setTranslationX((float) (-(1 - position) * 0.5 * pageWidth));

            view.setTranslationX((float) (-(1 - position) * pageWidth));
            view.setTranslationX((float) (-(1 - position) * pageWidth));

            view.setTranslationX((float) (-(1 - position) * 1.5 * pageWidth));
            view.setTranslationX((float) (-(1 - position) * 1.7 * pageWidth));
            // The 0.5, 1.5, 1.7 values you see here are what makes the view move in a different speed.
            // The bigger the number, the faster the view will translate.
            // The result float is preceded by a minus because the views travel in the opposite direction of the movement.

            view.setTranslationX((position) * (pageWidth / 4));

            view.setTranslationX((position) * (pageWidth / 1));

            view.setTranslationX((position) * (pageWidth / 2));

            view.setTranslationX((position) * (pageWidth / 1));
            // This is another way to do it


        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
