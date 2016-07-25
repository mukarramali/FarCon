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

         System.out.println("in utilities");
         if (output.contains("access") || output.contains("invalid") || output.contains("error")) {
            Toast.makeText(context, context.getString(R.string.login_again), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            //finish();
             System.out.println("here out");
        }
        else
            Toast.makeText(context, context.getString(R.string.try_again), Toast.LENGTH_LONG).show();

    }

}
