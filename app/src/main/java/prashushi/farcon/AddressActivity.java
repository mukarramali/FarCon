package prashushi.farcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {


    EditText etName, etEmail, etPhone, etHouse, etStreet, etArea, etCity, etPin, etState, etMember;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mydb=new DBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try{
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }catch(Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.tv_order).setOnClickListener(this);
        sPref=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor=sPref.edit();
        initET();

    }

    private void initET() {
    etArea= (EditText) findViewById(R.id.et_area);
        etCity= (EditText) findViewById(R.id.et_city);
        etEmail= (EditText) findViewById(R.id.et_email);
        etHouse= (EditText) findViewById(R.id.et_house);
        etName= (EditText) findViewById(R.id.et_name);
        etPhone= (EditText) findViewById(R.id.et_phone);
        etPin= (EditText) findViewById(R.id.et_pin);
        etState= (EditText) findViewById(R.id.et_state);
        etStreet= (EditText) findViewById(R.id.et_street);
        etMember= (EditText) findViewById(R.id.et_member);

        etPhone.setText(sPref.getString("add_phone", ""));
        etName.setText(sPref.getString("add_name", ""));
        etEmail.setText(sPref.getString("add_email", ""));
        etMember.setText(sPref.getString("add_member", ""));
        etHouse.setText(sPref.getString("add_house", ""));
        etStreet.setText(sPref.getString("add_street", ""));
        etArea.setText(sPref.getString("add_area", ""));
        etCity.setText(sPref.getString("add_city", ""));
        etPin.setText(sPref.getString("add_pin", ""));
        etState.setText(sPref.getString("add_state", ""));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_order:

                String name, phone, email, member, house, street, area, city, pin, state;
                name=etName.getText().toString();
                phone=etPhone.getText().toString();
                email=etEmail.getText().toString();
                member=etMember.getText().toString();
                house=etHouse.getText().toString();
                street=etStreet.getText().toString();
                area=etArea.getText().toString();
                city=etCity.getText().toString();
                pin=etPin.getText().toString();
                state=etState.getText().toString();

                if(phone.length()!=10){
                    etPhone.setError("10 digits required");
                    etPhone.requestFocus();
                    break;
                }

                if(verify(name))
                {setErr(etName);break;}
                else
                removeErr(etName);

                if(!format(name)){
                    etName.setError("Only alphabets!");
                    etName.requestFocus();
                    return;
                }


                if(verify(phone))
                { setErr(etPhone);break;}
                else
                    removeErr(etPhone);
                if(verify(member))
                {   setErr(etMember);break;}
                else
                    removeErr(etMember);
                if(verify(house))
                {   setErr(etHouse);break;}
                else
                    removeErr(etHouse);
                if(verify(street))
                {   setErr(etStreet);break;}
                else
                    removeErr(etStreet);
                if(verify(area))
                {   setErr(etArea);break;}
                else
                    removeErr(etArea);
                if(verify(city))
                {   setErr(etCity);break;}
                else
                    removeErr(etCity);
                if(verify(pin))
                {   setErr(etPin);break;}
                else
                    removeErr(etPin);
                if(verify(state))
                {   setErr(etState);break;}
                else
                    removeErr(etState);


                editor.putString("add_name", name);
                editor.putString("add_phone",phone );
                editor.putString("add_email",email );
                editor.putString("add_member", member);
                editor.putString("add_house", house);
                editor.putString("add_street", street);
                editor.putString("add_area", area);
                editor.putString("add_city", city);
                editor.putString("add_pin", pin);
                editor.putString("add_state", state);


                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                String id=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("id", "0");
                params.add("id");
                values.add(id);

                System.out.println("id:"+id);

                params.add("name");
                values.add(name);

                params.add("email");
                values.add(email);
                params.add("phone_num");
                values.add(phone);
                params.add("house_no");
                values.add(house);
                params.add("street");
                values.add(street);
                params.add("area");
                values.add(area);
                params.add("city");
                values.add(city);
                params.add("state");
                values.add(state);
                params.add("pin");
                values.add(pin);
                params.add("no");
                values.add(member);

                String url=getString(R.string.local_host)+"order";

                new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.contains("truexxx")) {
                           String url2=getString(R.string.local_host)+"checkout/"+getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("id", "0");
                            new BackgroundTaskLoad(url2, AddressActivity.this,new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    if(!output.contains("falsexxx")) {
                                        Intent intent=new Intent(AddressActivity.this, ConfirmActivity.class);
                                        intent.putExtra("order_id", output);
                                        mydb.deleteDB();
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                        Toast.makeText(AddressActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
                                    }
                            }).execute();
                        }
                        else
                        Toast.makeText(AddressActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
                    }
                }).execute();
                break;

        }
    }

    private void removeErr(EditText et) {
        et.setError(null);
    }

    private boolean verify(String st) {
            return st.length()==0;
    }
    private void setErr(EditText et) {
        et.setError("Can't be left empty!");
        et.requestFocus();
        return;
    }
    private boolean format(String name) {

        for(int i=0;i<name.length();i++){
            if(name.charAt(i)<'A'&&name.charAt(i)!=' ')
                return false;
        }
        return true;
    }

}
