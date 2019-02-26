package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.GoogleApiAvailability;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;

import static com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED;
import static com.google.android.gms.common.ConnectionResult.SERVICE_INVALID;
import static com.google.android.gms.common.ConnectionResult.SERVICE_MISSING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;

public class GoogleMessage extends DialogFragment {

    public static GoogleMessage newInstance(int state){
        GoogleMessage fragment = new GoogleMessage();
        Bundle args = new Bundle();
        args.putInt(Rules.GOOGLE_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (getArguments() != null){
                int state = getArguments().getInt(Rules.GOOGLE_STATE);
                builder.setTitle(Rules.GOOGLE_STATE);
                builder.setMessage(showMessageGoogleState(state));
                builder.setPositiveButton(R.string.ok, (DialogInterface dialog, int id) ->{
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_master, FragmentOfOutputOfProgressAction.newInstance(Rules.KEY_NO_GOOGLE, false), Rules.TAG_PROGRESS_ACTION)
                            .commit();
                    dialog.dismiss();
                });
                builder.setNegativeButton(getString(R.string.cancel), (DialogInterface dialog, int id) ->{
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), state, Rules.GOOGLE_PLAY_DEAD).show();
                    dialog.dismiss();
                });
            }
            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }

    private String showMessageGoogleState(int state){
        switch (state){
            case SERVICE_MISSING:
                return getString(R.string.no_Google_service);
            case SERVICE_UPDATING:
                return getString(R.string.update_Google_service);
            case SERVICE_VERSION_UPDATE_REQUIRED:
                return getString(R.string.update_version_Google);
            case SERVICE_DISABLED:
                return getString(R.string.Google_disabled);
            case SERVICE_INVALID:
                return getString(R.string.Google_service_error);
            default:
                return getString(R.string.Crash_service_google);
        }
    }
}
