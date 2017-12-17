package com.example.android.memegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

public class MainActivity extends AppCompatActivity {

    public int IMAGE_REQUEST = 1;
    ImageView IV;
    EditText textDol, textGora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ustawianie przy wlaczeniu
        IV = (ImageView) findViewById(R.id.imageView1);
        IV.setImageResource(R.mipmap.ic_launcher);

        //text gora
        textGora = (EditText) findViewById(R.id.Text_gora);
        String text_gora = textGora.getText().toString();

        //text dol
        textDol = (EditText) findViewById(R.id.Text_dol);
        String text_dol = textDol.getText().toString();

        /*
        DEFINICJE PRZYCISKÓW PONIŻEJ
         */

        // przycisk zmien
        Button change = (Button) findViewById(R.id.button_change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zmien = new Intent(Intent.ACTION_PICK);
                zmien.setType("image/*");
                zmien.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(zmien, "Wybierz zdjęcie"), IMAGE_REQUEST);

            }
        });

        // przycisk wyslij
        Button send = (Button) findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wyslij = new Intent(Intent.ACTION_SEND);
                wyslij.setType("text/plain");

                // tworzenie folderu zapisu
                String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                File myDir = new File(rootDir + "/my_memes");
                myDir.mkdirs();


                // tworzenie sciezki do pliku
                File root = Environment.getExternalStorageDirectory();
                String pathToMyAttachedFile = "my_memes/meme.jpg";
                File file = new File(root, pathToMyAttachedFile);
                if (!file.exists() || !file.canRead()) {
                    return;
                }
                Uri uri = Uri.fromFile(file);
                wyslij.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(wyslij, "Wyślij"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                IV.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void saveImage(){
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.meme); // the original file yourimage.jpg i added in resources
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        String topText = "My top text";
        String bottomText = "My bottom text";

        // formatowanie czcionki
        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(35);
        tPaint.setColor(Color.BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(src, 0f, 0f, null);

        // umieszczanie tekstu gornego
        float width = tPaint.measureText(topText);
        float x_coord = (src.getWidth() - width)/2;
        cs.drawText(topText, x_coord, 100, tPaint);

        // umieszczanie tekstu dolnego
        width = tPaint.measureText(bottomText);
        x_coord = (src.getWidth() - width)/2;
        cs.drawText(bottomText, x_coord, 300, tPaint);

        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/ImageAfterAddingText.jpg")));
            // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}