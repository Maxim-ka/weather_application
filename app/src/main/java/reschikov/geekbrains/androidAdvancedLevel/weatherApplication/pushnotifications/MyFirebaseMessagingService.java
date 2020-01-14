//package reschikov.geekbrains.androidadvancedlevel.weatherapplication.pushnotifications;
//
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.SharedPreferences;
//import androidx.core.app.NotificationCompat;
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
//
//import static android.content.ContentValues.TAG;
//import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
//import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private String token;
//    private int messageId;
//
//
//    @Override
//    public void onNewToken(String s) {
//        if (FirebaseMessaging.getInstance().isAutoInitEnabled()){
//            FirebaseInstanceId.getInstance().getInstanceId()
//                    .addOnCompleteListener(task -> {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//                        if (task.getResult() != null){
//                            token = task.getResult().getToken();
//                            SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("token", token);
//                            editor.apply();
//                        }
//                    });
//        }
//    }
//
////    private void sendRegistrationToServer (String refreshedToken){
////        if (refreshedToken != null){
//            //отправка индификатора на собственный сервер
////        }
////    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (remoteMessage.getNotification() != null)
//            if ("key weather".equals(remoteMessage.getNotification().getTitle())){
//                SharedPreferences sp = getDefaultSharedPreferences(getBaseContext());
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString(Rules.KEY_APIUX, remoteMessage.getNotification().getBody());
//                editor.apply();
//                return;
//            }
//            makeNote(getBaseContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//    }
//
//    private void makeNote (Context context, String title, String message) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
//                .setSmallIcon(R.drawable.ic_stat_notifications)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(PRIORITY_MAX);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(messageId++, builder.build());
//    }
//}
