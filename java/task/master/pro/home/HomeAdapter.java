package task.master.pro.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import task.master.pro.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    public interface OnItemClick {
        void onClick(HomeItem item, int position);
    }

    private final List<HomeItem> items;
    private final OnItemClick onItemClick;

    public HomeAdapter(List<HomeItem> items, OnItemClick onItemClick) {
        this.items = items;
        this.onItemClick = onItemClick;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, subtitle;

        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgThumb);
            title = v.findViewById(R.id.txtTitle);
            subtitle = v.findViewById(R.id.txtSubtitle);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        HomeItem it = items.get(position);
        h.img.setImageResource(it.imageRes);
        h.title.setText(it.title);
        h.subtitle.setText(it.subtitle);
        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(it, position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
