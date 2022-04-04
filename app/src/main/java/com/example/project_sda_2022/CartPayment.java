package com.example.project_sda_2022;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_sda_2022.R;
import com.stripe.android.paymentsheet.PaymentSheet;

public class CartPayment extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    private static final String BACKEND_URL = "http://10.0.2.2:4242";

    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;

    private Button payButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout_java);

        // Hook up the pay button
        payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(this::onPayClicked);
        //payButton.setEnabled(false);
/*
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        fetchPaymentIntent();
    }
    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        });
    }
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
    private void fetchPaymentIntent() {
        final String shoppingCartContent = "{\"items\": [ {\"id\":\"xl-tshirt\"}]}";
        final RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                shoppingCartContent
                );
        Request request = new Request.Builder()
                .url(BACKEND_URL + "/create-payment-intent")
                .post(requestBody)
                .build();
        new OkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showAlert("Failed to load data", "Error: " + e.toString());
                    }
                    @Override
                    public void onResponse(
                            @NonNull Call call,
                            @NonNull Response response
                    ) throws IOException {
                        if (!response.isSuccessful()) {
                            showAlert(
                                    "Failed to load page",
                                    "Error: " + response.toString()
                            );
                        } else {
                            final JSONObject responseJson = parseResponse(response.body());
                            paymentIntentClientSecret = responseJson.optString("clientSecret");
                            runOnUiThread(() -> payButton.setEnabled(true));
                            Log.i(TAG, "Retrieved PaymentIntent");
                        }
                    }
                });
 */
    }
/*
    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error parsing response", e);
            }
        }
        return new JSONObject();
    }
 */

    private void onPayClicked(View view) {
        /*
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Example, Inc.");
        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
         */
        // edited*****
        Intent reviewOrderIntent = new Intent();
        boolean cardAccepted = true;
        reviewOrderIntent.putExtra("card_status", cardAccepted);
        setResult(78, reviewOrderIntent);
        CartPayment.super.onBackPressed();
    }
/*
    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast("Payment complete!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.i(TAG, "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error.getLocalizedMessage());
        }
    }
 */
}