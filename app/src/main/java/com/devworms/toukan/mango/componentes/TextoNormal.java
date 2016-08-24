package com.devworms.toukan.mango.componentes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sergio on 23/08/16.
 */
public class TextoNormal extends TextView {


    public TextoNormal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenirnexregular.otf"));
    }
}
