package com.devworms.editorial.mango.openpay;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.devworms.editorial.mango.componentes.AdapterRecetarioList;
import com.devworms.editorial.mango.dialogs.WalletActivity;
import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
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



    // Pagos

    public static String[] pagarEnTienda(Double precio, String fechaVigencia , ParseObject objCliente){



        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"method\" : \"store\",\n   \"amount\" : " + precio + ",\n   \"description\" : \"Cargo con tienda\"} "); //2016-03-20T13:45:00 , "due_date" : "" + fechaVigencia + ""

        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+objCliente.getString("clientID")+"/charges")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "e91bc385-a147-ec4c-7d06-9d96dcd96698")
                .build();



        try {

            JSONObject response = new RequestOpenPay().execute(request).get();
            JSONObject jsonObject = new JSONObject(response.getString("payment_method"));
            objCliente.put("codigobarras",jsonObject.getString("barcode_url"));
            objCliente.put("referenciaentienda", jsonObject.getString("reference"));
            objCliente.put("transaction_id_tienda", response.getString("id"));
            objCliente.saveInBackground();
            return new String[]{jsonObject.getString("barcode_url"), jsonObject.getString("reference")};

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
                    if (!operacion.equals("")){
                        client.put("Suscrito", true);
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

    public static void cancelarSuscripcion(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        final ParseObject cliente = listaClientes.get(0);
                        final String clientId = cliente.getString("clientID");
                        Request request = new Request.Builder()
                                .url(StarterApplication.URL + StarterApplication.MERCHANT_ID + "/customers/"+clientId+"/subscriptions/"+StarterApplication.PLAN_ID)
                                .delete(null)
                                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                                .addHeader("content-type", "application/json")
                                .addHeader("cache-control", "no-cache")
                                .addHeader("postman-token", "7774140f-d94f-ff76-447c-9b904ab74f6e")
                                .build();


                        JSONObject response = null;

                        try {

                            response = new RequestOpenPay().execute(request).get();
                            String error = response.getString("error_code");
                            if (error == null || error.equals("")){
                                cliente.put("Suscrito",false);
                                cliente.put("idsuscripcion","");
                                cliente.put("caducidad","");
                                cliente.saveInBackground();
                            }

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

    public static String eliminarTarjeta(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Clientes");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> listaClientes, ParseException e) {
                if (e == null) {


                    if (listaClientes.size() > 0) {
                        final ParseObject cliente = listaClientes.get(0);
                        final String clientId = cliente.getString("clientID");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tarjetas");
                        query.whereEqualTo("cliente", ParseUser.getCurrentUser());
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

                                            String error = response.getString("error_code");
                                            if (error == null || error.equals("")){

                                                tarjeta.deleteInBackground();
                                            }

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
