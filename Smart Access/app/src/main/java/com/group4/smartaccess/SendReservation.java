package com.group4.smartaccess;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendReservation extends AsyncTask<String, Void, String> {
public AsyncResponse delegate = null;
String payload;
        String server_response;
        String request;
        TextView textView;
private KioskCheckInActivity kioskCheckInActivity;

public SendReservation(KioskCheckInActivity kioskCheckInActivity) {
        this.kioskCheckInActivity = kioskCheckInActivity;
        }

public SendReservation (TextView textView){
        this.textView = textView;
        }

public SendReservation(){}

public String getServer_response() {
        return server_response;
        }

public void setServer_response(String server_response) {
        this.server_response = server_response;
        }

public String getRequest() {
        return request;
        }

public void setRequest(String request) {
        this.request = request;
        }

@Override
protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection conn = null;
        payload = strings[1]; // should contain the payload

        try{
        url = new URL(strings[0]);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();
        // HERE ARE THE PARAMETERS FOR THE JSON OBJECT //
            // NEED NEW PARAPMETERS
        JSONObject params = new JSONObject();
        params.put("$class", "org.example.basic.checkIn");
        params.put("guest", "resource:org.example.basic.Guest#9208");
        params.put("res_code", payload);

        Log.i("JSON", params.toString());
        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(params.toString());

        os.flush();
        os.close();

        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
        Log.i("MSG" , conn.getResponseMessage());

        request = conn.getRequestMethod();
        int code = conn.getResponseCode();

        if(code == HttpURLConnection.HTTP_OK){
        server_response = readStream(conn.getInputStream());
        Log.v("CatalogClient", server_response); // Log read in Logcat
        }
        else{
        server_response = "Failed: code was " + code + ". Not \"OK\"";
        }
        }catch(MalformedURLException e){
        e.printStackTrace();
        }catch (IOException | JSONException e){
        e.printStackTrace();
        }

        return server_response;
        }

// Return the information here
@Override
protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d("SERVER:", "" + server_response);
        Log.e("Response", "" + server_response); // Log read in Logcat
        Log.e("Payload", "" + payload); // Log read in Logcat
        //setServer_response(s);
        //textView.setText(server_response);
       // delegate.processFinish(server_response); // this allows the result to be used in the Activity
        kioskCheckInActivity.updateView();
        }

// Convert inputstream to String
private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
        reader = new BufferedReader(new InputStreamReader(in));
        String line = "";
        while ((line = reader.readLine()) != null) {
        response.append(line);
        }
        } catch (IOException e) {
        e.printStackTrace();
        } finally {
        if (reader != null) {
        try {
        reader.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        }
        return response.toString();
        }

        }
