package com.devworms.editorial.mango.util;

/**
 * Created by sergio on 04/05/16.
 */
public class SoporteMultiplesPantallas {

    public static int getImageMenuDimens(int width, int height){

        Double d = 0.0;
        switch (width){
            case 1440:case 1080:case 480:
                d = height*0.4;
                break;
            case 768:case 320:
                d = height*.44;
                break;
            case 720:
                d = height*.38;
                break;
            default:
                d = height*0.4;
                break;
        }

        return d.intValue();
    }

}
