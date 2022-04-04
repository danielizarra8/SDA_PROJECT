package com.example.project_sda_2022;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.pdf.PdfDocument;

import com.example.project_sda_2022.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OrderConfirmed extends AppCompatActivity {

    public  TextView mOrderSummary, mCustomerName, mCustomerID, mCurrentDateOrder,
            mExpectedOrder,mTotalAmount, mTotalQty, mCustomerPhone, mCustomerAddress;
    public Button mGoBackBtn, mDownloadPDFBtn;
    LinearLayout linearLayout;
    Bitmap bitmap;
    String userName, userId, orderDate, orderExpected, userPhone,userAddress, totalAmount, totalQty;
    String fName ="macys_invoice" + String.valueOf(System.currentTimeMillis());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        userName = getIntent().getStringExtra("customer_name");
        userId = getIntent().getStringExtra("customerID");
        totalAmount = getIntent().getStringExtra("order_amount");
        totalQty = getIntent().getStringExtra("order_qty");
        orderDate = getIntent().getStringExtra("order_date");
        orderExpected = getIntent().getStringExtra("order_expected");
        userPhone = getIntent().getStringExtra("customer_phone");
        userAddress = getIntent().getStringExtra("customer_address");

        mOrderSummary = findViewById(R.id.ordersummaryTxt); mCustomerName = findViewById(R.id.orderUserName);
        mCustomerID = findViewById(R.id.orderUserID); mCustomerPhone = findViewById(R.id.orderPhoneProfile);
        mCustomerAddress = findViewById(R.id.orderAddressProfile); mCurrentDateOrder = findViewById(R.id.orderCurrentDate);
        mTotalAmount = findViewById(R.id.orderTotalAmount); mTotalQty = findViewById(R.id.orderTotalQty);
        mExpectedOrder = findViewById(R.id.orderExpectDate);
        mGoBackBtn = findViewById(R.id.gobackBtn);
        mDownloadPDFBtn = findViewById(R.id.getInvoiceBtn);

        linearLayout = findViewById(R.id.mainContent);

        if (!checkPermission()){

            requestPermission();
        }
        mCustomerName.setText("Customer Name: "+ userName); mCustomerID.setText("ID: "+userId);
        mCustomerAddress.setText("Address: "+userAddress); mCustomerPhone.setText("Phone: "+userPhone);
        mCurrentDateOrder.setText("Date: "+orderDate); mExpectedOrder.setText("Arriving by: "+orderExpected);
        mTotalQty.setText("Number of Products: "+totalQty); mTotalAmount.setText("Total Amount: "+totalAmount);

        mOrderSummary.setText(" Congratulations, you have successfully placed your order!" + "\n" +
                "Please find your order summary below:" + "\n \n" +
                "CUSTOMER NAME: " + userName + "\n" +
                "ORDER PLACED: " + orderDate  + "\n" +
                "EXPECTED DELIVERY: " + orderExpected + "\n \n" +
                "TOTAL ITEMS:  " + totalQty + "\n" +
                "TOTAL AMOUNT:  " + totalAmount + "\n \n" +
                "Thank you for shopping with Macys!");

        mDownloadPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bitmap = LoadBitmap(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
                createPDF();
            ///
            }
        });

        mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private Bitmap LoadBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        }
        else{
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return bitmap;
    }

    private void createPDF(){
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        int convertWidth = (int)width;
        int convertHeight = (int)height;

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth,convertHeight,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap,convertWidth,convertHeight,true);

        canvas.drawBitmap(bitmap,0,0,null);
        pdfDocument.finishPage(page);


        File file;
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                "/" + fName + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        }catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong! " + e.toString(), Toast.LENGTH_SHORT).show();
        }
            pdfDocument.close();
            Toast.makeText(getApplicationContext(), "Invoice successfully created!", Toast.LENGTH_SHORT).show();

           //openPdf(file);

    }
    private void openPdf(File filepdf){
        File file = filepdf;
        if (file.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                Toast.makeText(getApplicationContext(),"No application for pdf view found!", Toast.LENGTH_SHORT).show();

            }
        }

    }
    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(OrderConfirmed.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(OrderConfirmed.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(OrderConfirmed.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1100);
        }
    }

}