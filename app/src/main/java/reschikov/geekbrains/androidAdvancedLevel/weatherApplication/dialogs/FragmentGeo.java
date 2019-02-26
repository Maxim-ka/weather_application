package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage.Result;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyRecyclerView;

public class FragmentGeo extends DialogFragment {

    private static final float TEXT_SIZE = 20.0f;
    private static final int LEFT_RIGHT = 6;
    private static final int TOP_BOTTOM = 8;

    public interface Selectable{
        void getGeoCoordinates(String string);
        void discontinue();
    }

    public static FragmentGeo newInstance(ArrayList<Result> listItems){
        FragmentGeo fragment = new FragmentGeo();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Rules.KEY_RESULTS, listItems);
        fragment.setArguments(args);
        return fragment;
    }

    private Selectable selectable;

    public void setSelectable(Selectable selectable) {
        this.selectable = selectable;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frame, container, false);
        MyRecyclerView recyclerView = view.findViewById(R.id.items);
        LinearLayoutManager llm = ((LinearLayoutManager)recyclerView.getLayoutManager());
        if (llm != null) llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        if (getContext() != null){
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation()));
        }
        if (getArguments() != null){
            ArrayList<Result> results = getArguments().getParcelableArrayList(Rules.KEY_RESULTS);
            recyclerView.setAdapter(new GeoRVAdapter(results));
            recyclerView.setHasFixedSize(true);
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.title_choose_place));
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        selectable.discontinue();
    }

    private class GeoRVAdapter  extends RecyclerView.Adapter<GeoRVAdapter.ViewHolder>{

        private final ArrayList<Result> results;

        GeoRVAdapter(ArrayList<Result> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public GeoRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(new TextView(getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull GeoRVAdapter.ViewHolder viewHolder, int i) {
            Result item = results.get(i);
            viewHolder.bind(item);
        }

        @Override
        public int getItemCount() {
            if (results == null) return 0;
            return results.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            final TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setTextSize(TEXT_SIZE);
                textView.setPadding(LEFT_RIGHT, TOP_BOTTOM, LEFT_RIGHT, TOP_BOTTOM);
            }

            void bind(final Result item){
                textView.setText(item.getFormatted());
                itemView.setOnClickListener((View view) ->{
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        String coordinates = item.getGeometry().getLat() + "," + item.getGeometry().getLng();
                        selectable.getGeoCoordinates(coordinates);
                        dismiss();
                    }
                });
            }
        }
    }
}
