package reschikov.geekbrains.androidadvancedlevel.weatherapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseService;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.Admonition;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.FragmentChoiceOfAction;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.GoogleMessage;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch.Sender;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.UserAvatar;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply.FragmentPager;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.sensors.FragmentOutputSensors;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings.FragmentOfListOfCities;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings.Settings;
import static android.content.Intent.EXTRA_LOCAL_ONLY;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_COORDINATES_NOT_DEFINED_ANDROID;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_ERROR_SERVER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_ERROR_SETTING;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_LOAD_FROM_DB;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_NOT_CONNECTED;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_NO_PERMISSION;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_RECORD_DB;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_TIME_IS_OVER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_WEATHER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_WRITE_LETTER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.KEY_USER_IMAGE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.KEY_USER_NAME;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.KEY_WEATHER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.TAG_PROGRESS_ACTION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener, Situateable {

    private static final int REQUEST_IMAGE_GET = 1;
    private static final int REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE = 2;
    private static final int TIME_EXIT = 3_000;
    private static final String PREFIX_FILE = "file";
    private LocalBroadcastManager lbm;
    private MyReceiver bcrNetwork;
    private long pushingTime;
    private ImageView imageView;
    private TextView textView;
    private EditText editText;
    private DrawerLayout drawer;
    private boolean isMasterDetail;
    private boolean isNoPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(getBaseContext(), R.xml.setting, false);

        isMasterDetail = (findViewById(R.id.frame_detail) != null);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.int_navigation_drawer_open, R.string.int_navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.used_cities).setVisible(!isMasterDetail);
        navigationView.setNavigationItemSelectedListener(this);

        lbm = LocalBroadcastManager.getInstance(getBaseContext());
        bcrNetwork = new MyReceiver();

        if (savedInstanceState == null){
            drawer.openDrawer(GravityCompat.START);
            Intent load = new Intent(getBaseContext(), DatabaseService.class);
            load.setAction(Rules.LOADING);
            startService(load);
        }

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext()) == SUCCESS)FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        imageView = navigationView.getHeaderView(0).findViewById(R.id.imageUser);
        imageView.setOnLongClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else getImage();
            return true;
        });

        editText = navigationView.getHeaderView(0).findViewById(R.id.userNameEdit);
        textView = navigationView.getHeaderView(0).findViewById(R.id.userName);
        textView.setOnLongClickListener(this);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        textView.setText(preferences.getString(KEY_USER_NAME, getString(R.string.user_name)));
        String pathUserImage = preferences.getString(Rules.KEY_USER_IMAGE, null);
        if (pathUserImage != null) loadSavedUserImage(pathUserImage);
        else Picasso.get().load(R.drawable.avatar).fit().centerCrop().into(imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_NOT_CONNECTED));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_ERROR_SERVER));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_COORDINATES_NOT_DEFINED_ANDROID));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_ERROR_SETTING));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_NO_PERMISSION));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_TIME_IS_OVER));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_WEATHER));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_RECORD_DB));
        lbm.registerReceiver(bcrNetwork, new IntentFilter(ACTION_LOAD_FROM_DB));
    }

    @Override
    protected void onPause() {
        super.onPause();
        lbm.unregisterReceiver(bcrNetwork);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentOfOutputOfProgressAction fopa = (FragmentOfOutputOfProgressAction) fm.findFragmentByTag(TAG_PROGRESS_ACTION);
        if (fopa != null && fopa.isVisible()){
            fopa.breakIn();
            return;
        }
        if (fm.getBackStackEntryCount() < 1){
            long currentTime = System.currentTimeMillis();
            if (pushingTime == 0 || currentTime - pushingTime > TIME_EXIT) {
                pushingTime = currentTime;
                Toast.makeText(getBaseContext(), getString(R.string.go_out), Toast.LENGTH_LONG).show();
                return;
            }
            finish();
        }
        if (!isMasterDetail){
            Fragment fragment = fm.findFragmentByTag(Rules.TAG_FRAGMENT_PAGER);
            if (fragment != null) fm.beginTransaction().remove(fragment).commit();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_PRESSURE) == null &&
            sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null &&
            sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) == null) menu.findItem(R.id.sensors).setVisible(false);
        menu.findItem(R.id.select_city).setVisible(!isMasterDetail);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentOfOutputOfProgressAction fopa = (FragmentOfOutputOfProgressAction) fm.findFragmentByTag(TAG_PROGRESS_ACTION);
        if (fopa != null && fopa.isVisible()) fopa.breakIn();
        switch (item.getItemId()) {
            case R.id.locate:
                int stateGooglePlayServices = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
                if (stateGooglePlayServices == SUCCESS) fm.beginTransaction()
                    .replace(getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(null, false), Rules.TAG_PROGRESS_ACTION)
                    .commit();
                else GoogleMessage.newInstance(stateGooglePlayServices).show(fm, Rules.KEY_NO_GOOGLE);
                return true;
            case R.id.select_city:
                fm.beginTransaction().replace(R.id.frame_master, new FragmentOfListOfCities())
                    .addToBackStack(Rules.TAG_FRAGMENT_OF_LIST_OF_CITIES)
                    .commit();
                return true;
            case R.id.sensors:
                fm.beginTransaction().replace(getIdFrameLayout(), new FragmentOutputSensors())
                    .addToBackStack(Rules.TAG_FRAGMENT_OUTPUT_SENSORS)
                    .commit();
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.used_cities:
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_master, new FragmentOfListOfCities())
                    .addToBackStack(Rules.TAG_FRAGMENT_OF_LIST_OF_CITIES)
                    .commit();
                return true;
            case R.id.settings:
                intent = new Intent(getBaseContext(), Settings.class);
                intent.setAction(Rules.ACTION_SETTING_PREFERENCE);
                startActivity(intent);
                return true;
            case R.id.feedback:
                intent = new Intent(getBaseContext(), Sender.class);
                intent.setAction(ACTION_WRITE_LETTER);
                startActivity(intent);
                return true;
            case R.id.about_developer:
                Admonition.newInstance(getString(R.string.about_me), getString(R.string.title_me), null).show(getSupportFragmentManager(), Rules.TAG_TITLE_ME);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case Rules.REQUEST_CHECK_SETTINGS:
                String city = null;
                if (resultCode == RESULT_CANCELED) city = Rules.KEY_NO_GOOGLE;
                getSupportFragmentManager().beginTransaction()
                    .replace(getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(city, false), Rules.TAG_PROGRESS_ACTION)
                    .commitAllowingStateLoss();
                break;
            case REQUEST_IMAGE_GET:
                if (resultCode == RESULT_OK && data != null){
                    Uri uri = data.getData();
                    if (uri != null){
                        String imagePath = null;
                        if (getString(R.string.str_content).equals(uri.getScheme())){
                            String[] projection = new String[]{MediaStore.Images.Media.DATA};
                            try (Cursor cursor = getContentResolver().query( uri, projection,null, null, null)) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    int numberIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                                    imagePath = cursor.getString(numberIndex);
                                }
                            }
                        } else imagePath = uri.toString();
                        if (imagePath != null) saveUserImage(imagePath);
                    }
                }
                break;
        }
    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(getString(R.string.type_image));
        intent.putExtra(EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Rules.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
                if (permissions.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    getSupportFragmentManager().beginTransaction()
                        .replace(getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(Rules.KEY_NO_GOOGLE,false), TAG_PROGRESS_ACTION)
                        .commitAllowingStateLoss();
                } else isNoPermission = true;
                break;
            case REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getImage();
                }
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isNoPermission){
            Admonition.newInstance(getString(R.string.disabling_location), getString(R.string.no_permission_determine_location), null)
                    .show(getSupportFragmentManager(), Rules.TAG_DISABLING_LOCATION);
            isNoPermission = false;
        }
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Rules.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public boolean onLongClick(View v) {
        editText.setText(textView.getText());
        textView.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        editText.setInputType(TYPE_CLASS_TEXT);
        editText.setCursorVisible(true);
        editText.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) setUserName();
        });
        editText.setOnEditorActionListener((v12, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                setUserName();
                return true;
            }
            return false;
        });
        return true;
    }

    private void setUserName(){
        textView.setText(editText.getText());
        editText.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Rules.KEY_USER_NAME,textView.getText().toString());
        editor.apply();
    }

    private void loadSavedUserImage(String path){
        if (path.startsWith(PREFIX_FILE)) Picasso.get().load(path)
            .transform(new UserAvatar()).fit().centerCrop().error(R.drawable.avatar).into(imageView);
        else Picasso.get().load(new File(path)).transform(new UserAvatar()).fit().centerCrop()
            .error(R.drawable.avatar).into(imageView);
    }

    private void saveUserImage(String path){
        loadSavedUserImage(path);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_IMAGE, path);
        editor.apply();
    }

    @Override
    public int getIdFrameLayout(){
        if (isMasterDetail) return R.id.frame_detail;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.getTitle() != null &&
                !actionBar.getTitle().toString().equals(getString(R.string.app_name)))
            actionBar.setTitle(getString(R.string.app_name));
        return R.id.frame_master;
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                FragmentManager fm = getSupportFragmentManager();
                switch (action) {
                    case ACTION_LOAD_FROM_DB:
                        ArrayList<Location> cities = intent.getParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES);
                        if (cities == null) new FragmentChoiceOfAction().show(fm, Rules.TAG_CHOICE);
                        else if (cities.get(0).isShowingCity()){
                            fm.beginTransaction()
                                .replace(getIdFrameLayout(),
                                    FragmentOfOutputOfProgressAction.newInstance(cities.get(0).getLat() + "," + cities.get(0).getLon(), false), TAG_PROGRESS_ACTION)
                                .commit();
                            if (isMasterDetail) fm.beginTransaction()
                                .replace(R.id.frame_master, FragmentOfListOfCities.newInstance(cities)).commit();
                        } else {
                            fm.beginTransaction()
                                .replace(R.id.frame_master, FragmentOfListOfCities.newInstance(cities)).commit();
                        }
                        break;
                    case ACTION_NOT_CONNECTED:
                        Admonition.newInstance(getString(R.string.no_network), Rules.ATTENTION, null).show(fm, Rules.TAG_NO_NETWORK);
                        break;
                    case ACTION_ERROR_SERVER:
                        String error = intent.getStringExtra(Rules.KEY_ERR_SERVER);
                        Admonition.newInstance(error, Rules.SERVER_MESSAGE, null).show(fm, Rules.TAG_ERR_SERVER);
                        break;
                    case ACTION_COORDINATES_NOT_DEFINED_ANDROID:
                        float accuracy = intent.getFloatExtra(Rules.KEY_ACCURACY, 0.0f);
                        Admonition.newInstance(getString(R.string.unable_determine_coordinates, accuracy), Rules.WARNING, null).show(fm, Rules.TAG_NOT_DEFINED);
                        break;
                    case ACTION_ERROR_SETTING:
                        PendingIntent pendingIntent = intent.getParcelableExtra(Rules.KEY_ERROR_SETTING);
                        try {
                            startIntentSenderForResult(pendingIntent.getIntentSender(), Rules.REQUEST_CHECK_SETTINGS,null,0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("ERROR SETTING", e.getMessage());
                        }
                        break;
                    case ACTION_NO_PERMISSION:
                        requestPermissions();
                        break;
                    case ACTION_TIME_IS_OVER:
                        Admonition.newInstance(getString(R.string.Unable_determine_Check_network), Rules.ATTENTION, null).show(fm, Rules.TAG_UNABLE_DETERMINE_LOCATION);
                        break;
                    case ACTION_WEATHER:
                        ServerResponse sr = intent.getParcelableExtra(KEY_WEATHER);
                        if (sr != null){
                            fm.beginTransaction()
                                .replace(getIdFrameLayout(), FragmentPager.newInstance(sr), Rules.TAG_FRAGMENT_PAGER)
                                .commit();
                        }
                        break;
                    case ACTION_RECORD_DB:
                        boolean recorded = intent.getBooleanExtra(Rules.KEY_RECORD, false);
                        String msg = (recorded) ? getString(R.string.saved) : getString(R.string.not_saved);
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }
}
