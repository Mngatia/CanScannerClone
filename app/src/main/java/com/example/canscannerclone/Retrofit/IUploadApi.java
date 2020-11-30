package com.example.canscannerclone.Retrofit;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IUploadApi {

    @Multipart
    @POST("/api/upload")
    Call<String> uploadfile(@Part MultipartBody.Part file);
}
