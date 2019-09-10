package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;

public class Admonition extends DialogFragment {

    public static Admonition newInstance(String message, String title, Intent intent){
        Admonition fragment = new Admonition();
        Bundle args = new Bundle();
        args.putString(Rules.MESSAGE, message);
        args.putString(Rules.TITLE, title);
        args.putParcelable(Rules.INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (getArguments() != null){
                String message = getArguments().getString(Rules.MESSAGE);
                String title = getArguments().getString(Rules.TITLE);
                Intent intent = getArguments().getParcelable(Rules.INTENT);
                builder.setTitle(title);
                builder.setMessage(message);
                boolean del = (intent != null);
                builder.setPositiveButton(R.string.ok, (DialogInterface dialog, int id) ->{
                    if (del) getActivity().startService(intent);
                    dialog.dismiss();
                });
                if (del){
                    builder.setNegativeButton(getString(R.string.cancel), (DialogInterface dialog, int id) -> dialog.dismiss());
                }
            }
            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
