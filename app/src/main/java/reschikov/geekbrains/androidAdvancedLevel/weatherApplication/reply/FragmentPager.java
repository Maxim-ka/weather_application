package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
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
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class FragmentPager extends Fragment {

    private static final float SIZE_TEXT_STRIP = 20.0f;

    public static FragmentPager newInstance(ServerResponse response){
        FragmentPager fragment = new FragmentPager();
        Bundle args = new Bundle();
        args.putParcelable(Rules.KEY_WEATHER, response);
        fragment.setArguments(args);
        return fragment;
    }

    private Situateable situateable;
    private ServerResponse response;
    private FragmentAdapter fragAdapter;
    private ViewPager pager;
    private boolean isXLargeLayout;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_frame, container, false);

        isXLargeLayout = getResources().getBoolean(R.bool.is_X_large_layout);

        if (getArguments() != null) response = getArguments().getParcelable(Rules.KEY_WEATHER);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener((View v) ->{
            if (response != null){
                String city = response.getLocation().getLat() + "," + response.getLocation().getLon();
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(situateable.getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(city, false))
                            .commit();
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            response = savedInstanceState.getParcelable(Rules.KEY_WEATHER);
        }

        ArrayList<Fragment> listFragment = new ArrayList<>();
        listFragment.add(FragmentCurrentDisplay.newInstance(response));
        listFragment.add(FragmentForecastDisplay.newInstance(response));

        pager = view.findViewById(R.id.pages);
        if (getActivity() != null ){
            fragAdapter = new FragmentAdapter(getChildFragmentManager(), listFragment, getContext());
            pager.setAdapter(fragAdapter);
        }

        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        if (!isXLargeLayout && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tabLayout.setupWithViewPager(pager);
        } else {
            tabLayout.setVisibility(View.GONE);
            PagerTitleStrip titleStripView = view.findViewById(R.id.pagerTitleStrip);
            titleStripView.setTextSize(COMPLEX_UNIT_SP, SIZE_TEXT_STRIP);
            titleStripView.setTextColor(Color.WHITE);
            titleStripView.setBackgroundResource(R.color.colorPrimary);
            titleStripView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.to_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(getString(R.string.type_text_plain));
                intent.putExtra(Intent.EXTRA_TEXT, ((Collectable)fragAdapter.getItem(pager.getCurrentItem())).collectData(getContext(), response));
                startActivity(Intent.createChooser(intent, getString(R.string.how_to_send)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        situateable = (Situateable) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_share_weather, menu);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Rules.KEY_WEATHER, response);
    }
}
