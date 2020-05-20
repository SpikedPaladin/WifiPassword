package me.paladin.wifi.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.paladin.wifi.R;
import me.paladin.wifi.model.WifiModel;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private ArrayList<WifiModel> filteredData, data;
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
        WifiModel item = getItem(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return filteredData.size();
    }
    
    public WifiModel getItem(int position) {
        return filteredData.get(position);
    }
    
    public ArrayList<WifiModel> getItems() {
        return data;
    }
    
    public void setItems(ArrayList<WifiModel> items) {
        data = items;
        filteredData = data;
    }
    
    public void addItem(WifiModel item) {
        data.add(item);
        filteredData = data;
    }
    
    public void clear() {
        data.clear();
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
                    ArrayList<WifiModel> filteredList = new ArrayList<>();
                    for (WifiModel item : data) {
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
                filteredData = (ArrayList<WifiModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ssid, password, user, type;
        
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            ssid = itemView.findViewById(R.id.adapter_ssid);
            password = itemView.findViewById(R.id.adapter_password);
            user = itemView.findViewById(R.id.adapter_user);
            type = itemView.findViewById(R.id.adapter_type);
        }
        
        private void bind(@NonNull WifiModel item) {
            itemView.setOnClickListener(this);
            ssid.setText(item.getSsid());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.getBoolean("show_passwords", true)) {
                password.setText(item.getPassword());
            } else {
                password.setText(item.getProtectedPassword());
            }
            type.setText(item.getType());
            if (item.getPassword().length() <= 0) {
                type.setCompoundDrawables(null, ContextCompat.getDrawable(context, R.drawable.ic_wifi), null, null);
            }
            if (item.getUser().length() > 0) {
                if (item.getType().equals(WifiModel.TYPE_ENTERPRISE)) {
                    user.setText(context.getString(R.string.description_user, item.getUser()));
                } else if (item.getType().equals(WifiModel.TYPE_WEP)) {
                    user.setText(context.getString(R.string.description_key_index, item.getUser()));
                }
                user.setVisibility(TextView.VISIBLE);
            } else {
                user.setVisibility(TextView.GONE);
            }
        }
        
        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getAbsoluteAdapterPosition());
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
