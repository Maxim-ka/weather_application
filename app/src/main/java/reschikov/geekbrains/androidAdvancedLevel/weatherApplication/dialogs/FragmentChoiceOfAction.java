package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.google.android.gms.common.GoogleApiAvailability;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Situateable;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class FragmentChoiceOfAction extends DialogFragment {

    private boolean isMasterDetail;
    private Situateable situateable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choice_dialog, container, false);

        isMasterDetail = (view.findViewById(R.id.frame_detail) != null);

        RadioButton place = view.findViewById(R.id.current_location);
        RadioButton city = view.findViewById(R.id.set_city);

        place.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                int stateGooglePlayServices = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
                if (getActivity() != null){
                    if (stateGooglePlayServices == SUCCESS) getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(situateable.getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(null, false), Rules.TAG_PROGRESS_ACTION)
                        .commit();
                    else GoogleMessage.newInstance(stateGooglePlayServices)
                        .show(getActivity().getSupportFragmentManager(), Rules.KEY_NO_GOOGLE);
                }
                dismiss();
            }
        });

        city.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                if (getActivity() != null){
                    if (getResources().getBoolean(R.bool.is_large_layout) || isMasterDetail) new FragmentOfCityInput()
                            .show(getActivity().getSupportFragmentManager(), Rules.TAG_ENTER_PLACE);
                    else getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_master, new FragmentOfCityInput())
                            .commit();
                }
                dismiss();
            }
        });
        setCancelable(false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.determine_location));
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        situateable = (Situateable) context;
    }
}
