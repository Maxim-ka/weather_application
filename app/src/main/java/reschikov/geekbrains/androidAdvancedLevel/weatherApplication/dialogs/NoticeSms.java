package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.sms.SmsService;

public class NoticeSms extends DialogFragment {

    public static NoticeSms newInstance(String recipient, ArrayList<String> messages){
        NoticeSms fragment = new NoticeSms();
        Bundle args = new Bundle();
        args.putString(Rules.KEY_RECIPIENT, recipient);
        args.putStringArrayList(Rules.KEY_MESSAGES, messages);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null && getArguments() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String recipient = getArguments().getString(Rules.KEY_RECIPIENT);
            final ArrayList<String> messages = getArguments().getStringArrayList(Rules.KEY_MESSAGES);
            builder.setTitle(R.string.SMS_sending);
            if (messages != null) builder.setMessage(getString(R.string.send_SMS, messages.size()));
            builder.setPositiveButton(R.string.ok, (DialogInterface dialog, int id) ->{
                Intent intent = new Intent(getContext(), SmsService.class);
                intent.setAction(Rules.ACTION_SMS);
                intent.putExtra(Rules.KEY_RECIPIENT, recipient);
                intent.putExtra(Rules.KEY_MESSAGES, messages);
                getActivity().startService(intent);
                getActivity().finish();
            });
            builder.setNegativeButton(getString(R.string.cancel), (DialogInterface dialog, int id) -> dialog.dismiss());
            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
