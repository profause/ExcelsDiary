package com.webege.profause.excelsdiary.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.adapter.MeasurementListAdapter;
import com.webege.profause.excelsdiary.helper.App;
import com.webege.profause.excelsdiary.helper.Callback;
import com.webege.profause.excelsdiary.helper.RealmHelper;
import com.webege.profause.excelsdiary.helper.SimpleDividerItemDecoration;
import com.webege.profause.excelsdiary.model.Client;
import com.webege.profause.excelsdiary.model.Measurement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class ClientDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, MeasurementListAdapter.ClickListener {

    private static final int REQUEST_SELECT_IMAGE = 100;
    ImageView clientImage, ivEditbtn, ivBg, ivCamerabtn;
    EditText clientName, clientTel;
    RadioButton rbfemale, rbmale;
    RadioGroup rgGender;
    ProgressBar imageProgress;
    RecyclerView measurementList;
    MeasurementListAdapter measurementListAdapter;
    FloatingActionButton fab;
    FragmentManager fragmentManager;
    File tempOutputFile;

    boolean isEditing;
    int gender = 0;

    Client c;
    Realm realm;
    RealmHelper realmHelper;
    RealmChangeListener realmChangeListener;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        realm = Realm.getDefaultInstance();
        try {
            c = getIntent().getParcelableExtra("clientDetails");
            getSupportActionBar().setTitle(c.getName());
            isEditing = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        InitialiseViews();
        tempOutputFile = new File(getExternalCacheDir(), "temp_image.jpg");
        realmHelper = new RealmHelper(realm, this);

        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                realmHelper.RetrieveMeasurementRealm(c.getName());
                measurementListAdapter.setMeasurementList(realmHelper.measurementsRefresh());
            }
        };
        realm.addChangeListener(realmChangeListener);
        DisplayDetails(c);

    }

    private void ChangeBackground(int a) {
        Picasso.with(ClientDetailsActivity.this)
                .load(a)
                .fit().centerCrop()
                .into(ivBg);
    }

    private void DisplayDetails(Client c) {
        clientName.setText(c.getName());
        clientTel.setText(c.getTelephone());
        //Todo
        realmHelper.RetrieveMeasurementRealm(c.getName());
        measurementListAdapter.setMeasurementList(realmHelper.measurementsRefresh());
        try {
            Picasso.with(this).load(new File(c.getImageUri()))
                    .error(R.drawable.user)
                    .fit()
                    .placeholder(R.drawable.user)
                    .into(clientImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageProgress.setVisibility(View.GONE);
                        }
                    });
            switch (c.getGender()) {
                case 1:
                    ChangeBackground(R.drawable.kent5);
                    break;
                case 2:
                    ChangeBackground(R.drawable.kent5);
                    break;
                default:
                    ChangeBackground(R.drawable.kent5);

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        SetGender(c.getGender());
    }


    private void SetGender(int gender) {
        switch (gender) {
            case 1:
                rbmale.setChecked(true);
                rbfemale.setChecked(false);
                break;
            case 2:
                rbmale.setChecked(false);
                rbfemale.setChecked(true);
                break;
        }
    }


    private void InitialiseViews() {
        clientImage = (ImageView) findViewById(R.id.clientImage);
        ivBg = (ImageView) findViewById(R.id.ivBg);
        ivEditbtn = (ImageView) findViewById(R.id.ivEditbtn);
        ivCamerabtn = (ImageView) findViewById(R.id.ivCamerabtn);
        measurementList = (RecyclerView) findViewById(R.id.measurementList);
        //init adapter and set adapter
        measurementListAdapter = new MeasurementListAdapter(this);
        measurementList.setAdapter(measurementListAdapter);
        SlideInLeftAnimator animation = new SlideInLeftAnimator();
        animation.setAddDuration(350);
        animation.setRemoveDuration(350);
        measurementList.setItemAnimator(animation);
        measurementList.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        measurementList.addItemDecoration(new SimpleDividerItemDecoration(this));
        measurementList.setHasFixedSize(true);

        // measurementListAdapter.setMeasurementList(addTempClients());
        measurementListAdapter.setClickListener(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        measurementList.setLayoutManager(layoutManager);
        ivEditbtn.setOnClickListener(this);
        ivCamerabtn.setOnClickListener(this);

        clientName = (EditText) findViewById(R.id.clientName);
        clientTel = (EditText) findViewById(R.id.clientTel);
        rbfemale = (RadioButton) findViewById(R.id.rbfemale);
        rbmale = (RadioButton) findViewById(R.id.rbmale);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        rgGender.setOnCheckedChangeListener(this);

        imageProgress = (ProgressBar) findViewById(R.id.image_progressBar);

    }

    public ArrayList<Measurement> addTempClients() {
        ArrayList<Measurement> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new Measurement("mensah", "054375", 1));
        }
        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                //TODO: ADD MEASUREMENT
                AddMeasurementDialog dialog = AddMeasurementDialog.newInstance(new Measurement(), c.getName(), false);
                dialog.setAddNewMeasurementCallback(new Callback.AddNewMeasurementCallback() {
                    @Override
                    public void onAdded(Measurement measurement) {
                        RealmHelper helper = new RealmHelper(realm, ClientDetailsActivity.this);
                        if (helper.Save(c, measurement)) {
                            //clientListAdapter.AddClient(client);
                        }
                    }
                });
                dialog.show(fragmentManager, "addMeasurementDialog");
                break;
            case R.id.ivEditbtn:
                //TODO: HANDLE EDIT BUTTON
                HandleEditButton();
                break;

            case R.id.ivCamerabtn:
                //TODO: HANDLE CAMERA BUTTON
                HandleCameraButton();
                break;
        }
    }

    private void HandleCameraButton() {
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivities = getPackageManager()
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
           /* CropImage.activity(outputFile)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);*/
        } else if (requestCode == Crop.REQUEST_CROP) {
            clientImage.setImageResource(0);
            String newPath = MoveFile(tempOutputFile.getAbsolutePath());
            clientImage.setImageURI(Uri.parse(newPath));

            SaveEdit(newPath);
            Log.d("me", newPath);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                tempOutputFile=new File(resultUri.getPath());

                clientImage.setImageResource(0);
                String newPath = MoveFile(tempOutputFile.getAbsolutePath());
                clientImage.setImageURI(Uri.parse(newPath));

                SaveEdit(newPath);
                Toast.makeText(ClientDetailsActivity.this, tempOutputFile.getPath()+"", Toast.LENGTH_LONG).show();
                //clientImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void beginCrop(Uri source, Uri destination) {
        Crop.of(source, destination)
                .asSquare()
                .start(ClientDetailsActivity.this);
    }

    private void HandleEditButton() {
        if (isEditing) {
            ivEditbtn.setImageResource(R.drawable.ic_action_save);
            clientName.setEnabled(true);
            clientName.setBackgroundResource(R.drawable.edit_text_bg);
            clientTel.setEnabled(true);
            clientTel.setBackgroundResource(R.drawable.edit_text_bg);
            rgGender.setEnabled(true);
            rbfemale.setEnabled(true);
            rbmale.setEnabled(true);

        } else if (!isEditing) {
            ivEditbtn.setImageResource(R.drawable.ic_action_edit);
            clientName.setBackgroundResource(R.drawable.edit_text_disabled_bg);
            clientName.setEnabled(false);
            clientTel.setBackgroundResource(R.drawable.edit_text_disabled_bg);
            clientTel.setEnabled(false);
            rgGender.setEnabled(false);
            rbfemale.setEnabled(false);
            rbmale.setEnabled(false);
            SaveEdit(MoveFile(c.getImageUri()));

        }
        isEditing = !isEditing;
    }

    private void SaveEdit(String imageUri) {
        if (!isNullOrEmpty(clientName.getText().toString())
                || !isNullOrEmpty(clientTel.getText().toString())) {
            String name = clientName.getText().toString();
            String tel = clientTel.getText().toString();
            Client newClient = new Client(name, tel, gender, imageUri, c.getDateAdded());
            getSupportActionBar().setTitle(newClient.getName());
            realmHelper = new RealmHelper(realm, getApplicationContext());

            if (realmHelper.Update(c.getName(), newClient)) {
                //Toast.makeText(ClientDetailsActivity.this, "saved", Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(ClientDetailsActivity.this, "client not updated", Toast.LENGTH_SHORT).show();

            }

        }
    }

    public String MoveFile(String file) {
        String newFilePath = "";
        try {
            File afile = new File(file);
            File newFile = new File(App.dirPath + File.separator + clientName
                    .getText().toString() + "_" + clientTel.getText().toString() + ".jpg");

            if (afile.renameTo(newFile)) {
                newFilePath = newFile.getAbsolutePath();
                afile.delete();
            } else {
                Toast.makeText(this, "Failed to move!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFilePath;
    }

    public boolean isNullOrEmpty(String text) {
        return text.matches("");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rbfemale:
                gender = 2;
                break;
            case R.id.rbmale:
                gender = 1;
                break;
            default:
                gender = 0;
        }
    }

    @Override
    public void itemClicked(View view, int position, final Measurement m) {
        switch (view.getId()) {
            case R.id.ivDelBtn:
                //TODO DELETE MEASUREMENT
                ConfirmDeleteDialog deleteDialog = new ConfirmDeleteDialog();
                deleteDialog.setDeleteCallback(new Callback.DeleteCallback() {
                    @Override
                    public void onDelete() {
                        RealmHelper helper = new RealmHelper(realm, ClientDetailsActivity.this);
                        helper.DeleteMeasurement(c.getName(), m.getName());
                    }
                });
                deleteDialog.show(fragmentManager, "confirmDelete");
                break;
            case R.id.ivEditBtn:
                //TODO EDIT MEASUREMENT
                AddMeasurementDialog dialog = AddMeasurementDialog.newInstance(m, c.getName(), true);
                dialog.show(fragmentManager, "addMeasurementDialog");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }
    }
}
