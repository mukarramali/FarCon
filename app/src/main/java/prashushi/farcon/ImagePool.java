package prashushi.farcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dell User on 7/20/2016.
 */
public class ImagePool {

    String path = Environment.getExternalStorageDirectory() + "/farcon/";
    String key;
    Bitmap bitmap;
    Context context;

    ImagePool(Context context) {
        this.bitmap = null;
        this.key = "";
        this.context = context;
    }

    void saveImage(Bitmap bitmap, String keys) {
        this.bitmap = bitmap;
        this.key = keys;
        String[] ss = keys.split("/");
        this.key = ss[ss.length - 1];
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(key, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bmp is your Bitmap instance
            System.out.println("Image pool 1");
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Bitmap getImage(String keys) {
        this.key = keys;
        String[] ss = keys.split("/");
        this.key = ss[ss.length - 1];
        FileInputStream inputStream = null;
        try {
            inputStream = context.openFileInput(key);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
