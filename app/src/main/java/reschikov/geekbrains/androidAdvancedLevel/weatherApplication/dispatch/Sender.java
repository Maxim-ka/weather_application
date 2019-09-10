package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.Admonition;

public class Sender extends AppCompatActivity implements FragmentShippingControl.Transferable {

    private Toolbar toolbar;
    private Sending sending;
    private LocalBroadcastManager lbm;
    private MyReceiver bcrSender;
    private boolean isCanSent;

    public void setSending(Sending sending) {
        this.sending = sending;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null){
            Intent intent = getIntent();
            getAction(intent);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> sending.sendMessage());

        lbm = LocalBroadcastManager.getInstance(getBaseContext());
        bcrSender = new MyReceiver();
    }

    private void getAction(Intent intent){
        String action = intent.getAction();
        if (action == null) finish();
        ArrayList<String> listMessage = new ArrayList<>();
        switch (intent.getAction()){
            case Rules.ANDROID_INTENT_ACTION_SEND:
                if (!intent.hasExtra(Intent.EXTRA_TEXT) || intent.hasExtra(Intent.EXTRA_STREAM)){
                    startNextMatchingActivity(intent);
                    finish();
                } else {
                    String string = intent.getStringExtra(Intent.EXTRA_TEXT);
                    listMessage.add(string);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_master, FragmentShippingControl.newInstance(listMessage))
                            .commit();
                }
                break;
            case Rules.ACTION_WRITE_LETTER:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_master, FragmentShippingControl.newInstance(listMessage))
                        .commit();
                break;
            default:
                finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Rules.MY_PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCanSent = true;
                }
            break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isCanSent){
            sending.send();
            isCanSent = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == Rules.REQUEST_SELECT_PHONE_NUMBER) {
            if (data != null){
                Uri contactUri = data.getData();
                if (contactUri != null){
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                    try (Cursor cursor = getContentResolver().query(contactUri, projection,null, null, null)){
                        if (cursor != null && cursor.moveToFirst()) {
                            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String number = cursor.getString(numberIndex);
                            sending.getPhone(number);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) toolbar.setTitle(savedInstanceState.getString(Rules.KEY_TITLE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Rules.KEY_TITLE, toolbar.getTitle().toString());
    }

    @Override
    public void changeTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onResume() {
        super.onResume();
        lbm.registerReceiver(bcrSender, new IntentFilter(Rules.ACTION_NOT_CONNECTED));
        lbm.registerReceiver(bcrSender, new IntentFilter(Rules.ACTION_EMAIL_NOT_SENT));
        lbm.registerReceiver(bcrSender, new IntentFilter(Rules.ACTION_EMAIL_SENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        lbm.unregisterReceiver(bcrSender);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Rules.ACTION_NOT_CONNECTED:
                        Admonition.newInstance(getString(R.string.no_network), Rules.ATTENTION, null).show(getSupportFragmentManager(), Rules.TAG_NO_NETWORK);
                        break;
                    case Rules.ACTION_EMAIL_NOT_SENT:
                        Toast.makeText(getBaseContext(), getString(R.string.email_not_sent), Toast.LENGTH_LONG).show();
                        break;
                    case Rules.ACTION_EMAIL_SENT:
                        Toast.makeText(getBaseContext(), getString(R.string.email_sent), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        }
    }
}
