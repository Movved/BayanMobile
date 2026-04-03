package com.example.bayan;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    
    private static final String BASE_URL = "https://lknjweohvexqimvmdlis.supabase.co/rest/v1/";
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxrbmp3ZW9odmV4cWltdm1kbGlzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ5ODMyMTIsImV4cCI6MjA5MDU1OTIxMn0.gJEtqsJWuWN9hvi0w7SVbKbv1D1mxR8IWO78wIEhrOU";
    public static final String AUTH = "Bearer " + API_KEY;

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("apikey", API_KEY)
                                .addHeader("Authorization", AUTH)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SupabaseApi getApi() {
        return getInstance().create(SupabaseApi.class);
    }
}