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

    public static Map<String, Object> responseHttpAuth(HttpStatus status, String mensaje, String jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("status", status.value());
        response.put("message", mensaje);
        response.put("jwt", jwt);
        return response;
    }

//    public static Map<String, Object> responseHttpFind(Object data, HttpStatus code,String msm, int size, int page, int items) {
//        Map<String, Object> response = new HashMap<>();

//        response.put("date", new Date());
//        response.put("code", code);
//        response.put("message", msm);
//        response.put("currentPage", page);
//        response.put("totalItems", items);
//        response.put("totalPages", size);
//        response.put("data",data);
//        return response;
//    }

    //findById
    public static Map<String, Object> responseHttpFindId(Object data, String code,String msm) {
        Map<String, Object> response = responseHttpAction(code, msm);

        response.put("data",data);
        return response;
    }

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

//    public static Map<String,Object> responseHttpPut(String result, HttpStatus codeMessage){
//        Map<String, Object> response = new HashMap<>();

//        response.put("date",new Date());
//        response.put("code",codeMessage.value());
//        response.put("message",result);
//        return response;
//
//    }
//
//    //Error
//    public static Map<String,Object> responseHttpError(String result, HttpStatus codeMessage, String data){
//        Map<String,Object> response = new HashMap<>();
//
//        response.put("date",new Date());
//        response.put("code",codeMessage.value());
//        response.put("message",result);
//        response.put("data",data);
//        return response;
//    }

}
