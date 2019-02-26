package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyRecyclerView;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

public class FragmentListMessage extends Fragment implements FragmentShippingControl.Settable {

    public static FragmentListMessage newInstance(ArrayList<String> listMessage){
        FragmentListMessage fragment = new FragmentListMessage();
        Bundle args = new Bundle();
        args.putStringArrayList(Rules.KEY_LIST, listMessage);
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<String> listMessage;
    private MyRecyclerView recyclerView;
    private boolean set;
    private MessageRVAdapter messageRVAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frame, container, false);
        if (getArguments() != null) listMessage = getArguments().getStringArrayList(Rules.KEY_LIST);
        recyclerView = view.findViewById(R.id.items);
        recyclerView.setHasFixedSize(!set);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            set = savedInstanceState.getBoolean(Rules.KEY_SET);
            listMessage = savedInstanceState.getStringArrayList(Rules.KEY_LIST);
        }
        setNestedScrollingRecyclerView(set);
        messageRVAdapter = new MessageRVAdapter(listMessage, set);
        recyclerView.setAdapter(messageRVAdapter);
        recyclerView.scrollChange(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean(Rules.KEY_SET, set);
        saveInstanceState.putStringArrayList(Rules.KEY_LIST, messageRVAdapter.getItems());
    }

    @Override
    public void setDisplayMode(boolean isSMS) {
        set = isSMS;
        recyclerView.clearFocus();
        messageRVAdapter.changeMessage(isSMS);
    }

    @Override
    public void setNestedScrollingRecyclerView(boolean set) {
        if (recyclerView != null){
            recyclerView.setNestedScrollingEnabled(!set);
        }
    }

    @Override
    public ArrayList<String> getMessages() {
        recyclerView.clearFocus();
        return messageRVAdapter.getItems();
    }
}
