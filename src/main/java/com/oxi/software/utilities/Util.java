package com.oxi.software.utilities;

import com.oxi.software.business.UserBusiness;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.util.Map;



@Component

public class Util {
    public static final Logger logger = LogManager.getLogger(Util.class);
    public static JSONObject getData(Map<String, Object> json) throws JSONException {
        logger.debug("Entering getData method");  // Log de inicio del method
        JSONObject jsonObject = null;
        try {
            // Log de los datos de entrada
            logger.debug("Received JSON: " + json);

            jsonObject = new JSONObject(json);

            // Verificar que el objeto JSON tiene la clave "data"
            if (jsonObject.has("data")) {
                logger.debug("Key 'data' found in the JSON");
            } else {
                logger.warn("Key 'data' not found in the JSON");
            }


            JSONObject dataObject = jsonObject.getJSONObject("data");
            logger.debug("Data object extracted: " + dataObject);  // Log de lo que se extrae

            return dataObject;
        } catch (JSONException e) {
            logger.error("Error parsing JSON", e);  // Log de error si hay problemas con el JSON
            throw e;  // Rethrow the exception after logging it
        } finally {
            logger.debug("Exiting getData method");  // Log de salida del método
        }
    }
}
