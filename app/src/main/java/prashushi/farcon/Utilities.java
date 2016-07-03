package prashushi.farcon;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Dell User on 6/28/2016.
 */
public class Utilities {

     void checkIfLogged(String output, Context context) {
//         params.add("access_token");
  //       values.add(sPref.getString("access_token", "0"));

         if(output.contains("access_denied")||output.contains("invalid_request")){
            Toast.makeText(context, context.getString(R.string.login_again), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            //finish();

        }
        else
            Toast.makeText(context, context.getString(R.string.try_again), Toast.LENGTH_LONG).show();

    }

}
