package reschikov.geekbrains.androidadvancedlevel.weatherapplication;

public class Rules {

    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    public static final int REQUEST_SELECT_PHONE_NUMBER = 1;
    public static final int REQUEST_CHECK_SETTINGS = 3;
    public static final int GOOGLE_PLAY_DEAD = 5;
    public static final int GOOGLE_COORDINATE_REQUEST = 1;
    public static final int REQUEST_DETERMINE_COORDINATES_ANDROID = 2;
    public static final int REQUEST_CITY = 3;
    public static final int FAILURE = -5;
    public static final int TIME_START = 0;
    public static final int TIME_FINISH = 1;
    public static final int SHOW_LOCATION = 5;
    public static final int PROGRESS = 100;
    public static final int BREAKING = -1;
    public static final int COORDINATE_DETERMINATION_REQUEST = 4;
    public static final int DEFNIITION = 25;
    public static final int ZERO = 0;
    public static final int CLARIFICATION = 10;

    public static final long IN_MILLISEC = 1_000L;
    public static final int THREE_HOURS = 10_800_000;
    public static final int SEARCH_TIME = 60_000;
    public static final int MAX_SINGLE_BYTE_CHAR = 127;
    public static final int MAX_LEN_SMS_EN = 140;
    public static final int MAX_LEN_SMS_RU = 70;

    public static final float FROM_MBAR_IN_MMHG = 0.750064f;
    public static final float KMH_IN_MC = 0.277777777777778f;
    public static final float MIN_ACCURACY = 200.0f;

    public static final String LOADING = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.LOADING";
    public static final String RECORD = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.RECORD";
    public static final String GET_ALL_CITIES = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.GET_ALL_CITIES";
    public static final String CLEAR = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.CLEAR";
    public static final String REMOVE = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.REMOVE";
    public static final String ACTION_WEATHER = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_WEATHER";
    public static final String GET_WEATHER_DB = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.GET_WEATHER_DB";
    public static final String ACTION_ANSWER_DB = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_ANSWER_DB";
    public static final String ACTION_RECORD_DB ="reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_RECORD_DB";
    public static final String ACTION_LOAD_FROM_DB = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_LOAD_FROM_DB";

    public static final String ACTION_NOT_CONNECTED = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_NOT_CONNECTED";
    public static final String ACTION_ERROR_SERVER = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_ERROR_SERVER";
    public static final String ACTION_ERROR_SETTING = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ERROR_SETTING";
    public static final String ACTION_COORDINATES_NOT_DEFINED_ANDROID = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_COORDINATES_NOT_DEFINED_ANDROID";
    public static final String ACTION_NO_PERMISSION = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.NO_PERMISSION";
    public static final String ACTION_SETTING_PREFERENCE = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_SETTING_PREFERENCE";
    public static final String ACTION_TIME_IS_OVER = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.TIME_IS_OVER";

    public static final String ANDROID_INTENT_ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_WRITE_LETTER = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_WRITE_LETTER";
    public static final String ACTION_EMAIL_NOT_SENT = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_EMAIL_NOT_SENT";
    public static final String ACTION_EMAIL_SENT = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_EMAIL_SENT";
    public static final String ACTION_SMS = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_SMS";
    public static final String ACTION_SENT_SMS = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_SENT_SMS";
    public static final String ACTION_DELIVERED_SMS = "reschikov.geekbrains.androidadvancedlevel.weatherapplication.ACTION_DELIVERED_SMS";

    public static final String ATTENTION = "Attention!";
    public static final String SERVER_MESSAGE = "Server message";
    public static final String WARNING = "Warning!";
    public static final String GOOGLE_STATE = "google state";
    public static final String MESSAGE = "message";
    public static final String TITLE = "title";
    public static final String INTENT = "intent";

    public static final String TAG_PROGRESS_ACTION = "Tag ProgressAction";
    public static final String TAG_TITLE_ME = "Tag title me";
    public static final String TAG_DISABLING_LOCATION = "Tag disabling location";
    public static final String TAG_CHOICE = "Tag choice";
    public static final String TAG_NO_NETWORK = "Tag no network";
    public static final String TAG_NOT_DEFINED = "Tag not defined";
    public static final String TAG_ENTER_PLACE = "Tag enter name city";
    public static final String TAG_DELETE = "Tag delete";
    public static final String TAG_ERR_SERVER = "Tag err server";
    public static final String TAG_SEND_SMS = "Tag send sms";
    public static final String TAG_UNABLE_DETERMINE_LOCATION = "Tag unable determine location";
    public static final String TAG_FRAGMENT_PAGER = "TAG FragmentPager";
    public static final String TAG_LIMIT_IS_EXCEEDED = "Tag limit is exceeded";
    public static final String TAG_FRAGMENT_OUTPUT_SENSORS = "Tag FragmentOutputSensors";
    public static final String TAG_CHOICE_PLACE = "TAG choice place";
    public static final String TAG_FRAGMENT_OF_LIST_OF_CITIES = "Tag FragmentOfListOfCities";

    public static final String KEY_LIST_OF_CITIES = "key list of cities";
    public static final String KEY_NO_GOOGLE = "key no google";
    public static final String KEY_WEATHER = "key weather";
    public static final String KEY_USER_NAME = "key user name";
    public static final String KEY_USER_IMAGE = "key image user";
    public static final String KEY_ERR_SERVER = "key err server";
    public static final String KEY_ACCURACY = "key accuracy";
    public static final String KEY_ERROR_SETTING = "key error setting";
    public static final String KEY_RECORD = "key record";
    public static final String KEY_PATH_IMAGE = "key path image";
    public static final String KEY_MESSAGES = "key messages";
    public static final String KEY_RECIPIENT = "key recipient";
    public static final String KEY_LANG = "key lang";
    public static final String KEY_APIUX = "key APIUX";
    public static final String KEY_SELECTED = "key selected";
    public static final String KEY_FORECAST = "key forecast";
    public static final String KEY_PLACE = "key place";
    public static final String KEY_CURRENT = "key current";
    public static final String KEY_FROM = "key from";
    public static final String KEY_PASSWORD = "key password";
    public static final String KEY_TOPIC = "key topic";
    public static final String KEY_TITLE = "key title";
    public static final String KEY_IS_SMS = "key is sms";
    public static final String KEY_LETTER = "key letter";
    public static final String KEY_LOCK = "key lock";
    public static final String KEY_SET = "key set";
    public static final String KEY_LIST = "key list";
    public static final String KEY_MSG = "key msg";
    public static final String KEY_OPEN_CAGE = "key OpenCage";
    public static final String KEY_ON_GEOCODER = "key onGeoCoder";
    public static final String KEY_IS_GEO_CODER = "key isGeoCoder";
    public static final String KEY_RESULTS = "key results";
    public static final String KEY_NUMBER_OF_REQUESTS = "key number of requests";
    public static final String KEY_RESET_DATE = "key reset date";
    public static final String KEY_IS_DELETED = "key isDeleted";

    public static final String KEY = "key";
    public static final String Q = "q";

    public static final String RU = "ru";
    public static final String C = " \u2103";
    public static final String $_2F_$S = "%1$.2f %2$s";
    public static final String $D_ = "%1$d %%";
}
