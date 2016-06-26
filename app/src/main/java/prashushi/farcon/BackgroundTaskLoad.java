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
    String url="";
    ArrayList<String> params, values;
    String result="";
    public AsyncResponse delegate = null;

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
      /*  pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(this!=null)
                    this.cancel(true);
                loadProfileTask=null;
                Toast.makeText(ProfileStudents.this, "Try later!", Toast.LENGTH_LONG).show();
            }
        });
        */pDialog.show();

    }


    @Override
    protected Boolean doInBackground(Void... param) {
        //________________load image

        StringBuilder sb = new StringBuilder();
        try {

//            String data = URLEncoder.encode(params[0], "UTF-8") + "=" + URLEncoder.encode(values[0], "UTF-8");
            if(params.size()>0)
                url+="?"+params.get(0)+"="+values.get(0);
            for(int i=1;i<values.size();i++) {
                //              data += "&" + URLEncoder.encode(params[i], "UTF-8") + "=" + URLEncoder.encode(values[i], "UTF-8");
                url+="&"+params.get(i)+"="+values.get(i);
            }
            System.out.println(url);
            System.out.println("url-2");
            URL Url = new URL(url);
            URLConnection conn = Url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;// Read Server Response

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result=sb.toString();
        if(!result.contains("Falsexxx")||result.contains("Truexxx"))
            return true;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
//            mActivity.findViewById(R.id.result)
        if(pDialog!=null)
        pDialog.dismiss();
        System.out.println(result);
        delegate.processFinish(result);
    }

    public interface AsyncResponse {
        void processFinish(String output);

    }

}
