package com.example.abhi192001.lucid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity
{

    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private RecyclerView CommentsList;
    private DatabaseReference UsersRef, PostsRef;

    private String Post_Key,current_user_id;
    private FirebaseAuth mAuth;

    private FirebaseRecyclerAdapter<Comments,CommentsActivity.CommentsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        Post_Key=getIntent().getExtras().get("PostKey").toString();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");


        PostCommentButton=(ImageButton)findViewById(R.id.post_comment_button);
        CommentsList=(RecyclerView)findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText=(EditText)findViewById(R.id.comment_input);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            String userName=snapshot.child("username").getValue().toString();

                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments> firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(PostsRef,Comments.class)
                .build();
       firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(firebaseRecyclerOptions) {
           @Override
           protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model)
           {
               holder.setComments(model);
           }

           @NonNull
           @Override
           public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
           {
              View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
              return new CommentsViewHolder(view);
           }
       };
       firebaseRecyclerAdapter.startListening();
        CommentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        TextView  myComment,myUsername,myDate,myTime;

        public CommentsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            myDate = itemView.findViewById(R.id.comment_date);
            myTime= itemView.findViewById(R.id.comment_time);
            myComment=itemView.findViewById(R.id.comment_text);
            myUsername=itemView.findViewById(R.id.comment_username);


        }
            public void setComments(Comments comments)
            {
               String username=comments.getUsername();
                 myUsername.setText(username);
               String comment=comments.getComment();
               myComment.setText(comment);
               String Date=comments.getDate();
               myDate.setText(Date);
               String Time=comments.getTime();
               myTime.setText(Time);



            }

        }

    private void ValidateComment(String userName)
    {
        String commentText=CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please Write Something...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calFordate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
           final String saveCurrentDate=currentDate.format(calFordate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
          final String saveCurrentTime=currentTime.format(calFordate.getTime());

          final String RandomKey=current_user_id+saveCurrentDate+saveCurrentTime;

          HashMap commentsMap=new HashMap();
            commentsMap.put("uid",current_user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",userName);

            PostsRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CommentsActivity.this, "You have Commented Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(CommentsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
}