package com.example.canscannerclone.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitClient = null;
    private static Retrofit getClient ()
    {
        if (retrofitClient==null)
        {
            retrofitClient = new Retrofit.Builder()
                    .baseUrl("http://3.14.73.171")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }
}