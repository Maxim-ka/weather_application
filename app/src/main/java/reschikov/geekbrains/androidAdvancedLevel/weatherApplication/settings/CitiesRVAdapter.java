package reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyCardView;
import static android.graphics.Color.WHITE;

public class CitiesRVAdapter extends RecyclerView.Adapter<CitiesRVAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private boolean visibility;
    private List<Location> items;
    private final OnItemClickListener itemClickListener;

    public void setItems(List<Location> items) {
        this.items = items;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    CitiesRVAdapter(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_city, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Location item = items.get(i);
        viewHolder.manageVisibility(visibility);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView textView;
        final TextView regionTView;
        final TextView countryTView;
        final TextView latTView;
        final TextView lonTView;
        final CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            regionTView = itemView.findViewById(R.id.region);
            countryTView = itemView.findViewById(R.id.country);
            latTView = itemView.findViewById(R.id.lat);
            lonTView = itemView.findViewById(R.id.lon);
            checkBox = itemView.findViewById(R.id.checkbox);
            ((MyCardView)itemView).turnScreen();
        }

        void bind(Location item){
            textView.setText(item.getName());
            regionTView.setText(item.getRegion());
            countryTView.setText(item.getCountry());
            latTView.setText(latTView.getContext().getString(R.string.fractional_number,item.getLat()));
            lonTView.setText(lonTView.getContext().getString(R.string.fractional_number, item.getLon()));
            itemView.setOnClickListener((View view) ->{
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
            });
            if (item.isShowingCity()) ((MyCardView)itemView).setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_light));
            else ((MyCardView)itemView).setCardBackgroundColor(WHITE);

            checkBox.setOnClickListener(v -> item.setSelected(checkBox.isChecked()));
            if (!visibility) item.setSelected(false);
            else checkBox.setChecked(item.isSelected());
        }

        void manageVisibility(boolean visibility) {
            if (visibility) checkBox.setVisibility(View.VISIBLE);
            else {
                checkBox.setVisibility(View.GONE);
                if (checkBox.isChecked()) checkBox.setChecked(false);
            }
        }
    }
}

