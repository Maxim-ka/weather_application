package reschikov.geekbrains.androidadvancedlevel.weatherapplication.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch.GMailSender;

import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_EMAIL_NOT_SENT;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_EMAIL_SENT;

public class SenderService extends IntentService {

    private static final char CH_DOG = '@';

    public SenderService() {
        super("sendEmail");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!connectionCheck()) {
            Intent error = new Intent(Rules.ACTION_NOT_CONNECTED);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(error);
            return;
        }
        String senderEmail = intent.getStringExtra(Rules.KEY_FROM);
        String password = intent.getStringExtra(Rules.KEY_PASSWORD);
        String recipient = intent.getStringExtra(Rules.KEY_RECIPIENT);
        String topic = intent.getStringExtra(Rules.KEY_TOPIC);
        ArrayList<String> messages = intent.getStringArrayListExtra(Rules.KEY_MESSAGES);

        String login = senderEmail.substring(0, senderEmail.indexOf(CH_DOG));
        String host = senderEmail.substring(senderEmail.indexOf(CH_DOG) + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            sb.append(messages.get(i)).append("\n\n");
        }
        try {
            GMailSender sender = new GMailSender(host, login, password);
            sender.sendMail(topic, sb.toString(), senderEmail, recipient);
        } catch (Exception e) {
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(ACTION_EMAIL_NOT_SENT));
            Log.e("SendMail", e.getMessage(), e);
            return;
        }
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(ACTION_EMAIL_SENT));
    }

    private boolean connectionCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
