package com.example.finalproject1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MyReceiverSMS extends BroadcastReceiver { //sending SMS to the person who typed you
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String num = messages[i].getOriginatingAddress();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num, null, "Hi, stop sending me SMS, I am playing MonsterClicker," +
                            " if you want to play too, download it in the playstore -MonsterClicker-", null, null);
                }
            }
        }
    }
}
