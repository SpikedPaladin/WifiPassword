package me.paladin.wifi.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.paladin.wifi.R;
import me.paladin.wifi.model.WifiItem;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private ArrayList<WifiItem> filteredData, data;
    private ItemClickListener listener;
    private LayoutInflater inflater;
    private Context context;
    
    public WifiAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
        filteredData = data;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_main_list, parent, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiItem item = getItem(position);
        holder.ssid.setText(item.getSsid());
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getBoolean("show_passwords", true)) {
            holder.password.setText(item.getPassword());
        } else {
            holder.password.setText(item.getProtectedPassword());
        }
        holder.type.setText(item.getType());
        if (item.getPassword().length() <= 0) {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_wifi));
        }
        if (item.getUser().length() > 0) {
            StringBuilder user = new StringBuilder();
            if (item.getType().equals(WifiItem.TYPE_ENTERPRISE)) {
                user.append("User: ");
            } else if (item.getType().equals(WifiItem.TYPE_WEP)) {
                user.append("Keyindex: ");
            }
            user.append(item.getUser());
            holder.user.setText(user.toString());
            holder.user.setVisibility(TextView.VISIBLE);
        } else {
            holder.user.setVisibility(TextView.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return filteredData.size();
    }
    
    public WifiItem getItem(int position) {
        return filteredData.get(position);
    }
    
    public void setItems(ArrayList<WifiItem> items) {
        data = items;
        filteredData = data;
    }
    
    public void addItem(WifiItem item) {
        data.add(item);
        filteredData = data;
    }
    
    public void clear() {
        data = new ArrayList<>();
        filteredData = data;
    }
    
    public Filter getFilter() {
        return new Filter() {
            
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredData = data;
                } else {
                    ArrayList<WifiItem> filteredList = new ArrayList<>();
                    for (WifiItem item : data) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        if (item.getSsid().toLowerCase().contains(charString) || (preferences != null && preferences.getBoolean("password_search_enabled", false) && item.getPassword().toLowerCase().contains(charString))) {
                            filteredList.add(item);
                        }
                    }
                    filteredData = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }
            
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<WifiItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ssid, password, user, type;
        ImageView image;
        
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            ssid = itemView.findViewById(R.id.adapterSSID);
            password = itemView.findViewById(R.id.adapterPassword);
            user = itemView.findViewById(R.id.adapterUser);
            type = itemView.findViewById(R.id.adapterType);
            image = itemView.findViewById(R.id.adapterImage);
            itemView.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getAdapterPosition());
            }
        }
    }
    
    public void setClickListener(ItemClickListener listener) {
        this.listener = listener;
    }
    
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
