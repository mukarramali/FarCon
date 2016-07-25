package prashushi.farcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Dell User on 7/18/2016.
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView im;
    boolean[] thread;
    public LruCache<String, Bitmap> mMemoryCache, generalCache;
    String sKey;

    ImagePool imagePool;

    public DownloadImage(ImageView pstr, boolean[] thread) {
        im = pstr;
        this.thread = thread;
        Log.e("00", "thread in DownloadImage:" + thread[0]);
    }


    public DownloadImage(ImageView pstr, boolean[] thread, LruCache<String, Bitmap> mMemoryCache, String s, Context context) {
        im = pstr;
        sKey = s;
        this.thread = thread;
        this.mMemoryCache = mMemoryCache;
        Log.e("00", "thread in DownloadImage:" + thread[0]);
        imagePool = new ImagePool(context);
    }

    @Override
    protected void onPreExecute() {


        if (imagePool != null && imagePool.getImage(sKey) != null) {

            try {
                System.out.println("Image set from memory!");
                addBitmapToMemoryCache(sKey, imagePool.getImage(sKey));
                if (im != null)
                    im.setImageBitmap(imagePool.getImage(sKey));
            } catch (Exception e) {

            }
            this.cancel(true);
        }
        Log.e("00", "thread in DownloadImage2:" + thread[0]);
        if (!thread[0]) {
            this.cancel(true);
            Log.e("1", "Background download cancelled!");
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap bitmap = null;
        try {
            if (!thread[0]) {
                Log.e("", "Background download cancelled!");
                this.cancel(true);
                return bitmap;
            }
            InputStream fin = new URL(url.replace(" ", "")).openStream();
            bitmap = BitmapFactory.decodeStream(fin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            addBitmapToMemoryCache(sKey, bitmap);
            if (imagePool != null)
                imagePool.saveImage(bitmap, sKey);
            if (im != null)
                im.setImageBitmap(bitmap);

        }
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        try {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        } catch (Exception e) {

        }

    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
