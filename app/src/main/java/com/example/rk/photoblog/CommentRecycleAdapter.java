package com.example.rk.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecycleAdapter extends RecyclerView.Adapter<CommentRecycleAdapter.ViewHolder> {


    public List<Comment> commentsList;

    public CommentRecycleAdapter(List<Comment> commentsList){

        this.commentsList = commentsList;

    }

    public Context context;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public CommentRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        context = parent.getContext();


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        return new CommentRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentRecycleAdapter.ViewHolder holder, int position) {

        final String currentuserid = firebaseAuth.getCurrentUser().getUid();

        final String blogpostId = commentsList.get(position).BlogPostId;

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMassage();
        holder.setComment_message(commentMessage);

        final String User_id = commentsList.get(position).getUserId();



//        firebaseFirestore.collection("Post/"+User_id+"/Comments").document(currentuserid)
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//
//                        if (documentSnapshot.exists()){
//
//                            String username = documentSnapshot.getString("User_id");
//                            holder.UsserData(username);
//
//                        }
//                    }
//                });
//
//        firebaseFirestore.collection("Post").document(User_id).collection("Comments").document(User_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if(task.isSuccessful()){
//
//                    DocumentSnapshot snapshot = task.getResult();
//
//                    if (snapshot.exists()) {
//
//                        String userName = task.getResult().getString("Name");
//
//
//                        holder.UsserData(userName);
//
//
//
//
//                    } else {
//
//                    }
//
//
//
//                }
//
//            }
//        });
//


    }

    @Override
    public int getItemCount() {
        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private CircleImageView user_commnet_image;
        private TextView comment_user_name;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }
//        private void UsserData(String name , String image){
//
//            comment_user_name = mView.findViewById(R.id.item_username);
//            user_commnet_image = mView.findViewById(R.id.circleImageView);
//
//            comment_user_name.setText(name);
//
//            RequestOptions placeholder = new RequestOptions();
//            placeholder.placeholder(R.drawable.profile);
//
//            Glide.with(context).applyDefaultRequestOptions(placeholder).load(image).into(user_commnet_image);
//        }

        public void UsserData(String userName) {

            comment_user_name = mView.findViewById(R.id.comment_username);
            comment_user_name.setText(userName);
        }
    }
}
