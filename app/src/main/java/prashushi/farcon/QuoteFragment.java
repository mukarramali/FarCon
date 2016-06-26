package prashushi.farcon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Dell User on 6/20/2016.
 */
public class QuoteFragment extends Fragment implements View.OnClickListener {

    LinearLayout layoutContainer;
    public QuoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_quote, container, false);
        layoutContainer= (LinearLayout) view.findViewById(R.id.layout_container);
        view.findViewById(R.id.bt_add).setOnClickListener(this);


            return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_add:
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ///  LinearLayout hiddenLayout = (LinearLayout)findViewById(R.id.header);
                int count;
                View child=getLayoutInflater(null).inflate(R.layout.quote_card, null);
                child.setLayoutParams(params);
                count=layoutContainer.getChildCount();
                layoutContainer.addView(child,(count));

                break;
        }
    }
}
