package prashushi.farcon;

/**
 * Created by Dell User on 6/23/2016.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


class BackgroundTaskLoad extends AsyncTask<Void, Void, Boolean> {
    public AsyncResponse delegate = null;
    String url="";
    ArrayList<String> params, values;
    String result="";
    Context activity;
    ProgressDialog pDialog;
    BackgroundTaskLoad(String url, Context activity, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;
        this.activity=activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Loading . Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }


    @Override
    protected Boolean doInBackground(Void... param) {
        StringBuilder sb = new StringBuilder();
        try {
            if(params.size()>0)
                url+="?"+params.get(0)+"="+values.get(0);
            for(int i=1;i<values.size();i++) {
                url+="&"+params.get(i)+"="+values.get(i);
            }
            System.out.println(url);
            URL Url = new URL(url);
            URLConnection conn = Url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;// Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result=sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result="falsexxx";
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(pDialog!=null)
        pDialog.dismiss();
        System.out.println(result);
        delegate.processFinish(result);
    }

    public interface AsyncResponse {
        void processFinish(String output);

    }

}

