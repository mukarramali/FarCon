package prashushi.farcon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Dell User on 7/17/2016.
 */
public class DialogUpdate extends Activity implements View.OnClickListener {
    int k = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setFinishOnTouchOutside(false);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        k = getIntent().getExtras().getInt("case", 1);
        TextView msg = (TextView) findViewById(R.id.tv_msg);
        TextView tv_send = (TextView) findViewById(R.id.tv_send);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);

        switch (k) {
            case 2:
                msg.setText(getString(R.string.update2));
                tv_cancel.setText("Later");
                break;
            case 3:
                msg.setText(getString(R.string.update3));
                tv_cancel.setText("Exit");
                break;
            default:
                finish();
                break;
        }
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_send:
                String url = "http://www.farcon.in/download";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.tv_cancel:
                if (k == 2) {
                    finish();
                } else if (k == 3) {
                    // finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else
                        ActivityCompat.finishAffinity(this);

                    // System.exit(0);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (k == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else
                ActivityCompat.finishAffinity(this);

        }
    }
}
