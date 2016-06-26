package prashushi.farcon;



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


class BackgroundTaskPost extends AsyncTask<Void, Void, Boolean> {
    String url="";
    ArrayList<String> params, values;
    String result="";
    public AsyncResponse delegate = null;
    BackgroundTaskPost(String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;
    }
    @Override
    protected Boolean doInBackground(Void... param) {
        //________________load image
        StringBuilder sb = new StringBuilder();
        try {
            //
            String data = "";

            if (params.size()>0) {
                System.out.println(params.get(0)+":"+values.get(0));
                data = URLEncoder.encode(params.get(0), "UTF-8") + "=" + URLEncoder.encode(values.get(0), "UTF-8");
            }
            for(int i=1;i<values.size();i++)
            {    data += "&" + URLEncoder.encode(params.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
                //   url+="&"+params.get(i)+"="+values.get(i);
                System.out.println(params.get(i)+":"+values.get(i));
            }
            System.out.println(url);
            URL Url = new URL(url);
            URLConnection conn = Url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
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
        System.out.println(result);
        delegate.processFinish(result);
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }
}
