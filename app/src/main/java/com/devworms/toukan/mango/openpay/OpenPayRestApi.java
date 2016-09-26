package com.devworms.toukan.mango.openpay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.componentes.AdapterRecetarioList;
import com.devworms.toukan.mango.dialogs.WalletActivity;
import com.devworms.toukan.mango.main.StarterApplication;
import com.devworms.toukan.mango.util.ISO8601;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import mx.openpay.android.model.Card;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by loajrla on 06/03/16.
 */
public class OpenPayRestApi{



    // crear clientes y tarjetas
    public static void crearClienteConTarjeta(final Card card, final String email, final String telefono, final ParseObject cliente, final Activity context) {

        ParseObject objCliente = null;
        if (cliente == null) {
            objCliente = crearCliente(card.getHolderName(), email, telefono, false);
        }
        else{
            objCliente = cliente;
        }

        crearTarjeta(card, objCliente, context);
    }


    public static ParseObject crearCliente(String nombre, String email, String telefono, boolean requires_account){

        email = "example@gmail.com";
        telefono ="5555555555";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"name\": \"" + nombre + "\",\n   \"email\": \"" + email + "\",\n   \"requires_account\": " + requires_account + ",\n   \"phone_number\": \"" + telefono + "\"\n}");
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "8e00153d-f547-d459-d7b8-3b7b6c338047")
                .build();

        JSONObject response  = null;
        ParseObject clientes = new ParseObject("Clientes");
        try {

            response = new RequestOpenPay().execute(request).get();
            clientes.put("username", ParseUser.getCurrentUser());
            clientes.put("clientID", response.getString("id"));
            clientes.put("nombre", response.getString("name"));
            clientes.put("email", response.getString("email"));
            clientes.put("numero", response.getString("phone_number"));
            clientes.put("codigobarras", "");
            clientes.put("referenciaentienda", "");
            clientes.put("Suscrito", false);
            clientes.saveInBackground();


        } catch (InterruptedException e) {
            clientes = null;
            e.printStackTrace();
        } catch (ExecutionException e) {
            clientes = null;
            e.printStackTrace();
        } catch (JSONException e) {
            clientes = null;
            e.printStackTrace();
        }

        return clientes ;

    }



    public static String crearTarjeta(Card card, ParseObject parseClient, Activity context){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"card_number\":\"" + card.getCardNumber() + "\",\n   \"holder_name\":\"" + card.getHolderName() + "\",\n   \"expiration_year\":\"" + card.getExpirationYear() + "\",\n   \"expiration_month\":\"" + card.getExpirationMonth() + "\",\n   \"cvv2\":\"" + card.getCvv2() + "\"\n }");
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+parseClient.getString("clientID")+"/cards")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "51aac3bd-dfad-a02f-ebbb-986b00d47d07")
                .build();

        JSONObject response  = null;
        String tarjetaId = "";

        try {
            response = new RequestOpenPay().execute(request).get();
            tarjetaId = response.getString("id");
            ParseObject clientes = new ParseObject("Tarjetas");

            clientes.put("cliente", parseClient);
            clientes.put("tarjetaPrincipal", tarjetaId);
            clientes.put("brand", response.getString("brand"));
            clientes.put("numero", response.getString("card_number"));
            clientes.put("banco", response.getString("bank_name"));
            clientes.saveInBackground();


            Intent intent = new Intent(context.getApplicationContext(), WalletActivity.class);

            context.startActivity(intent);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tarjetaId;

    }

    public static boolean validarPagoEnTienda(ParseObject objCliente){

        String caducidad = ISO8601.fecha(0,1); // un mes en futuro parametros;  dias en el futuro, meses en el futuro
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+objCliente.getString("clientID")+"/charges/"+objCliente.getString("transaction_id_tienda"))
                .get()
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "fffa7be1-071a-4b41-1de2-b7d0e3988de1")
                .build();

        try {

            JSONObject response = new RequestOpenPay().execute(request).get();
            String estatus = response.getString("status");


            Calendar calendar = ISO8601.toCalendar(caducidad);
            Date date = calendar.getTime();
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(date);


            if(estatus == null){return false;}

            if (estatus.equals("completed")){
                objCliente.put("codigobarras","");
                objCliente.put("referenciaentienda", "");
                objCliente.put("transaction_id_tienda", "");
                objCliente.put("Suscrito", true);
                objCliente.put("Caducidad", fecha);
                objCliente.saveInBackground();

                return true;
            }else if(estatus.equals("cancelled") || estatus.equals("unpaid")){
                objCliente.put("Suscrito", false);
                objCliente.saveInBackground();
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }



    // Pagos

    public static String[] pagarEnTienda(Double precio , ParseObject objCliente, Activity actividad){


        String caducidadCanjeo = ISO8601.fecha(7,0); // una semana, 7 dias en el futuro y 0 mese en el futuro


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"method\" : \"store\",\n   \"amount\" : " + precio + ",\n   \"description\" : \"Cargo con tienda\",\n" +
                "   \"due_date\" : \""+caducidadCanjeo+"\"} "); //2016-03-20T13:45:00 , "due_date" : "" + fechaVigencia + ""

        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+objCliente.getString("clientID")+"/charges")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "e91bc385-a147-ec4c-7d06-9d96dcd96698")
                .build();



        try {


            Calendar calendar = ISO8601.toCalendar(caducidadCanjeo);

            Date date = calendar.getTime();
            String fecha = new SimpleDateFormat("yyyy-MM-dd")
                    .format(date);

            JSONObject response = new RequestOpenPay().execute(request).get();
            JSONObject jsonObject = new JSONObject(response.getString("payment_method"));
            objCliente.put("codigobarras",jsonObject.getString("barcode_url"));
            objCliente.put("referenciaentienda", jsonObject.getString("reference"));
            objCliente.put("transaction_id_tienda", response.getString("id"));
            objCliente.put("Caducidad", fecha);
            objCliente.saveInBackground();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

            // set title
            alertDialogBuilder.setTitle("Canjea este codigo antes de");


            String fechas = new SimpleDateFormat("dd-MM-yyyy")
                    .format(date);

            // set dialog message
            alertDialogBuilder
                    .setMessage(fechas)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            return new String[]{jsonObject.getString("barcode_url"), jsonObject.getString("reference")};

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return new String[]{};

    }

    // suscribirse a plan ( necesario tener tarjeta de credito registrada a un cliente para esto)

    public static void suscribirsePlan(final Activity actividad){


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        final ParseObject cliente = listaClientes.get(0);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", cliente);
                        query.findInBackground(new FindCallback<ParseObject>() {

                            public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                if (e == null) {
                                    suscribirsePlanRest(listaTarjetas.get(0).getString("tarjetaPrincipal"), cliente, actividad);
                                }
                            }
                        });
                    }
                    else{
                        //Imprimir un error diciendo que no hay ningun cliente y no se puede
                    }


                    Log.d("score", "Retrieved scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }

            public void suscribirsePlanRest(String tarjetaId, ParseObject client, Activity actividad){


                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n     \"source_id\":\""+tarjetaId+"\"\n , \"plan_id\":\""+StarterApplication.PLAN_ID+"\"\n}");
                Request request = new Request.Builder()
                        .url(StarterApplication.URL+StarterApplication.MERCHANT_ID+"/customers/"+client.getString("clientID")+"/subscriptions")
                        .post(body)
                        .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "feeff8e9-b5d7-880f-9674-9a788a8fe85a")
                        .build();

                JSONObject response  = null;

                try {
                    response = new RequestOpenPay().execute(request).get();
                    String operacion = response.getString("id");
                    String caducidad = response.getString("period_end_date");
                    if (!operacion.equals("")){
                        client.put("Suscrito", true);
                        client.put("idsuscripcion",operacion);
                        client.put("Caducidad",caducidad);
                        client.saveInBackground();

                        Toast.makeText(actividad, "Te has suscrito", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    // conexion a internet
    private static class RequestOpenPay extends AsyncTask<Request, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(Request... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(params[0]).execute();

                String string = response.body().string();
                JSONObject jsonObject = new JSONObject(string);

                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public static void cancelarSuscripcion(final Activity actividad, final TextView txtSubscripcion, final View view){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        final ParseObject cliente = listaClientes.get(0);
                        final String clientId = cliente.getString("clientID");
                        Request request = new Request.Builder()
                                .url(StarterApplication.URL + StarterApplication.MERCHANT_ID + "/customers/"+clientId+"/subscriptions/"+cliente.getString("idsuscripcion"))
                                .delete(null)
                                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                                .addHeader("content-type", "application/json")
                                .addHeader("cache-control", "no-cache")
                                .addHeader("postman-token", "7774140f-d94f-ff76-447c-9b904ab74f6e")
                                .build();


                        JSONObject response = null;

                        try {

                            response = new RequestOpenPay().execute(request).get();
                            String error = response == null ? "":response.getString("error_code");

                            String titulo = "";
                            String mensaje = "";
                            if (error == null || error.equals("")){
                                cliente.put("Suscrito",false);
                                cliente.put("idsuscripcion","");
                                cliente.put("Caducidad","");
                                cliente.saveInBackground();
                                titulo = "Suscripción cancelada";
                                mensaje = "su suscripción actual fue cancelada con éxito";
                                view.setVisibility(View.INVISIBLE);
                                txtSubscripcion.setText("Sin inscripción actual");
                            }
                            else{
                                titulo = "Error en cancelación";
                                mensaje = "No es posible cancelar en este momento, intente más tarde";


                            }

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                            // set title
                            alertDialogBuilder.setTitle(titulo);

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(mensaje)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });

                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (ExecutionException ex) {
                            ex.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static String eliminarTarjeta(final Activity actividad, final View view, final LinearLayout linearLayoutCompat){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        final ParseObject cliente = listaClientes.get(0);
                        final String clientId = cliente.getString("clientID");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", cliente);
                        query.findInBackground(new FindCallback<ParseObject>() {

                            public void done(List<ParseObject> listaTarjetas, ParseException e) {
                                if (e == null) {


                                    if (listaTarjetas.size() > 0) {
                                        final ParseObject tarjeta = listaTarjetas.get(0);


                                        String tarjetaId = tarjeta.getString("tarjetaPrincipal");

                                        Request request = new Request.Builder()
                                                .url(StarterApplication.URL + StarterApplication.MERCHANT_ID + "/customers/" + clientId + "/cards/" + tarjetaId)
                                                .delete(null)
                                                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                                                .addHeader("content-type", "application/json")
                                                .addHeader("cache-control", "no-cache")
                                                .addHeader("postman-token", "b7c48f0e-7e92-7617-2ab4-b1d5b3451692")
                                                .build();

                                        JSONObject response = null;

                                        try {

                                            response = new RequestOpenPay().execute(request).get();
                                            String titulo = "";
                                            String mensaje = "";
                                            String error = response == null ? "" : response.getString("error_code");
                                            if (error == null || error.equals("")){

                                                tarjeta.deleteInBackground();

                                                titulo = "Tarjeta eliminada";
                                                mensaje = "Esta tarjeta fue eliminada de esta cuenta";
                                                view.setVisibility(View.INVISIBLE);
                                                linearLayoutCompat.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                titulo = "Error en eliminación";
                                                mensaje = "No es posible dar de baja esta tarjeta, si esta suscrito primero cancele la membresia, de lo contrario intente más tarde";


                                                if(error.equals("1005")){
                                                    tarjeta.deleteInBackground();

                                                    titulo = "Tarjeta eliminada";
                                                    mensaje = "Esta tarjeta fue eliminada de esta cuenta";
                                                    view.setVisibility(View.INVISIBLE);
                                                    linearLayoutCompat.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(actividad, R.style.myDialog));

                                            // set title
                                            alertDialogBuilder.setTitle(titulo);

                                            // set dialog message
                                            alertDialogBuilder
                                                    .setMessage(mensaje)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    });

                                            // show it
                                            alertDialogBuilder.show();
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        } catch (ExecutionException ex) {
                                            ex.printStackTrace();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        return "";
    }




}
