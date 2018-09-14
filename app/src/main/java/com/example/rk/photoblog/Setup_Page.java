package com.example.rk.photoblog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup_Page extends AppCompatActivity {

    private Toolbar setup_toolbar;
    private CircleImageView setupimage;
    Uri mainimageuri = null;
    private Button setupbtn;
    private EditText setupname;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private String user_id;
    private boolean isChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup__page);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();


        setup_toolbar = findViewById(R.id.setup_tolbar);
        setSupportActionBar(setup_toolbar);
        getSupportActionBar().setTitle("Account Setup");
        setup_toolbar.setNavigationIcon(R.mipmap.backbtn);

        setupname = findViewById(R.id.setup_name);
        setupbtn = findViewById(R.id.setup_btn);
        setupimage = findViewById(R.id.setupimage);
        progressDialog = new ProgressDialog(this);

        progressDialog.show();
        progressDialog.setMessage("Please wait");
        setupbtn.setEnabled(false);

        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        String name = task.getResult().getString("Name");
                        String image = task.getResult().getString("Image");

                        setupname.setText(name);

                        mainimageuri = Uri.parse(image);

//                        RequestOptions placeholderrequestOption = new RequestOptions();
//                        placeholderrequestOption.placeholder(R.drawable.profile);

//                        Glide.with(Setup_Page.this).setDefaultRequestOptions(placeholderrequestOption).load(image).into(setupimage);
                        Glide.with(Setup_Page.this).load(image).into(setupimage);

                        Toast.makeText(Setup_Page.this,"Data Exists",Toast.LENGTH_LONG).show();


                    }

                }
                else {

                    String retrievError = task.getException().getMessage();
                    Toast.makeText(Setup_Page.this,"Firestore Retrieving Error"+retrievError,Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
                setupbtn.setEnabled(true);

            }
        });

        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Please wait the file is uploading");
                progressDialog.setTitle("Uploading");
                progressDialog.setCanceledOnTouchOutside(false);


                final String Username = setupname.getText().toString();
                if (!TextUtils.isEmpty(Username) && mainimageuri != null) {
                    progressDialog.show();

                     if (isChanged) {


                           user_id = firebaseAuth.getCurrentUser().getUid();
                           StorageReference image_path = mStorageRef.child("Profle_Images").child(user_id + ".jpg");
                           image_path.putFile(mainimageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                   if (task.isSuccessful()) {

//                                       Here i creat method

                                       StroreFirestre(task, Username);

                                   } else {

                                       String errormsg = task.getException().getMessage();

                                       Toast.makeText(Setup_Page.this, "Image Error :" + errormsg, Toast.LENGTH_LONG).show();

                                       progressDialog.dismiss();
                                   }


                               }
                           });


                     }else {

                         StroreFirestre(null, Username);

                }

               }
            }
        });

        setupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    if (ContextCompat.checkSelfPermission(Setup_Page.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(Setup_Page.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Setup_Page.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


                    }

                    else {
//                      Toast.makeText(Setup_Page.this,"You Already have Permission",Toast.LENGTH_LONG).show()
                        BringImagePicker();
                    }

                }
                else {

                    BringImagePicker();

                }


            }
        });


        setup_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Setup_Page.this,MainActivity.class);
                startActivity(i);

            }
        });




    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Setup_Page.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    private void StroreFirestre(@NonNull Task<UploadTask.TaskSnapshot> task, String Username) {

        Uri downloadUri;

        if (task!=null){

            downloadUri = task.getResult().getDownloadUrl();
        }else {

            downloadUri = mainimageuri;
        }


        Map<String,String> usermap = new HashMap<>();
        usermap.put("Name",Username);
        usermap.put("Image",downloadUri.toString());

        firebaseFirestore.collection("User").document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(Setup_Page.this,"The user Setting are Update" ,Toast.LENGTH_LONG).show();

                    Intent Main = new Intent(Setup_Page.this,MainActivity.class);
                    startActivity(Main);

                }
                else {
                    String errormsg = task.getException().getMessage();

                    Toast.makeText(Setup_Page.this,"Frestore Error :" + errormsg,Toast.LENGTH_LONG).show();


                }
                progressDialog.dismiss();

            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(Setup_Page.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainimageuri = result.getUri();

                setupimage.setImageURI(mainimageuri);
                isChanged = true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
