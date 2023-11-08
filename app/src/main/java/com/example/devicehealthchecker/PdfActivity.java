package com.example.devicehealthchecker;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PdfActivity extends AppCompatActivity {

    private ListView listView;
    private ImageView capturePdfButton;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        listView = findViewById(R.id.listView);
        capturePdfButton = findViewById(R.id.generatePdfButton);


        Intent intent = getIntent();
        ArrayList<String> receivedStringArray = intent.getStringArrayListExtra("STRING_ARRAY");

        if (receivedStringArray != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, receivedStringArray);
            listView.setAdapter(adapter);
        }

        capturePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePdf();
            }


        });
    }


    public void generatePdf() {
        // Get the root view of the activity
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_pdf, null, false);

        // Create a bitmap from the view
        // Get a resized bitmap from the view
        Bitmap bitmap = getResizedBitmapFromView(rootView, 300, 200);

        // Create a PdfDocument
        PdfDocument document = new PdfDocument();

        // Create a PageInfo and a Page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the bitmap onto the page
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        // Finish the page
        document.finishPage(page);

        // Create a PDF file
        File pdfFile = new File(Environment.getExternalStorageDirectory(), "activity_pdf.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            document.close();
            fos.close();

            Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public Bitmap getResizedBitmapFromView(View view, int targetWidth, int targetHeight) {
        // Ensure the view is not null
        if (view == null) {
            return null;
        }

        // Get the current dimensions of the view
        int width = view.getWidth();
        int height = view.getHeight();

        // Calculate the scale factors to fit the target dimensions
        float scaleWidth = ((float) targetWidth) / width;
        float scaleHeight = ((float) targetHeight) / height;

        // Create a matrix for the scaling
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // Create a Bitmap from the view with the specified dimensions
        return Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height, matrix, true);
    }



}


