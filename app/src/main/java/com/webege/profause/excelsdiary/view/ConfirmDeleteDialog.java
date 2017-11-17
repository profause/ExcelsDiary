package com.webege.profause.excelsdiary.view;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.helper.Callback;

/**
 * Created by Emmanuel Mensah on 8/24/2016.
 */
public class ConfirmDeleteDialog extends DialogFragment implements View.OnClickListener {
    AlertDialog.Builder aBuilder;
    LayoutInflater inflater;
    View v;
    TextView cancel, delete;
    Callback.DeleteCallback deleteCallback;

    public void setDeleteCallback(Callback.DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        aBuilder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.confirm_delete_dialog_layout, null);

        cancel = (TextView) v.findViewById(R.id.cancel);
        delete = (TextView) v.findViewById(R.id.Delete);

        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);

        aBuilder.setView(v);
        aBuilder.setCancelable(true);

        Dialog dialog = aBuilder.create();
        return dialog;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                //TODO: CANCEL
                dismiss();
                break;
            case R.id.Delete:
                //TODO: DELETE
                Delete();
                break;
        }
    }

    private void Delete() {
        if(deleteCallback!=null){
        deleteCallback.onDelete();
        dismiss();}

    }
}
