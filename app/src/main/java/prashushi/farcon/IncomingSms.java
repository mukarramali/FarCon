package prashushi.farcon;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("Incoming SMS");
        final Bundle bundle = intent.getExtras();
        String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
        if (intent.getAction() .compareTo( SMS_RECEIVED)==0)
        try {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])                                                                                                    pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();
                    try
                    {
                        if (senderNum .contains("MM-FarCon"))
                        {

                            OTPActivity Sms = new OTPActivity();
                            Sms.recivedSms(message );
                        }
                    }
                    catch(Exception e){}

                }
            }

        } catch (Exception e)
        {

        }
    }

}