package com.example.canscannerclone.Utils;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import static android.os.Looper.getMainLooper;

//code for progress updater and to upload file on server

public class ProgressRequestBody extends RequestBody {

    private IUploadCallbacks listener;

    public ProgressRequestBody(IUploadCallbacks listener, File file) {
        this.listener = listener;
        this.file = file;
    }

    private File file;
    private static int DEFAULT_BUFFER_SIZE = 4096;



    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        FileInputStream in = new FileInputStream(file);
        long uploaded = 0;
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1)
            {
                handler.post(new ProgressUpdater(uploaded,fileLength));
                uploaded += read;
                sink.write(buffer, 0,read);
            }
        }finally {
            in.close();
        }

    }

    public class ProgressUpdater implements Runnable
    {
        private long uploaded;
        private long fileLength;

        public ProgressUpdater(long uploaded, long fileLength) {
            this.uploaded = uploaded;
            this.fileLength = fileLength;
        }

        @Override
        public void run() {
            listener.onProgressUpdate((int)(100*uploaded/fileLength));

        }
    }

}
