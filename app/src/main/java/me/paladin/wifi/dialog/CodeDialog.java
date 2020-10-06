package me.paladin.wifi.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;

import me.paladin.wifi.R;
import me.paladin.wifi.model.WifiModel;

public class CodeDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnShowListener {
    private AlertDialog dialog;
    private Activity activity;
    private WifiModel item;
    private Bitmap bitmap;
    
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.dialog_main_code, null);
        ImageView code = view.findViewById(R.id.dialogCodeImage);
        TextView warn = view.findViewById(R.id.dialogCodeWarn);
        // WIFI:T:WPA;S:network;P:password;;
        String wifiUser = "";
        String wifiTyp = "WEP";
        if (item.getType().equals(WifiModel.TYPE_WEP)) {
            wifiTyp = "WEP";
        } else if (item.getType().equals(WifiModel.TYPE_WPA)) {
            wifiTyp = "WPA";
        } else if (item.getType().equals(WifiModel.TYPE_ENTERPRISE)) {
            wifiTyp = "WPA";
            wifiUser = "U:" + qrEncode(item.getUser());
            warn.setText(R.string.message_enterprise_error);
            warn.setVisibility(View.VISIBLE);
        }
        String wifiText = "WIFI:T:" + qrEncode(wifiTyp) + ";S:" + qrEncode(item.getSsid()) + ";P:" + qrEncode(item.getPassword()) + ";" + wifiUser + ";";
        bitmap = encodeAsBitmap(wifiText);
        if (bitmap != null) {
            code.setImageBitmap(bitmap);
        } else {
            warn.setText(R.string.message_code_error);
            warn.setVisibility(View.VISIBLE);
        }
        builder.setView(view);
        builder.setTitle(item.getSsid());
        builder.setPositiveButton(R.string.action_save, null);
        builder.setNegativeButton(R.string.action_cancel, null);
        dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;
    }
    
    private Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        try {
            int qrSize = getQrSize();
            result = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, qrSize, qrSize);
        } catch (Exception e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
    
    private int getQrSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int result = width;
        if (height < width) {
            result = height;
        }
        if (result <= 0) {
            return 900;
        } else {
            return ((int) ((((double) result) * 70) / 100));
        }
    }
    
    private String qrEncode(String input) {
        String result = input;
        if (result == null || result.isEmpty()) {
            result = "";
        }
        result = result.replaceAll("\\\\", "\\\\\\\\");
        result = result.replaceAll(";", "\\\\;");
        result = result.replaceAll(",", "\\\\,");
        result = result.replaceAll(":", "\\\\:");
        return result;
    }
    
    private void saveCode() {
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Wifi Password");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (!success) {
                Toast.makeText(getActivity(), R.string.message_save_error, Toast.LENGTH_LONG).show();
                return;
            }
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Wifi Password/" + item.getSsid().replaceAll("/", " ") + ".png";
            FileOutputStream out = new FileOutputStream(dir);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Toast.makeText(getActivity(), getString(R.string.message_save_completed, dir), Toast.LENGTH_LONG).show();
            dismiss();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.message_save_error, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            saveCode();
        } else {
            requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
    
    @Override
    public void onShow(DialogInterface dialogInterface) {
        dialog.getButton(dialog.BUTTON_POSITIVE).setOnClickListener(this);
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("item", item);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), R.string.message_permission_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        saveCode();
    }
    
    public void show(FragmentManager manager, WifiModel item) {
        this.item = item;
        show(manager, "CodeDialog");
    }
}
