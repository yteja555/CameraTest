package com.example.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton takePicture,savePicture,deletePicture;
    String currentPhotoPath;
    static final int REQUEST_CODE=1;
    TextView textView;


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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        if((requestCode==REQUEST_CODE)&&(resultCode==RESULT_OK))
        {
            Log.e("yuvateja",currentPhotoPath);
            Toast.makeText(this,currentPhotoPath,Toast.LENGTH_LONG).show();
//            takePicture.hide();
//            savePicture.show();
//            deletePicture.show();
//            File file = new File(currentPhotoPath);
//            Uri uri = Uri.fromFile(file);
////            Bitmap bitmap=null;
//            try {
//                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            }
//            catch(FileNotFoundException e)
//            {
//
//            }
//            catch(IOException e)
//            {
//
//            }
//            if(bitmap!=null) {
//                imageView.setImageBitmap(bitmap);
//            }
            getLicensePlate();
        }
    }
    private File createTempFile()throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getLicensePlate(){

        Log.e("In getLicensePlate()","called");

        try
        {
            String secret_key = "sk_7a7c343067c4e21cb93bc6f0";

            // Read image file to byte array
            Path path = Paths.get(currentPhotoPath);
            byte[] data = Files.readAllBytes(path);

            // Encode file bytes to base64
            byte[] encoded = Base64.getEncoder().encode(data);

            Log.e("Try", "in try block");

            // Setup the HTTPS connection to api.openalpr.com
            URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=us&secret_key=" + secret_key);
            Log.e("URL","url object");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setFixedLengthStreamingMode(encoded.length);
            http.setDoOutput(true);

            // Send our Base64 content over the stream
            try(OutputStream os = http.getOutputStream()) {
                os.write(encoded);
            }

            int status_code = http.getResponseCode();
            if (status_code == 200)
            {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream()));
                String json_content = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content += inputLine;
                in.close();

                textView = findViewById(R.id.json_alpr);
                textView.setText(json_content);
            }
            else
            {
                System.out.println("Got non-200 response: " + status_code);
            }


        }
        catch (MalformedURLException e)
        {
            System.out.println("Bad URL");
        }
        catch (IOException e)
        {
            System.out.println("Failed to open connection");
        }

    }

}

