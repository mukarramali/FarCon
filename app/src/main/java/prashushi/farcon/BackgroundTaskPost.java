package prashushi.farcon;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;



class BackgroundTaskPost extends AsyncTask<Void, Void, Boolean> {
    public AsyncResponse delegate = null;
    String url="";
    ArrayList<String> params, values;
    String result="";
    Boolean loader;
    Context context;
    long time;
    AlertDialog alertDialog;
    String msg;
    ProgressDialog progressDialog;
    BackgroundTaskPost(String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;
        this.loader = false;
        time = (new Date().getTime()) % 100000;
        System.out.println("Constructor1 time:" + time);
    }

    BackgroundTaskPost(Context context, String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.delegate = delegate;
        this.loader = true;
        this.context = context;
        time = (new Date().getTime()) % 100000;
        System.out.println("Constructor2 time:" + time);
    }

    BackgroundTaskPost(Context context, String msg, String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.delegate = delegate;
        this.loader = true;
        this.context = context;
        time = (new Date().getTime()) % 100000;
        this.msg = msg;
        System.out.println("Constructor2 time:" + time);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        time = (new Date().getTime()) % 100000;
        System.out.println("PreExecute Loader:" + time);

        if (loader) {
            progressDialog = new ProgressDialog(context);
            String title = "Loading...";
            if (msg != null)
                title = msg;
            progressDialog.setMessage(title);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        time = (new Date().getTime()) % 100000;
        System.out.println("PreExecute Loader2:" + time);
    }


    @Override
    protected Boolean doInBackground(Void... param) {
//        time=(new Date().getTime())%100000;
        //      System.out.println("doInBackground1:"+time);

        StringBuilder sb = new StringBuilder();
        try {
            String data = "";
            if (params.size()>0) {
//                System.out.println(params.get(0)+":"+values.get(0));
                data = URLEncoder.encode(params.get(0), "UTF-8") + "=" + URLEncoder.encode(values.get(0), "UTF-8");
                System.out.print(params.get(0) + ":" + values.get(0) + "  ");
            }
            for (int i = 1; i < values.size() && i < params.size(); i++)
            {    data += "&" + URLEncoder.encode(params.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
                System.out.print(params.get(i) + ":" + values.get(i) + "  ,");
            }
            System.out.println();
            System.out.println(url);
            URL Url = new URL(url);
            URLConnection conn = Url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            time = (new Date().getTime()) % 100000;
//          System.out.println("doInBackground3:"+time);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line ;// Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result=sb.toString();
            time = (new Date().getTime()) % 100000;
            System.out.println("doInBackground4:" + time);
        } catch (Exception e) {
            e.printStackTrace();
            result="falsexxx";
        }
        return false;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        System.out.println(result);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        delegate.processFinish(result);
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }
}
