package com.example.android.memegenerator;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    ImageView mImage;
    Button mButtonOpen, mButtonSend;
    EditText mTextLower, mTextUpper;
    Drawable drawable;
    Bitmap bitmap;
    String mImagePath;
    Uri URI;
    File file;
    ContextWrapper wrapper;
    private String mTextUpperString, mTextLowerString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImagePath = Environment.getExternalStorageDirectory().toString();

        mImage = (ImageView) findViewById(R.id.ivImage);
        mImage.setImageResource(R.mipmap.ic_launcher);

        // text upper
        mTextUpper = (EditText) findViewById(R.id.et_upper);
        mTextUpperString = mTextUpper.getText().toString();

        // text lower
        mTextLower = (EditText) findViewById(R.id.et_lower);
        mTextLowerString = mTextLower.getText().toString();

        // buttons
        mButtonOpen = (Button) findViewById(R.id.but_open);
        mButtonSend = (Button) findViewById(R.id.but_send);

        // method for opening gallery
        openImage(mButtonOpen);

        // method for sending meme graphic
        sendImage(mButtonSend);
    }

    // setting picked graphic as image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

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

    // open image gallery
    private void openImage(Button button) {
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
    public void sendImage(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                generateImage();

//                saveImageInternal();
                /* OR */
//                saveImageExternal();

                send();

            }
        });
    }

    public void saveImageExternal() {
        drawable = getResources().getDrawable(R.drawable.meme);
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        file = new File(mImagePath, "meme.jpg");

        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveImageInternal() {
        drawable = getResources().getDrawable(R.drawable.meme);
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        wrapper = new ContextWrapper(getApplicationContext());
        file = wrapper.getDir("Images", MODE_PRIVATE);
        file = new File(file, "meme.jpg");

        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateImage() {

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.id.ivImage);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        // setting font
        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(35);
        tPaint.setColor(Color.BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(src, 0f, 0f, null);

        // setting upper text
        float width = tPaint.measureText(mTextUpperString);
        float x_coord = (src.getWidth() - width) / 2;
        cs.drawText(mTextUpperString, x_coord, 100, tPaint);

        // setting lower text
        width = tPaint.measureText(mTextLowerString);
        x_coord = (src.getWidth() - width) / 2;
        cs.drawText(mTextLowerString, x_coord, 300, tPaint);

        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "meme.jpg")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This meme was generated by Meme Generator");
        if (URI != null) {
            intent.putExtra(Intent.EXTRA_STREAM, URI);
        }
        this.startActivity(Intent.createChooser(intent, "Sending..."));
    }
}