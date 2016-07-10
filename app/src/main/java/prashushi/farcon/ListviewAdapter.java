package prashushi.farcon;

/**
 * Created by Dell User on 7/9/2016.
 */
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;
        import java.util.ArrayList;

//for drawerList

public class ListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> price, size;
    TextView tv_size, tv_price;
    public ListViewAdapter(Context context, ArrayList<String> price, ArrayList<String> size) {
        super(context, R.layout.listview_package, new String[]{});
        this.context = context;
        this.price = price;
        this.size = size;
    }

    @Override
    public void add(String object) {
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.price.size();
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_package, parent, false);
        tv_price=(TextView)rowView.findViewById(R.id.tv_list_price);
        tv_price.setText(price.get(position)+context.getResources().getString(R.string.Rs));
        Double size_d=Double.valueOf(size.get(position));
        tv_size=(TextView)rowView.findViewById(R.id.tv_list_size);
        tv_size.setText(getSizeTag(size_d));
        return rowView;
    }

    private String getSizeTag(Double size_d) {
        String tag="Kg";
        if(size_d<1.0)
        {
            size_d*=1000;
            tag="grams";
        }
        return size_d+" "+tag;
    }

}

