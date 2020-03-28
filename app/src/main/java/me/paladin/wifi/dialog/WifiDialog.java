package me.paladin.wifi.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import me.paladin.wifi.R;
import me.paladin.wifi.activity.InfoActivity;
import me.paladin.wifi.model.WifiItem;

public class WifiDialog extends DialogFragment implements View.OnClickListener {
    private Activity activity;
    private WifiItem item;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (savedInstanceState != null) {
            item = savedInstanceState.getParcelable("item");
        }
    }
    
    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.dialog_main_wifi, null);
        view.findViewById(R.id.dialogWifiCopy).setOnClickListener(this);
        view.findViewById(R.id.dialogWifiCode).setOnClickListener(this);
        view.findViewById(R.id.dialogWifiOpen).setOnClickListener(this);
        view.findViewById(R.id.dialogWifiShare).setOnClickListener(this);
        view.findViewById(R.id.dialogWifiClose).setOnClickListener(this);
        builder.setView(view);
        builder.setTitle(item.getSsid());
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.AppTheme_Dialog;
        }
        return dialog;
    }
    
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.dialogWifiCopy) {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", item.getPassword());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity, R.string.message_copied_password, Toast.LENGTH_SHORT).show();
            dismiss();
        } else if (id == R.id.dialogWifiCode) {
            new CodeDialog().show(getFragmentManager(), item);
            dismiss();
        } else if (id == R.id.dialogWifiOpen) {
            Intent intent = new Intent(activity, InfoActivity.class);
            intent.putExtra("item", item);
            activity.startActivity(intent);
            dismiss();
        } else if (id == R.id.dialogWifiShare) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, item.toString());
            activity.startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
            dismiss();
        } else if (id == R.id.dialogWifiClose) {
            dismiss();
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("item", item);
    }
    
    public void show(FragmentManager manager, WifiItem item) {
        if (!isAdded()) {
            this.item = item;
            show(manager, "WifiDialog");
        }
    }
}
