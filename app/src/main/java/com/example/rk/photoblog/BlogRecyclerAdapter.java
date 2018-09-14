package com.example.rk.photoblog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.print.PageRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;

    Context context;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;

    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blg_list_item,parent,false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String currentuserid = firebaseAuth.getCurrentUser().getUid();

        final String blogpostId = blog_list.get(position).BlogPostId;

        String desc_data = blog_list.get(position).getDescription();
        holder.setDescText(desc_data);

        final String blogImage = blog_list.get(position).getImage_url();
        holder.blogpostImage(blogImage);



        final String user_id = blog_list.get(position).getCurrent_user();
        //User Data will be retrieved here...
        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    DocumentSnapshot snapshot = task.getResult();

                    if (snapshot.exists()) {

                        String userName = task.getResult().getString("Name");
                        String userImage = task.getResult().getString("Image");

                        holder.UsserData(userName, userImage);


                    } else {

                    }


                }

            }
        });

        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = android.text.format.DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
            holder.datetime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

//        long millisecond = blog_list.get(position).getTimestamp().getTime();
//        String dateString = android.text.format.DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
//        holder.datetime(dateString);


        if (user_id.equals(currentuserid)){

            holder.deletpost.setEnabled(true);
            holder.deletpost.setVisibility(View.VISIBLE);

        }


        firebaseFirestore.collection("Post/"+blogpostId+"/Likes").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();
                    holder.update_like_count(count);

                }else {

                    holder.update_like_count(0);


                }

            }
        });



        //getlikes

        firebaseFirestore.collection("Post/"+blogpostId+"/Likes").document(currentuserid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()){

                    holder.postlike.setImageDrawable(context.getDrawable(R.mipmap.pressliked));
                }else {
                    holder.postlike.setImageDrawable(context.getDrawable(R.mipmap.like));
                }
            }
        });


        ///Like feature

        holder.postlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firebaseFirestore.collection("Post/"+blogpostId+"/Likes").document(currentuserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){

                            Map<String , Object> likesMap = new HashMap<>();

                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Post/"+blogpostId+"/Likes").document(currentuserid).set(likesMap);

                        }else {

                            firebaseFirestore.collection("Post/"+blogpostId+"/Likes").document(currentuserid).delete();

                        }

                    }
                });

            }
        });


        /// Comment section

        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent commentsection = new Intent(context,Comment_Section.class);
                commentsection.putExtra("blog_post_id",blogpostId);
                context.startActivity(commentsection);

            }
        });

        ///Delet section

        holder.deletpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Post").document(blogpostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        blog_list.remove(position);


                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView descView;
        private ImageView blogmainImage;
        private TextView blog_user_name;
        private CircleImageView currentuser;
        private TextView blogdate;
        private ImageView postlike;
        private ImageView postliked_red;
        private ImageView share;

        private TextView likeCount;
        private ImageView commentbtn;

        private Button deletpost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            postlike = mView.findViewById(R.id.likebtn);
            postliked_red = mView.findViewById(R.id.post_liked);
            likeCount = mView.findViewById(R.id.like_text);

            commentbtn = mView.findViewById(R.id.comment_btn);

            deletpost = mView.findViewById(R.id.deletbtn);

            share = mView.findViewById(R.id.imageView4);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Photo Blog");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "If you want to install this applicaton than visit here https://drive.google.com/open?id=1cJ279GeEfZT7wd8CQp0gCx4TOVIwifWH ");
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });

        }

        public void setDescText (String text){

            descView = mView.findViewById(R.id.item_decription);
            descView.setText(text);
        }

        public void blogpostImage(String downlaodImage){

            blogmainImage = mView.findViewById(R.id.blogmainImage);

            RequestOptions placeholder = new RequestOptions();
            placeholder.placeholder(R.mipmap.add_btn);

            Glide.with(context).applyDefaultRequestOptions(placeholder).load(downlaodImage).into(blogmainImage);

        }


        private void UsserData(String name , String image){

            blog_user_name = mView.findViewById(R.id.item_username);
            currentuser = mView.findViewById(R.id.circleImageView);

            blog_user_name.setText(name);

            RequestOptions placeholder = new RequestOptions();
            placeholder.placeholder(R.drawable.profile);

            Glide.with(context).applyDefaultRequestOptions(placeholder).load(image).into(currentuser);

//            Glide.with(context).load(image).into(currentuser);
        }

        public void datetime(String date){

            blogdate = mView.findViewById(R.id.item_date);
            blogdate.setText(date);
        }

        public void update_like_count(int count){

            likeCount = mView.findViewById(R.id.like_text);
            likeCount.setText(count + " Likes");

        }


    }

}
