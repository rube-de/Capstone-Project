package de.ruf2.rube.fridgeorganizer.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.ruf2.rube.fridgeorganizer.BuildConfig;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 16.12.2016.
 */

public class ProductService extends IntentService {

    public static final String FETCH_PRODUCT = "de.ruf2.rube.fridgeorganizer.services.FETCH_PRODUCT";
    public static final String EAN = "de.ruf2.rube.fridgeorganizer.services.EAN";

    public ProductService(){
        super("FridgeOrganizer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_PRODUCT.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                fetchProduct(ean);

            }

        }
    }

    private void fetchProduct (String ean){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String productJsonString = null;

        try {
            final String DB_BASE_URL = "https://api.outpan.com/v2/products/";
            final String EAN_PARAM = ean;

            final String API_PARAM = "apikey";
            final String API_KEY = BuildConfig.OUTPAN_API_KEY;

            Uri builtUri = Uri.parse(DB_BASE_URL+EAN_PARAM+"?").buildUpon()
                    .appendQueryParameter(API_KEY, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            productJsonString = buffer.toString();
            getProductDataFromJSON(productJsonString, ean);
        } catch (Exception e) {
            Timber.e(e, "Error");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Timber.e(e,"Error closing stream");
                }
            }

        }


    }

    private void getProductDataFromJSON(String productJsonString, String ean) {
        final String NAME = "name";

        try {
            JSONObject productJson = new JSONObject(productJsonString);


            String productName = "";
            if (productJson.has(NAME)){
                productName = productJson.getString(NAME);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}