package com.oxi.software.utilities.http;

import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponseHttpApi {

    public static final String CODE_OK = "200";
    public static final String CODE_BAD = "400";
    public static final String NO_CONTENT = "204";

    //findAll
    public static Map<String, Object> responseHttpFindAll(Object data, HttpStatus code, String msm, int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("date", new Date());
        response.put("code", code);
        response.put("message", msm);
        response.put("totalItems", size);
        response.put("data", data);

        return response;
    }

    public static Map<String, Object> responseHttpFind(Object data, HttpStatus code,String msm, int size, int page, int items) {
        Map<String, Object> response = new HashMap<>();
        response.put("date", new Date());
        response.put("code", code);
        response.put("message", msm);
        response.put("currentPage", page);
        response.put("totalItems", items);
        response.put("totalPages", size);
        response.put("data",data);

        return response;
    }

    //findById
    public static Map<String, Object> responseHttpFindId(Object data, String code,String msm) {
        Map<String, Object> response = new HashMap<>();
        response.put("date", new Date());
        response.put("code", code);
        response.put("message", msm);
        response.put("data",data);

        return response;
    }

    //Post, Put and Delete
    public static Map<String, Object> responseHttpAction(String code, String msm) {

        Map<String,Object> response = new HashMap<>();

        response.put("date",new Date());
        response.put("code",code);
        response.put("message",msm);
        return response;
    }

    //Post
    public static Map<String,Object> responseHttpPost(String result, HttpStatus codeMessage){

        Map<String, Object> response = new HashMap<>();
        response.put("date",new Date());
        response.put("code",codeMessage.value());
        response.put("message",result);
        return response;

    }

    public static Map<String,Object> responseHttpPut(String result, HttpStatus codeMessage){

        Map<String, Object> response = new HashMap<>();
        response.put("date",new Date());
        response.put("code",codeMessage.value());
        response.put("message",result);
        return response;

    }

    public static Map<String, Object> responseHttpMassive(String code, String message, Map<String, Object> result) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("successCount", result.get("successCount"));
        response.put("errors", result.get("errors"));
        return response;
    }

    //Error
    public static Map<String,Object> responseHttpError(String result, HttpStatus codeMessage, String data){
        Map<String,Object> response = new HashMap<>();

        response.put("date",new Date());
        response.put("code",codeMessage.value());
        response.put("message",result);
        response.put("data",data);

        return response;
    }

}
