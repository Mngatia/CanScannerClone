package com.example.canscannerclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> filelist = new ArrayList<File>();
    PDFAdapter obj_adapter;
    File dir;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri imageUri;

    @Override
    protected void onResume(){
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {
        lv_pdf = (ListView) findViewById(R.id.lv_pdf);
        //Path to directory where all pdfs are saved
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CanScannerCloneStorage");
        getFile(dir);
        //completed getfile function
        //we set adapter to listview

        obj_adapter = new PDFAdapter(getApplicationContext(),filelist);
        lv_pdf.setAdapter(obj_adapter);
    }

    private ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length >0)
        {
            for (int i = 0;i<listFile.length;i++)
            {
                if (listFile[i].isDirectory())
                {
                    getFile(listFile[i]);
                }
                else
                 {
                     boolean booleanpdf = false;
                     //we need only.pdf file
                     if (listFile[i].getName().endsWith(".pdf"))
                     {
                         for (int j=0;j< filelist.size();j++)
                         {
                             if (fileList().getClass().equals(listFile[i].getName()))
                             {
                                 booleanpdf = true;
                             }
                         }
                         if (booleanpdf)
                         {
                             booleanpdf = false;
                         }
                         else
                          {
                              filelist.add(listFile[i]);

                          }
                     }
                 }

            }

        }
        return filelist;
    }

    public void check(View v) {
        //check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);

            } else {
                OpenCamera();
            }
        } else {
            OpenCamera();
        }
    }

    private void OpenCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Images");
        values.put(MediaStore.Images.Media.DESCRIPTION,"from the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_OK)
        {
            Intent i =new Intent(HomeActivity.this,ScanActivity.class);
            i.setData(imageUri);
            startActivity(i);

            //we send image url to next activity
        }
    }
}
