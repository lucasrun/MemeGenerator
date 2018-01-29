package com.example.android.memegenerator;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST = 2;
    ImageView mImage;
    View mLayout;
    Button mButtonOpen, mButtonSend;
    EditText mEditLower, mEditUpper;
    TextView mTextLower, mTextUpper;
    String mImagePath, currentImage = "";
    Uri URI;

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checking permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            // empty here
        }

        mImagePath = Environment.getExternalStorageDirectory().toString();
        mImage = (ImageView) findViewById(R.id.ivImage);
        mImage.setImageResource(R.mipmap.ic_launcher);
        mLayout = (View) findViewById(R.id.layout);

        // text upper
        mEditUpper = (EditText) findViewById(R.id.et_upper);
        mTextUpper = (TextView) findViewById(R.id.tv_upper);

        // text lower
        mEditLower = (EditText) findViewById(R.id.et_lower);
        mTextLower = (TextView) findViewById(R.id.tv_lower);

        // buttons
        mButtonOpen = (Button) findViewById(R.id.but_open);
        mButtonSend = (Button) findViewById(R.id.but_send);

        // method for opening gallery
        openMeme(mButtonOpen);

        // method for sending meme graphic
        sendMeme(mButtonSend);
    }

    // setting picked graphic as image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // setting link for image
            URI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URI);
                mImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            return;
        }
    }

    // open image gallery
    private void openMeme(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Choose pic"), IMAGE_REQUEST);
            }
        });
    }

    // send image
    public void sendMeme(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // image preparation
                mTextUpper.setText(mEditUpper.getText().toString());
                mTextLower.setText(mEditLower.getText().toString());
                currentImage = "meme" + System.currentTimeMillis() + ".png";
                Bitmap bitmap = getScreenShot(mLayout);

                // saving & sending
                saveImage(bitmap, currentImage);
                sendImage(currentImage);
            }
        });
    }

    /*
     support methods
      */
    public void saveImage(Bitmap bitmap, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/meme";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Meme saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendImage(String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/meme";
        Uri uri = Uri.fromFile(new File(dirPath, fileName));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "Meme generated by MemeGenerator");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(intent, "Share using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No apps available for sharing", Toast.LENGTH_SHORT).show();
        }
    }
}