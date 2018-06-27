package com.example.sardor.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsNotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        String phoneNumber="";
        String message="";
        Toast.makeText(context, "SMS RECEIVED", Toast.LENGTH_SHORT).show();
        try{
            final Object[] pduObj = (Object[])bundle.get("pdus");
            for(int i=0; i<pduObj.length; i++){
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])pduObj[i]);
                phoneNumber = currentMessage.getDisplayOriginatingAddress();
                message = currentMessage.getMessageBody();
            }
            Toast.makeText(context, phoneNumber, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
            nBuilder.setContentTitle("MEE"+phoneNumber);
            nBuilder.setContentText("BBEE"+message);

            /*Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            nBuilder.setContentIntent(contentIntent);*/

            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, nBuilder.build());
        }
        catch(Exception e){
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }

    }
}
