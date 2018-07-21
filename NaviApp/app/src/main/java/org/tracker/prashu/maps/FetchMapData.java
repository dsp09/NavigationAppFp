package org.tracker.prashu.maps;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class FetchMapData extends AsyncTask<Void, Void, String> {

    // declaration of variables.
    private String mUrl;
    private Context mContext;
    private DataInterface mDataInterface;
    URL urlGot;

    // constructor of the FetchData class.
    public FetchMapData(Context context, String url, DataInterface dataInterface){
        // setting internal variables of the class as the passed variables.
        mContext = context;
        mUrl = url;
        mDataInterface = dataInterface;
        try {
            urlGot = new URL(url);
        } catch (Exception e) {
            Toast.makeText(context, "URI CONVERSION: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    // overridden method of the AsyncTask class to do the task in background.
    @Override
    protected String doInBackground(Void... voids) {
        String responseData = null;

        try {
        // creating an object of the OkHttpClient.
        // dependency is added for this class in the build.gradle file.
        OkHttpClient okHttpClient = new OkHttpClient();
        // if the connection is unable to establish then wait for 2 minutes for it to connect and then timeout and end process.
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        // if reading of the URL is unable to establish then wait for 2 minutes for it to happen and then timeout and end process.
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        // getting the request for the JSON URL.
        Request request = new Request.Builder()
                .url(urlGot)
                .build();



            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                // response contains the protocol of the URL
                responseData = response.body().string();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(mContext, "RESOPNSE: " +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return responseData;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        // sending the response data to the interface created before.
        mDataInterface.fetchedData(response);
    }
}
