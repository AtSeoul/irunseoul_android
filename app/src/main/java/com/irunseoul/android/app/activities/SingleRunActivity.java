package com.irunseoul.android.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.adapters.MyRunsRecycleViewAdapter;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.utilities.PreferencesHelper;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class SingleRunActivity extends AppCompatActivity {

    private static final String TAG = SingleRunActivity.class.getSimpleName();
    public static final String TYPE = "image/*";
    public static final String REF_IMAGES = "run_images";

    private DatabaseReference mDatabase;
    private UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imagesRef;
    private MyRun myRun;
    private Query mRunQuery;
    private AlertDialog progressDialog;
    private ImagePicker imagePicker;
    private String certImagePath = "";

    @BindView(R.id.run_distance)
    TextView mRunDistance;

    @BindView(R.id.run_duration)
    TextView mRunDuration;

    @BindView(R.id.run_speed)
    TextView mRunSpeed;

    @BindView(R.id.run_achieve)
    TextView mRunAchievements;

    @BindView(R.id.run_date)
    TextView mDateView;

    @BindView(R.id.record_image)
    ImageView mRecordImage;

    @BindView(R.id.add_record_btn)
    Button mRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_run);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String runKey = getIntent().getStringExtra(MyRun.ARG_KEY);
        String runTitle = getIntent().getStringExtra(MyRun.ARG_TITLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRunQuery = mDatabase.child("user-runs").child(getUid()).child(runKey);

        setTitle(runTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new SpotsDialog(this, getResources().getString(R.string.fetching_data));

        setFirebaseStorage();
        addFirebaseEventListener();
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new CertImagePickerCallback());
        imagePicker.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);

    }


    private void createInstagramIntent(String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(TYPE);

        // Create the URI from the media
//        File media = new File(mediaPath);
//        Uri uri = Uri.fromFile(media);
        Uri uri = Uri.parse(mediaPath);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private void addFirebaseEventListener() {

        // Read from the database
        progressDialog.show();
        mRunQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                        myRun = dataSnapshot.getValue(MyRun.class);
                        myRun.runKey = dataSnapshot.getKey();
                        Log.d(TAG, "runSnapshot key("+ dataSnapshot.getKey() + "): " + myRun.title);

                        setupUI();

                }
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setupUI() {


        mDateView.setText(myRun.date);
        mRunDistance.setText(String.format(Locale.US, "%s KM",myRun.distance));
        mRunDuration.setText(myRun.moving_time);
        mRunSpeed.setText(String.format(Locale.US, "%s KM/H",myRun.average_speed));
        if(myRun.achievement_count.isEmpty()) {
            mRunAchievements.setText("0");
        } else {
            mRunAchievements.setText(myRun.achievement_count);
        }

        setCertRecordImage();
    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed");
        SharedPreferences sharedPref = PreferencesHelper.getSharedPref(this);

        PreferencesHelper.writePref(sharedPref, PreferencesHelper.WHICH_FRAGMENT, 2);

        super.onBackPressed();
    }

    public String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @OnClick(R.id.add_record_btn)
    public void onClickAddRecord(Button view) {

        if(isStoragePermissionGranted()) {
            imagePicker.pickImage();
        }

    }

    private void setFirebaseStorage() {

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child(REF_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == Picker.PICK_IMAGE_DEVICE) {
                if(imagePicker == null) {
                    imagePicker = new ImagePicker(SingleRunActivity.this);
                    imagePicker.setImagePickerCallback(new CertImagePickerCallback());
                }
                imagePicker.submit(data);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            imagePicker.pickImage();

        }
    }

    private class CertImagePickerCallback implements ImagePickerCallback {

        @Override
        public void onImagesChosen(List<ChosenImage> images) {
            Log.d(TAG, "chosen image : " + images.get(0).toString());

            ChosenImage chosenImage = images.get(0);
//            mRecordImage.setImageURI(Uri.parse(chosenImage.getThumbnailPath()));
            setCertRecordImageLocal(chosenImage.getOriginalPath());
            certImagePath = chosenImage.getOriginalPath();

            updateCertPic();
        }

        @Override
        public void onError(String s) {

            Log.d(TAG, "error while selecting image : " + s);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"Permission is granted");
                return true;
            } else {

                Log.d(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG,"Permission is granted");
            return true;
        }
    }

    private void updateCertPic() {


        if(!TextUtils.isEmpty(certImagePath)) {

            Uri file = Uri.fromFile(new File(certImagePath));
            StorageReference userImageRef = imagesRef.child(file.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            uploadTask = userImageRef.putFile(file, metadata);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final HashMap<String, Object> userImageMap = new HashMap<>();
                    userImageMap.put("record_pic_url", downloadUrl.toString());
                    myRun.record_pic_url = downloadUrl.toString();
                    DatabaseReference runRef = mDatabase.child("runs").child(myRun.runKey);
                    runRef.updateChildren(userImageMap);
                    mRunQuery.getRef().updateChildren(userImageMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                Log.d(TAG, "successfully added image");
                                setCertRecordImage();
                                showToastMessage("Successfully added image");

                            }
                        }
                    });


                }
            });
        } else {
            //TODO : No image selected
        }

    }

    public void setCertRecordImage() {

        if(!TextUtils.isEmpty(myRun.record_pic_url)) {

            mRecordButton.setText(getResources().getString(R.string.replace_image_certificate));
            Picasso.with(this)
                    .load(myRun.record_pic_url)
                    .error(R.drawable.couple_run)
                    .resize(400, 600)
                    .centerCrop()
                    .into(mRecordImage);
        }

    }

    public void setCertRecordImageLocal(String imagePath) {

        if(!TextUtils.isEmpty(imagePath)) {

            File f = new File(imagePath);
            mRecordButton.setText(getResources().getString(R.string.replace_image_certificate));
            Picasso.with(this)
                    .load(f)
                    .resize(500, 700)
                    .centerCrop()
                    .into(mRecordImage);
        }
    }

    private void showToastMessage(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
