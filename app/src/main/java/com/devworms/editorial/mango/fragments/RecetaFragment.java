package com.devworms.editorial.mango.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devworms.editorial.mango.R;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.InputStream;
import java.util.List;


/**
 * Created by sergio on 21/10/15.
 */
public class RecetaFragment extends Fragment implements View.OnClickListener{

    private ParseObject objReceta;
    private Bitmap imgReceta;


    public ParseObject getObjReceta() {
        return objReceta;
    }

    public void setObjReceta(ParseObject objReceta) {
        this.objReceta = objReceta;
    }

    public Bitmap getImgReceta() {
        return imgReceta;
    }

    public void setImgReceta(Bitmap imgReceta) {
        this.imgReceta = imgReceta;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_receta, container, false);


        ImageView imagen = (ImageView) view.findViewById(R.id.imagenreceta);
        imagen.setImageBitmap(this.getImgReceta());



        TextView pasosTitulo=(TextView)view.findViewById(R.id.txtrecetaTitulo);
        TextView pasos=(TextView)view.findViewById(R.id.txtreceta);
        pasosTitulo.setText(objReceta.getString("Nombre"));
        pasos.setText("Ingredientes \n" + (objReceta.getString("Ingredientes")));
        pasos.setText(pasos.getText() + "\n\nProcedimiento\n" + (objReceta.getString("Procedimiento")));

        FloatingActionButton buttonCompartir = (FloatingActionButton) view.findViewById(R.id.compartir);
        FloatingActionButton buttonAnadirFavoritos = (FloatingActionButton) view.findViewById(R.id.favoritos);


        buttonCompartir.setOnClickListener(this);
        buttonAnadirFavoritos.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.compartir:
                compartir();
                break;
            case R.id.favoritos:
                anadirFavoritos();
                break;
        }
    }

    public void compartir()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.actividad,(new CompartirFragment()))
                .addToBackStack("RecetaFragment")
                .commit();
    }

    public void anadirFavoritos()
    {
        /*
         let query = PFQuery(className: "Favoritos")
        query.cachePolicy = .CacheElseNetwork
        query.whereKey("username", equalTo: PFUser.currentUser()!)
        query.whereKey("Receta", equalTo: self.objReceta)
        query.findObjectsInBackgroundWithBlock {
            (recetas: [PFObject]?, error: NSError?) -> Void in
            // comments now contains the comments for myPost

            if error == nil {

                //Revisa si ese cliente tiene esa receta para mandar un mensaje de error al tratar de añadirla de nuevo
                if recetas != nil && recetas?.count>0 {

                    // The object has been saved.
                    let alertController = UIAlertController(title: "¡Esta receta ya fue añadida!",
                        message: "Tu receta ya esta en la seccion de favoritos",
                        preferredStyle: UIAlertControllerStyle.Alert)

                    alertController.addAction(UIAlertAction(title: "OK",
                        style: UIAlertActionStyle.Default,
                        handler: nil))
                    // Display alert
                    self.presentViewController(alertController, animated: true, completion: nil)
                }
                    //Añade la receta a favoritos
                else{

                    let date = NSDate()
                    let calendar = NSCalendar.currentCalendar()
                    let components = calendar.components([.Day , .Month , .Year], fromDate: date)

                    let year =  components.year
                    let month = components.month
                    let trimestre = Int(  (Double(month)/3) + 0.7)


                    let favorito = PFObject(className:"Favoritos")
                    favorito["username"] = PFUser.currentUser()
                    favorito["Anio"] = year
                    favorito["Mes"] = month
                    favorito["Trimestre"] = trimestre
                    favorito.relationForKey("authors")
                    favorito.addObject(self.objReceta, forKey: "Receta")

                    favorito.saveInBackgroundWithBlock {
                        (success: Bool, error: NSError?) -> Void in
                        if (success) {
                            // The object has been saved.
                            let alertController = UIAlertController(title: "Añadido a favoritos",
                                message: "¡Tu receta ya esta disponible en la seccion de favoritos!",
                                preferredStyle: UIAlertControllerStyle.Alert)

                            alertController.addAction(UIAlertAction(title: "OK",
                                style: UIAlertActionStyle.Default,
                                handler: nil))
                            // Display alert
                            self.presentViewController(alertController, animated: true, completion: nil)
                        } else {
                            // There was a problem, check error.description
                        }
                    }
                }
            }
            else
            {
                print(error)
            }
        }
         */

    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDetach();
    }

}
