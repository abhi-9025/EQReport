package com.example.abhi192001.lucid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private EditText username, userProfName, userStatus, userCountry, userGender, userRelation, userDob;
    private Button updateAccountSettingsButton;
    private CircleImageView userProfImage;

    private DatabaseReference settingUserRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private StorageReference userProfileImageRef;
    final static int galleryPic = 1;

    String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        settingUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       username=(EditText)findViewById(R.id.settings_username);
        userCountry = (EditText) findViewById(R.id.settings_country);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userProfName = (EditText) findViewById(R.id.settings_profile_full_name);
        userGender = (EditText) findViewById(R.id.settings_Gender);
        userRelation = (EditText) findViewById(R.id.settings_relationship_status);
        userDob = (EditText) findViewById(R.id.settings_dob);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        loadingBar=new ProgressDialog(this);

        updateAccountSettingsButton = (Button) findViewById(R.id.update_account_setting_buttons);

        settingUserRef.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);

                    username.setText(myUsername);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText(myDOB);
                    userCountry.setText(myCountry);
                    userGender.setText(myGender);
                    userRelation.setText(myRelationshipStatus);
                    Log.d("TAG", "IN add Value Event");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateAccountInfo();

            }
        });
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPic);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPic && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {    loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait ! while we are updating your Profile Image.");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri=result.getUri();
                StorageReference filePath =userProfileImageRef.child(current_user_id+ ".jpg");

                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();
                                        settingUserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {  Intent selfIntent=new Intent(SettingsActivity.this,SettingsActivity.class);
                                                    startActivity(selfIntent);
                                                    Toast.makeText(SettingsActivity.this,"Profile Image Stored to firebase databse Successfully",Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                                else
                                                {   String message=task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this,"Error Occured!"+message,Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                                    }
                                });

                            }
                        });
            }
            else
            {
                Toast.makeText(SettingsActivity.this,"Error Occured! Image Can't be cropped.",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
    }

    private void ValidateAccountInfo()
    {

        String name=username.getText().toString();
        String profilename =userProfName.getText().toString();
        String status =userStatus.getText().toString();
        String dob =userDob.getText().toString();
        String country=userCountry.getText().toString();
        String gender =userGender.getText().toString();
        String relationshipstatus =userRelation.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please Write your username...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(profilename))
        {
            Toast.makeText(this, "Please Write your Full Name...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(status))
        {
            Toast.makeText(this, "Please Write your status...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(gender))
        {
            Toast.makeText(this, "Please Write your Gender..", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(relationshipstatus))
        {
            Toast.makeText(this, "Please Write your Relationship Status...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(dob))
        {
            Toast.makeText(this, "Please Write your Date of Birth...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Updating...");
            loadingBar.setMessage("Please wait ! while we are updating your Account.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccountInfo(name,country, status , profilename , gender, relationshipstatus,dob);
        }
        Log.d("TAG","IN Validate Account Info");

    }
    private void UpdateAccountInfo(String username,String userCountry, String userStatus, String userProfName, String userGender, String userRelation,String userDob)
    {
        HashMap userMap  =new HashMap();
        userMap.put("username",username);
        userMap.put("country",userCountry);
        userMap.put("status",userStatus);
        userMap.put("dob",userDob);
        userMap.put("fullname",userProfName);
        userMap.put("gender",userGender);
        userMap.put("relationshipstatus",userRelation);


        settingUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this,"Account Updated Successfully",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    String message=task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this,"Error!"+message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }
        });


    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}