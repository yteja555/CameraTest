package com.example.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton takePicture,savePicture,deletePicture;
    String currentPhotoPath;
    static final int REQUEST_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.imageView);
        takePicture=(FloatingActionButton)findViewById(R.id.takePicture);
        savePicture=(FloatingActionButton)findViewById(R.id.saveButton);
        deletePicture=(FloatingActionButton)findViewById(R.id.deleteButton);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                launchCamera();
            }
        });



    }
    public void launchCamera()
    {
        Log.v("yuva camera launch  ","method called");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.v("yuva intent  ","created");
        if(cameraIntent.resolveActivity(getPackageManager())!=null)
        {
            File tempFile=null;
            try
            {
                tempFile=createTempFile();
            }
            catch (IOException e)
            {

            }
            if(tempFile!=null)
            {
                Log.v("yuva Temporary file ","Done");
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this,"com.example.cameratest.fileprovider",tempFile);
                Log.v("yuva Uri ","Done");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                Log.v("yuva put extra ","Done");
                startActivityForResult(cameraIntent,REQUEST_CODE);
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        if((requestCode==REQUEST_CODE)&&(resultCode==RESULT_OK))
        {


            takePicture.hide();
            savePicture.show();
            deletePicture.show();
            File file = new File(currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap=null;
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            }
            catch(FileNotFoundException e)
            {

            }
            catch(IOException e)
            {

            }
            if(bitmap!=null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    private File createTempFile()throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
