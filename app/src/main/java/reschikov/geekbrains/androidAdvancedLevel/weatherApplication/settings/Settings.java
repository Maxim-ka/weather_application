//package reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import android.view.MenuItem;
//
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
//
//public class Settings extends AppCompatActivity {
//
//    private ActionBar actionBar;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.content_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        if (actionBar != null){
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        String action;
//        if ((action = getIntent().getAction()) != null) fragmentLaunch(action);
//    }
//
//    private void fragmentLaunch(String action){
//        switch (action){
//            case Rules.ACTION_SETTING_PREFERENCE:
//                actionBar.setTitle(R.string.preference);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(getIdFrameLayout(), new PreferredSettings())
//                        .commit();
//                break;
//        }
//    }
//
//    private int getIdFrameLayout(){
//        if (findViewById(R.id.frame_detail) != null) return R.id.frame_detail;
//        return R.id.frame_master;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                returnMainActivity();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        returnMainActivity();
//    }
//
//    private void returnMainActivity(){
//        this.finish();
//    }
//}
