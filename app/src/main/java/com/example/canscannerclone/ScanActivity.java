package com.example.canscannerclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.canscannerclone.Retrofit.IUploadApi;
import com.example.canscannerclone.Retrofit.RetrofitClient;
import com.example.canscannerclone.Utils.Common;
import com.example.canscannerclone.Utils.IUploadCallbacks;
import com.example.canscannerclone.Utils.ProgressRequestBody;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity implements IUploadCallbacks {

    //Initialize all services and give reference to ui elements
    ImageView imageView,btnUpload,Save,Cancel;
    IUploadApi mService;
    Uri selectedFileUri;
    //ProgressBar dialog;
    ProgressDialog dialog;

    //One string to save image name
    public String image_name="";


    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;



    private IUploadApi getApiUpload()
    {
        return RetrofitClient.getClient().create(IUploadApi.class);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mService = getApiUpload();

        imageView =(ImageView) findViewById(R.id.image_view);
        btnUpload = (ImageView)findViewById(R.id.button_upload);
        Save = (ImageView)findViewById(R.id.save);
        Cancel = (ImageView)findViewById(R.id.cancel);

        Uri image_uri = getIntent().getData();
        imageView.setImageURI(image_uri);
        selectedFileUri = image_uri;  //uri for image that we take from camera

        //cancel and save function
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click cancel we will redirect to home activity without saving any image
                Intent i =new Intent(ScanActivity.this,HomeActivity.class);
                startActivity(i);
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream outputStream= null;

                //path to save file
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath()+"/CanScannerCloneStorage");
                directory.mkdir();
                //directory with the name CanScannerCloneStorage
                String fileName = image_name+".jpg";
                File outFile = new File(directory,fileName);

                try {
                    outputStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Document document =  new Document();

                String directoryPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/CanScannerCloneStorage";

                try {
                    PdfWriter.getInstance(document,new FileOutputStream(directoryPath +"/"+ image_name+".pdf"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                document.open();

                Image image = null;
                try {
                    image = Image.getInstance(directoryPath+"/"+image_name+".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() -0) /image.getWidth())*100;
                //0 means you have no indentation

                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

                try {
                    document.add(image);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
                Toast.makeText(ScanActivity.this, "PDF Saved Successfully..", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ScanActivity.this,HomeActivity.class);
                startActivity(i);

                //This point we create a directory path read image with their name and open pdf and write in pdf
                //After pdf saved successfully redirect to home where we can see saved pdf

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        //check the uri of the file
        if (selectedFileUri != null)
        {
            dialog = new ProgressDialog(ScanActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading...");
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();

            //Load actual file from uri
            File file =null;
            try
            {
             file = new File(Common.getFilePath(this,selectedFileUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (file !=null)
            {
                //If we get file and it is not empty then
                final ProgressRequestBody requestBody = new ProgressRequestBody(this,file);

                final MultipartBody.Part body = MultipartBody.Part.createFormData("image",file.getName(),requestBody);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.uploadfile(body)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        //Image link processed on server so that we can update this processed image to show result in the app

                                        String image_processed_link = "http://3.14.73.171/" +
                                                response.body().replace("\"", "");

                                        Toast.makeText(ScanActivity.this, "Please wait, Image is processing..", Toast.LENGTH_LONG).show();

                                        //we will use picasso to load the image output
                                        Picasso.get()
                                                .load(image_processed_link)
                                                .fit().centerInside()
                                                .rotate(90)
                                                .into(imageView);   //set image into image view

                                        //After loading image to image view, we have to

                                        image_name += response.body().replace("\"", "").split("/")[1];

                                        btnUpload.setVisibility(View.INVISIBLE);
                                        Save.setVisibility(View.VISIBLE);
                                        Cancel.setVisibility(View.VISIBLE);

                                        //we can ave and cancel image
                                        //We ned image name to save image name from response.body

                                        dialog.dismiss();


                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(ScanActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }).start();
            }
        }
        else
        {
            Toast.makeText(this, "Cannot upload this file..", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onProgressUpdate(int percent){

    }
}
