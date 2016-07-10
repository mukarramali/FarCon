package prashushi.farcon;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Dell User on 7/9/2016.
 */
public class ListPackageDialog extends DialogFragment{


    ArrayList<String> price, size, id;
    int pos;
    String[] oId, oPrice, oSize;

    public static ListPackageDialog newInstance(ArrayList<String> id, ArrayList<String> size, ArrayList<String> price, int pos, String[] oId, String[] oPrice, String[] oSize) {
        ListPackageDialog dialog = new ListPackageDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("size", size);
        args.putStringArrayList("price", price);
        args.putStringArrayList("id", id);
        args.putInt("pos", pos);
        args.putStringArray("oId", oId);
        args.putStringArray("oPrice", oId);
        args.putStringArray("oSize", oId);

        dialog.setArguments(args);

//        nListener=delegate;
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_list_package, container,
                true);

        getDialog().setTitle(R.string.choose_pack);

        return v;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_package, null);

        price=getArguments().getStringArrayList("price");
        size=getArguments().getStringArrayList("size");
        id=getArguments().getStringArrayList("id");
        pos=getArguments().getInt("pos");
        oId=getArguments().getStringArray("oId");
        oPrice=getArguments().getStringArray("oPrice");
        oSize=getArguments().getStringArray("oSize");
        ListView listView= (ListView) v.findViewById(R.id.listview_pakage);
        ListViewAdapter adapter=new ListViewAdapter(getActivity(), price, size);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id1) {
              //  mListener.onComplete("","","", 0 );
                oId[pos]=id.get(position);
                oPrice[pos]=price.get(position);
                oSize[pos]=size.get(position);
                mListener.onComplete(id.get(position),size.get(position),price.get(position), pos );
                dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_pack).setView(v);

        return builder.create();
    }

    public interface OnCompleteListener {
         void onComplete(String id, String size, String price, int pos);
    }

    OnCompleteListener mListener;



    // make sure the Activity implemented it
   @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

}