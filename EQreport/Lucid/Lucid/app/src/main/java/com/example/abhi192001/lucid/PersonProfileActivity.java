package com.example.abhi192001.lucid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity
{
    private TextView username, userProfName, userStatus, userCountry, userGender, userRelation, userDob;
    private CircleImageView userProfileImage;

    private Button send_friend_request_Button;
    private Button decline_friend_request_Button;

    private DatabaseReference profileUserRef,UsersRef,FriendRequestRef,FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId,receiverUserID,CURRENT_STATE,saveCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth=FirebaseAuth.getInstance();
         senderUserId=mAuth.getCurrentUser().getUid();
        receiverUserID=getIntent().getExtras().get("visit_user_id").toString();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        FriendRequestRef=FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        InitialiseFields();

        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    username.setText("@ "+myUsername);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText("DOB: "+myDOB);
                    userCountry.setText("Country: "+myCountry);
                    userGender.setText("Gender: "+myGender);
                    userRelation.setText("Relationship Status: "+myRelationshipStatus);

                    MaintainanceOfButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        decline_friend_request_Button.setVisibility(View.INVISIBLE);
        decline_friend_request_Button.setEnabled(false);

        if (!senderUserId.equals(receiverUserID))
        {
            send_friend_request_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                   send_friend_request_Button.setEnabled(false);

                   if(CURRENT_STATE.equals("not_friends"));
                    {
                        SendFriendRequestToPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAnExistingFriend();
                    }
                }
            });
        }
        else
        {
            decline_friend_request_Button.setVisibility(View.INVISIBLE);
            send_friend_request_Button.setVisibility(View.INVISIBLE);
        }


    }

    private void UnFriendAnExistingFriend()
    {
        FriendsRef.child(senderUserId).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserID).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                send_friend_request_Button.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                send_friend_request_Button.setText("Send Request");
                                                decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                                decline_friend_request_Button.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Log.d("tag1","IF Here ");
        Calendar calFordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordate.getTime());

        FriendsRef.child(senderUserId).child(receiverUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserID).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                FriendRequestRef.child(senderUserId).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    FriendRequestRef.child(receiverUserID).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        send_friend_request_Button.setEnabled(true);
                                                                                        CURRENT_STATE="friends";
                                                                                        send_friend_request_Button.setText("Unfriend");
                                                                                        decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                                                                        decline_friend_request_Button.setEnabled(false);
                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void CancelFriendRequest()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserID).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                send_friend_request_Button.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                send_friend_request_Button.setText("Send Request");
                                                decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                                decline_friend_request_Button.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void MaintainanceOfButton()
    {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChild(receiverUserID))
                        {
                            String request_type=snapshot.child(receiverUserID).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                CURRENT_STATE="request_sent";
                                send_friend_request_Button.setText("Cancel Request");
                                decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                decline_friend_request_Button.setEnabled(false);
                            }
                            else if(request_type.equals("received"))
                            {
                                CURRENT_STATE="request_received";
                                send_friend_request_Button.setText("Accept Request");
                                decline_friend_request_Button.setVisibility(View.VISIBLE);
                                decline_friend_request_Button.setEnabled(true);

                                decline_friend_request_Button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                            else
                            {
                                FriendsRef.child(senderUserId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                               if(snapshot.hasChild(receiverUserID))
                                               {
                                                   CURRENT_STATE="friends";
                                                   send_friend_request_Button.setText("Unfriend");
                                                   decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                                   decline_friend_request_Button.setEnabled(false);
                                               }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendFriendRequestToPerson()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserID).child(senderUserId).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                send_friend_request_Button.setEnabled(true);
                                                CURRENT_STATE="request_sent";
                                                send_friend_request_Button.setText("Cancel Request");
                                                decline_friend_request_Button.setVisibility(View.INVISIBLE);
                                                decline_friend_request_Button.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void InitialiseFields()
    {
        username = (TextView) findViewById(R.id.person_user_name);
        userCountry = (TextView) findViewById(R.id.person_country);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userProfName = (TextView) findViewById(R.id.person_full_name);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_relationship_status);
        userDob = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);
        send_friend_request_Button=(Button)findViewById(R.id.person_sent_friend_request_Button);
        decline_friend_request_Button=(Button)findViewById(R.id.person_decline_friend_request_Button);

        CURRENT_STATE="not_friends";
    }
}