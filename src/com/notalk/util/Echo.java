package com.notalk.util;

import com.google.gson.Gson;

public  class Echo {
    public static void echo(Object object){
        Gson gson = new Gson();
        System.out.println(gson.toJson(object));
    }
}
