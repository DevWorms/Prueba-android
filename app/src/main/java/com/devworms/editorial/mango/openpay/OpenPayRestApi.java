package com.devworms.editorial.mango.openpay;

import android.os.AsyncTask;

import com.devworms.editorial.mango.main.StarterApplication;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import mx.openpay.android.Openpay;
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
    public static String crearClienteConTarjeta(Card card, String email, String telefono) {


        Openpay openpay =  StarterApplication.getOpenpay();

        String customerId = consultarSiClienteExiste();
        // se consulta si el cliente existe o no
        if (customerId.isEmpty()){ //si no existe
            //crear cliente nuevo para que este asociado a esa tarjeta
            customerId = crearCliente(card.getHolderName(), email, telefono, false);
        }

        return crearTarjeta(card, customerId);
    }


    public static String consultarSiClienteExiste(){
        String customerId = "";
        return  customerId;
    }

    public static String crearCliente(String nombre, String email, String telefono,boolean requires_account){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"name\": \"" + nombre + "\",\n   \"email\": \"" + email + "\",\n   \"requires_account\": " + requires_account + ",\n   \"phone_number\": \""+telefono+"\"\n}");
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "8e00153d-f547-d459-d7b8-3b7b6c338047")
                .build();

        JSONObject response  = null;
        String customerId = "";

        try {

            response = new RequestOpenPay().execute(request).get();
            customerId = response.getString("id");
            ParseObject clientes = new ParseObject("Clientes");
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
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customerId ;

    }



    public static String crearTarjeta(Card card, String clientId){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"card_number\":\"" + card.getCardNumber() + "\",\n   \"holder_name\":\"" + card.getHolderName() + "\",\n   \"expiration_year\":\"" + card.getExpirationYear() + "\",\n   \"expiration_month\":\"" + card.getExpirationMonth() + "\",\n   \"cvv2\":\"" + card.getCvv2() + "\"\n }");
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+clientId+"/cards")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "51aac3bd-dfad-a02f-ebbb-986b00d47d07")
                .build();

        String respuesta  = null;

        try {
            respuesta = new RequestOpenPay("id").execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return respuesta;

    }



    // Pagos

    public static String[] pagarEnTienda(Integer precio, String fechaVigencia , String clientId){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"method\" : \"store\",\n   \"amount\" : " + precio + ",\n   \"description\" : \"Cargo con tienda\",\n   \"due_date\" : \"" + fechaVigencia + "\"\n} "); //2016-03-20T13:45:00
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+clientId+"/charges")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "e91bc385-a147-ec4c-7d06-9d96dcd96698")
                .build();



        try {

            String respuesta = new RequestOpenPay("payment_method").execute(request).get();
            JSONObject jsonObject = new JSONObject(respuesta);
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

    public static String suscribirsePlan(Card card, String clientId){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n   \"card_number\":\"" + card.getCardNumber() + "\",\n   \"holder_name\":\"" + card.getHolderName() + "\",\n   \"expiration_year\":\"" + card.getExpirationYear() + "\",\n   \"expiration_month\":\"" + card.getExpirationMonth() + "\",\n   \"cvv2\":\"" + card.getCvv2() + "\"\n }");
        Request request = new Request.Builder()
                .url(StarterApplication.URL + "" + StarterApplication.MERCHANT_ID + "/customers/"+clientId+"/cards")
                .post(body)
                .addHeader("authorization", "Basic c2tfNzUwNmI4MTgzYmMzNGUwMzhlZTllODQ5ZTJlNTI5OTQ6Og==")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "51aac3bd-dfad-a02f-ebbb-986b00d47d07")
                .build();

        String respuesta  = null;

        try {
            respuesta = new RequestOpenPay("id").execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return respuesta;

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




}
