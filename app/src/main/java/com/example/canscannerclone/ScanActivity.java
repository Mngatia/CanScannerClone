package com.example.canscannerclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.example.canscannerclone.Retrofit.IUploadApi;
import com.example.canscannerclone.Retrofit.RetrofitClient;
import com.example.canscannerclone.Utils.IUploadCallbacks;

public class ScanActivity extends AppCompatActivity implements IUploadCallbacks {

    //Initialize all services and give reference to ui elements
    ImageView imageView,btnUpload;
    IUploadApi mService;
    Uri selectedFileUri;
    ProgressDialog dialog;

    private IUploadApi getApiUpload()
    {
        return RetrofitClient.getClient().create(IUploadApi.class);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mService = getApiUpload();

        imageView = (ImageView)findViewById(R.id.image_view);
        btnUpload = (ImageView)findViewById(R.id.button_upload);

        Uri image_uri = getIntent().getData();
        imageView.setImageURI(image_uri);
        selectedFileUri = image_uri;  //uri for image that we take from camera

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        //check the uri of the file

    }

    @Override
    public void onProgressUpdate(int percent) {

    }
}
