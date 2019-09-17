package me.paladin.wifi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import me.paladin.wifi.R;

public class ExitDialog extends AppCompatDialogFragment implements DialogInterface.OnClickListener {
    private Activity activity;
    
    public ExitDialog(Activity activity) {
        this.activity = activity;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_exit_title);
        builder.setMessage(R.string.dialog_exit_message);
        builder.setPositiveButton(R.string.action_confirm, this);
        builder.setNegativeButton(R.string.action_cancel, null);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.AppTheme_Dialog;
        }
        return dialog;
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            activity.finish();
        }
    }
    
    public void show(FragmentManager manager) {
        show(manager, "ExitDialog");
    }
}
