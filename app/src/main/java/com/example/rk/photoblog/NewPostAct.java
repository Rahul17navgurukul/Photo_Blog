package com.example.rk.photoblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;


public class NewPostAct extends AppCompatActivity {

//    Declare all stuff here

    private static final int MAX_LENGTH = 100;
    private Toolbar toolbar;
    private ImageView newpost_image;
    private EditText  newpost_desc;
    private Button newpost_btn;
    private ProgressDialog progressDialog;

    Uri newpost_imageUri;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    private Bitmap compressedImagefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

//         Initialize FirebaseAuth here
//        Initialize all other thigs here

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Post");

        newpost_image = findViewById(R.id.NewPost_Image);
        newpost_desc = findViewById(R.id.NewPost_Description);
        newpost_btn = findViewById(R.id.NewPost_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait a while");
        progressDialog.setCanceledOnTouchOutside(false);

//        Set onclickLstener on floting button

        newpost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//              Here i am creating one string and get the text from newpost_des

                final String descripiton = newpost_desc.getText().toString();

//              Set the condition for  descripton && Image
//              the condition is if my description and image field are not empty than start Uploding process
//                and show progress dialog

                if (!TextUtils.isEmpty(descripiton) && newpost_imageUri!=null){

                    progressDialog.show();


//                    here i creat firebase Storage Refrence for putting my image file

                    StorageReference filepath = storageReference.child("post_images").child("jpg");

                    filepath.putFile(newpost_imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();


                            if (task.isSuccessful()){

//                              here i compress image file because when i retriew my image file on my device it
//                                retriving late


                                File newimagefle = new File(newpost_imageUri.getPath());

                                try {
                                    compressedImagefile = new Compressor(NewPostAct.this)
                                            .setMaxHeight(200)
                                            .setMaxWidth(200)
                                            .setQuality(5)
                                            .compressToBitmap(newimagefle);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

//                                These solution i get from internet

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImagefile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                byte[] thumbdata = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_image/Thumbs").child("jpg")
                                        .putBytes(thumbdata);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downlaodThumbsUri = taskSnapshot.getDownloadUrl().toString();

//                                    when my file uploaded

                                        Map<String,Object> postMap = new HashMap<>();
                                        postMap.put("image_url",downloadUri);
//                                        postMap.put("thumbs_url",downlaodThumbsUri)
                                        postMap.put("description",descripiton);
                                        postMap.put("current_user",current_user_id);
                                        postMap.put("timestamp",FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

//                                                when my uploading task finshed it back to main  activity

                                                if (task.isSuccessful()){

                                                    Toast.makeText(NewPostAct.this,"Post was Added",Toast.LENGTH_LONG).show();
                                                    Intent mainact = new Intent(NewPostAct.this,MainActivity.class);
                                                    startActivity(mainact);


                                                }else {

                                                    progressDialog.dismiss();


                                                }

                                            }
                                        });



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });





                            }else{

                                progressDialog.dismiss();


                            }

                        }
                    });
                }

            }
        });

//        Here i setOnclickListener On Image View

        newpost_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//         when i open my gallery and select any picture it ask for croping image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .start(NewPostAct.this);


            }
        });
    }

// here i creat OnCtivity result method
//    By the help of android startActivityResult() method, we can get result from another activity.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                newpost_imageUri = result.getUri();

                newpost_image.setImageURI(newpost_imageUri);

//                isChanged = true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
