package com.webege.profause.excelsdiary.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.helper.App;
import com.webege.profause.excelsdiary.helper.Callback;
import com.webege.profause.excelsdiary.model.Client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class AddClientDialog extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_IMAGE = 100;
    AlertDialog.Builder aBuilder;
    LayoutInflater inflater;
    View v;
    EditText etClientName;
    EditText etClientTelephone;
    TextView take_picture, save;
    RadioGroup rgGender;
    Callback.AddNewClientCallback addNewClientCallback;

    File tempOutputFile;
    String newPath;
    int gender=2;

    public void setAddNewClientCallback(Callback.AddNewClientCallback addNewClientCallback) {
        this.addNewClientCallback = addNewClientCallback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        aBuilder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.add_client_dialog_layout, null);
        etClientName = (EditText) v.findViewById(R.id.etClientName);
        etClientTelephone = (EditText) v.findViewById(R.id.etClientTelephone);
        rgGender= (RadioGroup) v.findViewById(R.id.rgGender);
        rgGender.setOnCheckedChangeListener(this);

        take_picture = (TextView) v.findViewById(R.id.take_picture);
        save = (TextView) v.findViewById(R.id.save);

        take_picture.setOnClickListener(this);
        save.setOnClickListener(this);
        tempOutputFile = new File(getContext().getExternalCacheDir(), "temp_image.jpg");

        if (savedInstanceState != null) {
            etClientName.setText(savedInstanceState.getString("etClientName"));
            etClientTelephone.setText(savedInstanceState.getString("etClientTel"));
            //tempFileUri = Uri.parse(savedInstanceState.getString("tempUri"));
        }


        aBuilder.setView(v);
        aBuilder.setCancelable(true);

        Dialog dialog = aBuilder.create();
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_picture:
                //TODO: TAKE PICTURE
                //HandleCameraButton();
                dismiss();
                break;
            case R.id.save:
                //TODO: ADD NEW CLIENT
                Save();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("etClientName", etClientName.getText().toString());
        outState.putString("etClientTel", etClientTelephone.getText().toString());
//        outState.putString("tempUri", tempFileUri.toString());
    }

    public void Save() {
        if (!isNullOrEmpty(etClientName.getText().toString())
                || !isNullOrEmpty(etClientTelephone.getText().toString())) {
            String name = etClientName.getText().toString().trim();
            String tel = etClientTelephone.getText().toString().trim();
            String date = GetDate();
            // File f = new File(App.dirPath,tempFileUri.getPath());
            Client newClient = new Client(name, tel,gender, null, date);
            if (addNewClientCallback != null) {
                addNewClientCallback.onAdded(newClient);
            }
        } else {
            Toast.makeText(getActivity(), "all fields are require", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    public boolean isNullOrEmpty(String text) {
        return text.matches("");
    }

    public String MoveFile(String file){
        String newFilePath="";
        try{
            File afile =new File(file);
            File newFile=new File(App.dirPath+File.separator + etClientName
                    .getText().toString()+"_"+etClientTelephone.getText().toString()+".jpg");

            if(afile.renameTo(newFile)){
                newFilePath=newFile.getAbsolutePath();
                afile.delete();
            }else{
                Toast.makeText(getActivity(), "Failed to move!", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return newFilePath;
    }

    public String GetDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());

        return (strDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            tempOutputFile.delete();
            return;
        }

        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutputFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE))) {
                outputFile = data.getData();
            } else {
                outputFile = tempFileUri;
            }
            beginCrop(outputFile, tempFileUri);
        } else if (requestCode == Crop.REQUEST_CROP) {
            newPath = MoveFile(tempOutputFile.getAbsolutePath());
            //Log.d("me",newPath);
        }
    }

    private void HandleCameraButton() {
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivities = getContext().getPackageManager()
                .queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
        for (ResolveInfo info : otherImageCaptureActivities) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOutputFile));
            otherImageCaptureIntents.add(captureIntent);
        }

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(selectImageIntent, "Choose avatar");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, otherImageCaptureIntents
                .toArray(new Parcelable[otherImageCaptureIntents.size()]));

        startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
    }

    private void beginCrop(Uri source, Uri destination) {
        Crop.of(source, destination).asSquare().start((Activity) getContext());
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rbfemale:
                gender=2;
                break;
            case R.id.rbmale:
                gender=1;
                break;
            default:
                gender=2;
        }
    }
}
