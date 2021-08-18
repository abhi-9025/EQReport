package com.example.abhi192001.lucid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private ImageButton SearchButton;
    private EditText SearchInputText;
    private RecyclerView SearchResultList;


    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);



        allUsersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar=(androidx.appcompat.widget.Toolbar)findViewById(R.id.find_friends_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        SearchResultList=(RecyclerView)findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton=(ImageButton)findViewById(R.id.Search_people_friends_button);
        SearchInputText=(EditText)findViewById(R.id.search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               String searchBoxInput= SearchInputText.getText().toString();
                SearchPeopleAndFriends(searchBoxInput);

            }
        });
    }
     //TODO 1  ---  find Friends Implementation Remaining
    private void SearchPeopleAndFriends(String searchBoxInput)
    {

        FirebaseRecyclerOptions<FindFriends>options=new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(allUsersDatabaseRef,FindFriends.class)
                .build();
        FirebaseRecyclerAdapter<FindFriends,FindFriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriends model)
            {
                holder.userfullname.setText(model.getFullname());
                holder.userStatus.setText(model.getStatus());


                Picasso.with(FindFriendsActivity.this).load(model.getProfileimage()).placeholder(R.drawable.profile).into(holder.profileImage);

                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v)
                     {
                         String visit_user_id=getRef(position).getKey();

                         Intent profileIntent=new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                         profileIntent.putExtra("visit_user_id",visit_user_id);
                         startActivity(profileIntent);
                     }
                 });

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout,parent,false);
                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        firebaseRecyclerAdapter.startListening();
        SearchResultList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView userfullname,userStatus;
        CircleImageView profileImage;

        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userfullname=(TextView)itemView.findViewById(R.id.all_users_profile__full_name);
            userStatus=(TextView)itemView.findViewById(R.id.all_users_profile__status);
            profileImage=(CircleImageView)itemView.findViewById(R.id.all_users_profile_image);
        }
    }
}