package com.oxi.software.utilities;

import com.oxi.software.business.UserBusiness;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.util.Map;
import org.apache.log4j.Logger;


@Component

public class Util {
    private static final Logger logger = Logger.getLogger(UserBusiness.class);
    public static JSONObject getData(Map<String, Object> json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        return dataObject;
    }
}
