package reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Situateable;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseService;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.Admonition;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.FragmentOfCityInput;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyRecyclerView;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply.FragmentPager;

public class FragmentOfListOfCities extends Fragment {

    private static final String KEY_IS_VISIBLE = "key isVisible";

    public static FragmentOfListOfCities newInstance(ArrayList<Location> listItems){
        FragmentOfListOfCities fragment = new FragmentOfListOfCities();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Rules.KEY_LIST_OF_CITIES, listItems);
        fragment.setArguments(args);
        return fragment;
    }

    private Situateable situateable;
    private MyRecyclerView recyclerView;
    private CitiesRVAdapter citiesRVAdapter;
    private ArrayList<Location> listItems;
    private MyReceiver bcrDatabase;
    private boolean isLargeLayout;
    private boolean isXLargeLayout;
    private boolean visible;
    private boolean isDeleteShownCity;
    private final int index = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frame, container, false);

        isLargeLayout = getResources().getBoolean(R.bool.is_large_layout);
        isXLargeLayout = getResources().getBoolean(R.bool.is_X_large_layout);

        recyclerView = view.findViewById(R.id.items);
        recyclerView.scrollChange(isXLargeLayout);

        if (getArguments() != null) createListCities(getArguments().getParcelableArrayList(Rules.KEY_LIST_OF_CITIES));
        else {
            Intent intent = new Intent(getContext(), DatabaseService.class);
            intent.setAction(Rules.GET_ALL_CITIES);
            if (getActivity() != null){
                if (!isXLargeLayout){
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    if (actionBar != null) actionBar.setTitle(getString(R.string.cities));
                }
                getActivity().startService(intent);
            }
        }

        bcrDatabase = new MyReceiver();

        setHasOptionsMenu(true);
        return view;
    }

    private void createListCities(ArrayList<Location> locations){
        listItems = locations;
        if (visible) {
            visible = false;
            citiesRVAdapter.setVisibility(false);
        }
        citiesRVAdapter.setItems(listItems);
        citiesRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        situateable = (Situateable) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Rules.KEY_LIST_OF_CITIES, listItems);
        outState.putBoolean(KEY_IS_VISIBLE, visible);
        outState.putBoolean(Rules.KEY_IS_DELETED, isDeleteShownCity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            listItems = savedInstanceState.getParcelableArrayList(Rules.KEY_LIST_OF_CITIES);
            visible = savedInstanceState.getBoolean(KEY_IS_VISIBLE);
            isDeleteShownCity = savedInstanceState.getBoolean(Rules.KEY_IS_DELETED);
        }
        citiesRVAdapter = new CitiesRVAdapter(position -> {
            if (visible){
                listItems.get(position).setSelected(!listItems.get(position).isSelected());
                citiesRVAdapter.notifyItemChanged(position);
                return;
            }
            Location location = listItems.get(position);
            if (!location.isShowingCity()){
                Location oldPlace = listItems.get(index);
                oldPlace.setShowingCity(false);
                location.setShowingCity(true);
                listItems.set(index, location);
                listItems.set(position,oldPlace);
                citiesRVAdapter.notifyItemChanged(position);
                citiesRVAdapter.notifyItemChanged(index);
                if (isXLargeLayout &&
                        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    recyclerView.scrollToPosition(index);
                }
            }
            if (getActivity() != null) getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(situateable.getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(location.getLat() + "," + location.getLon(), false), Rules.TAG_PROGRESS_ACTION)
                    .commit();
        });
        citiesRVAdapter.setVisibility(visible);
        recyclerView.setAdapter(citiesRVAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity().getBaseContext())
                    .registerReceiver(bcrDatabase, new IntentFilter(Rules.ACTION_ANSWER_DB));
        citiesRVAdapter.setItems(listItems);
        citiesRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity().getBaseContext())
                    .unregisterReceiver(bcrDatabase);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_list_cities, menu);
        menu.removeItem(R.id.select_city);
        menu.findItem(R.id.choice).setTitle((visible) ? R.string.to_cancel : R.string.choice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                if (getActivity() != null){
                    if (isLargeLayout) new FragmentOfCityInput()
                            .show(getActivity().getSupportFragmentManager(), Rules.TAG_ENTER_PLACE);
                    else getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_master, new FragmentOfCityInput())
                            .commit();
                    return true;
                }
                return false;
            case R.id.clear:
                if (listItems == null || listItems.isEmpty()) return false;
                String message;
                Intent intent = new Intent(getContext(), DatabaseService.class);
                if (visible) {
                    ArrayList<Parcelable> list = getSelected();
                    if (list.isEmpty()) return false;
                    intent.setAction(Rules.REMOVE);
                    intent.putParcelableArrayListExtra(Rules.KEY_SELECTED, list);
                    message = getString(R.string.delete_selected_cities);
                } else{
                    intent.setAction(Rules.CLEAR);
                    message = getString(R.string.clear_all);
                }
                if (getActivity()!= null)
                    Admonition.newInstance(message, getString(R.string.delete), intent)
                            .show(getActivity().getSupportFragmentManager(), Rules.TAG_DELETE);
                return true;
            case R.id.choice:
                if (listItems == null || listItems.isEmpty()) return false;
                visible = !visible;
                citiesRVAdapter.setVisibility(visible);
                citiesRVAdapter.notifyDataSetChanged();
                item.setTitle((visible) ? R.string.to_cancel : R.string.choice);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Parcelable> getSelected(){
        ArrayList<Parcelable> list = new ArrayList<>();
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).isShowingCity()) isDeleteShownCity = true;
            if (listItems.get(i).isSelected()) list.add(listItems.get(i));
        }
        return list;
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() != null){
                FragmentManager  fm = getActivity().getSupportFragmentManager();
                if (intent.hasExtra(Rules.KEY_LIST_OF_CITIES)){
                    ArrayList<Location> cities = intent.getParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES);
                    if (cities == null || (intent.hasExtra(Rules.KEY_IS_DELETED) && intent.getBooleanExtra(Rules.KEY_IS_DELETED, false) && isDeleteShownCity)) {
                        FragmentPager fp = (FragmentPager) fm.findFragmentByTag(Rules.TAG_FRAGMENT_PAGER);
                        if (fp != null) fm.beginTransaction().remove(fp).commit();
                        isDeleteShownCity = false;
                    }
                    if (cities != null && listItems != null && cities.size() == listItems.size()) return;
                    createListCities(cities);
                }
            }
        }
    }
}
