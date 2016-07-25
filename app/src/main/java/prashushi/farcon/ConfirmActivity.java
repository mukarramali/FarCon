package prashushi.farcon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell User on 6/21/2016.
 */
public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        }catch(Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");
        TextView tv1= (TextView) findViewById(R.id.tv1);
        try {
            String order_id = "\nYour Order Id " + getIntent().getExtras().getString("order_id", "#0");
            String cost = "\nPlease pay " + getIntent().getExtras().getString("cost", "#") + " on delivery!";
            tv1.setText(getString(R.string.confirm_order) + order_id.replace("\\", "") + cost);
        }catch (Exception e){
            e.printStackTrace();
        }
        findViewById(R.id.im1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   doRotations(getViews(new int[]{R.id.l1, R.id.l2, R.id.l3, R.id.l4, R.id.ch1, R.id.ch2, R.id.ch3}), 0, 360, 1000);
                createAnimation();
                System.out.println("Animation started!");
            }
        });
        createAnimation();
    }

    private void createAnimation() {
        Animation zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        Animation alphain = AnimationUtils.loadAnimation(this, R.anim.alpha_in);

        ImageView basket = (ImageView) findViewById(R.id.shoot_basket);
        basket.startAnimation(zoomin);
        ImageView tick = (ImageView) findViewById(R.id.im1);
        tick.startAnimation(alphain);
        TextView tv = (TextView) findViewById(R.id.tv1);
        tv.startAnimation(alphain);

    }


    public ArrayList<View> getViews(int[] ids) {
        ArrayList<View> array = new ArrayList<>();
        for (int id : ids) {
            array.add(findViewById(id));
        }
        return array;
    }

    public void doRotations(ArrayList<View> views, int start, int end, int duration) {

        for (int i = 0; i < views.size(); i++) {
            RotateAnimation temp = new RotateAnimation(start, end, .5f, .5f);
            temp.setDuration(duration);
            temp.setFillAfter(false);

            Animation down = AnimationUtils.loadAnimation(this, R.anim.drop);

            AnimationSet set = new AnimationSet(this, null);
            set.addAnimation(temp);
            set.addAnimation(down);
            views.get(i).startAnimation(set);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
