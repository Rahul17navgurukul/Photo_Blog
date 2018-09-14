package com.example.rk.photoblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment_Section extends AppCompatActivity {

    private EditText comment_et;
    private ImageView coment_btn;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private RecyclerView comment_list;
    private CommentRecycleAdapter commentsRecyclerAdapter;
    private List<Comment> commentsList;
    private String blog_post_id;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment__section);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");



        comment_et = findViewById(R.id.comment_et);
        coment_btn = findViewById(R.id.comment_post);
        comment_list = findViewById(R.id.comment_list);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting your comment");
        progressDialog.setMessage("Please Wait");

        blog_post_id = getIntent().getStringExtra("blog_post_id");


        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentRecycleAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);




        firebaseFirestore.collection("Post/" + blog_post_id + "/Comments")
                .addSnapshotListener(Comment_Section.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String commentId = doc.getDocument().getId();
                        Comment comments = doc.getDocument().toObject(Comment.class);
                        commentsList.add(comments);
                        commentsRecyclerAdapter.notifyDataSetChanged();


                    }

                }
            }
        });

        coment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment_msg = comment_et.getText().toString();

                progressDialog.show();

                if (!comment_msg.isEmpty()){

                    Map<String,Object> current_map = new HashMap<>();
                    current_map.put("Massage",comment_msg);
                    current_map.put("User_id",current_user_id);
                    current_map.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Post/" + blog_post_id + "/Comments").add(current_map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()){

                                Toast.makeText(Comment_Section.this,"Error" +
                                        task.getException(),Toast.LENGTH_LONG).show();


                            }else {
                                comment_et.setText("");
//                                Intent main = new Intent(Comment_Section.this,MainActivity.class);
//                                startActivity(main);
                                progressDialog.dismiss();

                            }

                        }
                    });

                }


            }
        });



    }
}
