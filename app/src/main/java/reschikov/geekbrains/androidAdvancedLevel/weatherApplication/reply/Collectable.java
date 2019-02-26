package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;

interface Collectable {

    String collectData(Context context, ServerResponse response);
}
