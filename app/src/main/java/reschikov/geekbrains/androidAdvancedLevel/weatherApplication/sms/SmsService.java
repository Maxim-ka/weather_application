//package reschikov.geekbrains.androidadvancedlevel.weatherapplication.sms;
//
//import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.telephony.SmsManager;
//import java.util.ArrayList;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
//
//import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_SMS;
//
//public class SmsService extends IntentService {
//
//    public SmsService() {
//        super("sms");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        String action = intent.getAction();
//        if (action != null){
//            switch (action){
//                case ACTION_SMS:
//                    sendSms(intent.getStringArrayListExtra(Rules.KEY_MESSAGES),
//                            intent.getStringExtra(Rules.KEY_RECIPIENT));
//                    break;
//            }
//        }
//    }
//
//    private void sendSms(ArrayList<String> messages, String recipient){
//        SmsManager smsManager =  SmsManager.getDefault();
//        Intent sent = new Intent(Rules.ACTION_SENT_SMS);
//        sent.putExtra(Rules.KEY_RECIPIENT, recipient);
//        Intent delivered = new Intent(Rules.ACTION_DELIVERED_SMS);
//        delivered.putExtra(Rules.KEY_RECIPIENT, recipient);
//        PendingIntent sending = PendingIntent.getBroadcast(getBaseContext(),0, sent, 0);
//        PendingIntent reception = PendingIntent.getBroadcast(getBaseContext(), 0, delivered, 0);
//        for (int i = 0; i < messages.size(); i++) {
//            smsManager.sendTextMessage(recipient, null, messages.get(i), sending, reception);
//        }
//    }
//}
