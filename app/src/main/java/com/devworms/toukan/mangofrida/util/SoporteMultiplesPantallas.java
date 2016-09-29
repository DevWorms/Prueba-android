package com.devworms.toukan.mangofrida.util;


/**
 * Created by sergio on 04/05/16.
 */
public class SoporteMultiplesPantallas {

    public static int getImageMenuDimens(int width, int height){

        Double d = 0.0;
        switch (width){

            // dispositivos moviles
            case 1440:case 1080:case 480:case 1200:
                d = height*0.4;
                break;
            case 768:case 320: case 800:
                d = height*.44;
                break;
            case 720:
                d = height*.38;
                break;
            // tabletas
            case 600:  // 7 pulgadas
                d = height*.41;
                break;
            case 1536:  // 8 pulgadas
                d = height*.54;
                break;
            default:
                d = height*0.4;
                break;
        }

        return d.intValue();
    }

}
