package com.devworms.toukan.mangofrida.util;

/**
 * Created by sergio on 05/12/16.
 */

public class ErroresOpenpay {

    public static String getError(String errorCode){

        String mensaje = "";

        switch (errorCode){
            case "1000":	mensaje = "Internal Server Error	Ocurrió un error interno en el servidor de Openpay"; break;
            case "1001":	mensaje = "El formato de la petición no es JSON, los campos no tienen el formato correcto, o la petición no tiene campos que son requeridos"; break;
            case "1002":	mensaje = "La llamada no esta autenticada o la autenticación es incorrecta"; break;
            case "1003":	mensaje = "422 Unprocessable Entity	La operación no se pudo completar por que el valor de uno o más de los parametros no es correcto"; break;
            case "1004":	mensaje = "Un servicio necesario para el procesamiento de la transacción no se encuentra disponible"; break;
            case "1005":	mensaje = "Uno de los recursos requeridos no existe"; break;
            case "1006":	mensaje = "Ya existe una transacción con el mismo ID de orden"; break;
            case "1007":	mensaje = "La transferencia de fondos entre una cuenta de banco o tarjeta y la cuenta de Openpay no fue aceptada"; break;
            case "1008":	mensaje = "Una de las cuentas requeridas en la petición se encuentra desactivada"; break;
            case "1009":	mensaje = "El cuerpo de la petición es demasiado grande"; break;
            case "1010":	mensaje = "Se esta utilizando la llave pública para hacer una llamada que requiere la llave privada, o bien, se esta usando la llave privada desde JavaScript"; break;

            case "2001":	mensaje = "La cuenta de banco con esta CLABE ya se encuentra registrada en el cliente"; break;
            case "2002":	mensaje = "La tarjeta con este número ya se encuentra registrada en el cliente"; break;
            case "2003":	mensaje = "El cliente con este identificador externo (External ID) ya existe"; break;
            case "2004":	mensaje = "El dígito verificador del número de tarjeta es inválido de acuerdo al algoritmo Luhn"; break;
            case "2005":	mensaje = "La fecha de expiración de la tarjeta es anterior a la fecha actual"; break;
            case "2006":	mensaje = "El código de seguridad de la tarjeta (CVV2) no fue proporcionado"; break;
            case "2007":	mensaje = "El número de tarjeta es de prueba, solamente puede usarse en Sandbox"; break;
            case "2008":	mensaje = "La tarjeta consultada no es valida para puntos"; break;

            case "3001":    mensaje = "La tarjeta fue declinada"; break;
            case "3002":    mensaje = "La tarjeta ha expirado"; break;
            case "3003":	mensaje = "La tarjeta no tiene fondos suficientes"; break;
            case "3004":	mensaje = "La tarjeta ha sido identificada como una tarjeta robada"; break;
            case "3005":	mensaje = "La tarjeta ha sido identificada como una tarjeta fraudulenta"; break;
            case "3006":	mensaje = "La operación no esta permitida para este cliente o esta transacción"; break;
            case "3008":	mensaje = "La tarjeta no es soportada en transacciones en linea"; break;
            case "3009":	mensaje = "La tarjeta fue reportada como perdida"; break;
            case "3010":	mensaje = "El banco ha restringido la tarjeta"; break;
            case "3011":	mensaje = "El banco ha solicitado que la tarjeta sea retenida. Contacte al banco"; break;
            case "3012":	mensaje = "Se requiere solicitar al banco autorización para realizar este pago"; break;

            case "4001":	mensaje = "La cuenta de Openpay no tiene fondos suficientes"; break;
        }

        return mensaje;
    }
}
