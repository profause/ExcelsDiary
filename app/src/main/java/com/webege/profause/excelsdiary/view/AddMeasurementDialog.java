package com.webege.profause.excelsdiary.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.helper.Callback;
import com.webege.profause.excelsdiary.helper.RealmHelper;
import com.webege.profause.excelsdiary.model.Measurement;

import io.realm.Realm;

/**
 * Created by Emmanuel Mensah on 8/22/2016.
 */
public class AddMeasurementDialog extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    AlertDialog.Builder aBuilder;
    LayoutInflater inflater;
    View v;
    EditText etName;
    EditText etValue;
    RadioGroup rgUnit;
    RadioButton rbcm,rbinches;

    TextView cancel, save, title;
    Callback.AddNewMeasurementCallback addNewMeasurementCallback;
    String value, name,clientName;
    boolean isUpdate;
    Measurement m;

    Realm realm;
    RealmHelper realmHelper;
    private int unit=1;

    public void setAddNewMeasurementCallback(Callback.AddNewMeasurementCallback addNewMeasurementCallback) {
        this.addNewMeasurementCallback = addNewMeasurementCallback;
    }

    static AddMeasurementDialog newInstance(Measurement m,String clientName,boolean isUpdate) {
        AddMeasurementDialog dialog = new AddMeasurementDialog();
        Bundle args = new Bundle();
        args.putParcelable("measurement", m);
        args.putString("clientName", clientName);
        args.putBoolean("isUpdate",isUpdate);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        m = getArguments().getParcelable("measurement");
        name = m.getName();
        value = m.getValue();
        unit=m.getUnit();
        clientName = getArguments().getString("clientName");
        isUpdate=getArguments().getBoolean("isUpdate");

        aBuilder = new AlertDialog.Builder(getActivity());
        realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(realm, getContext());


        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.add_measurement_dialog_layout, null);
        etName = (EditText) v.findViewById(R.id.etName);
        etValue = (EditText) v.findViewById(R.id.etValue);
        save = (TextView) v.findViewById(R.id.save);
        title = (TextView) v.findViewById(R.id.title);
        cancel = (TextView) v.findViewById(R.id.cancel);
        rbcm = (RadioButton)v. findViewById(R.id.rbcm);
        rbinches = (RadioButton)v. findViewById(R.id.rbinches);
        rgUnit= (RadioGroup) v.findViewById(R.id.rgUnit);
        rgUnit.setOnCheckedChangeListener(this);


        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        etName.setText(name);
        etValue.setText(value);
        SetUnit(unit);


        aBuilder.setView(v);
        aBuilder.setCancelable(true);

        Dialog dialog = aBuilder.create();
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                //TODO: TAKE PICTURE
                Cancel();
                break;
            case R.id.save:
                //TODO: ADD NEW CLIENT
                if (!isUpdate) {
                    Save();
                } else {
                    Update();
                }
                break;
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void Save() {
        if (!isNullOrEmpty(etName.getText().toString())
                || !isNullOrEmpty(etValue.getText().toString())) {
            String name = etName.getText().toString().trim();
            String value = etValue.getText().toString().trim();

            // File f = new File(App.dirPath,tempFileUri.getPath());
            Measurement newMeasurement = new Measurement(name, value,unit);
            if (addNewMeasurementCallback != null) {
                addNewMeasurementCallback.onAdded(newMeasurement);
            }
        } else {
            Toast.makeText(getActivity(), "all fields are require", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    private void Cancel() {
        dismiss();
    }

    private void Update() {
        if (!isNullOrEmpty(etName.getText().toString())
                || !isNullOrEmpty(etValue.getText().toString())) {
            String name = etName.getText().toString().trim();
            String value = etValue.getText().toString().trim();
            realmHelper.Update(this.clientName,this.name, new Measurement(name, value,unit));

        } else {
            Toast.makeText(getActivity(), "all fields are require", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    public boolean isNullOrEmpty(String text) {
        return text.matches("");
    }

    private void SetUnit(int unit) {
        switch (unit) {
            case 1:
                rbcm.setChecked(true);
                rbinches.setChecked(false);
                break;
            case 2:
                rbcm.setChecked(false);
                rbinches.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rbcm:
                unit=1;
                break;
            case R.id.rbinches:
                unit=2;
                break;
            default:
                unit=1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm!=null){
            realm.close();
        }
    }
}
