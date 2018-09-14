package com.example.rk.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

//    Declare an instance of FirebaseAuth
//    Declare an inteance of Firebase firestore
//    Declare All otherThings here


    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private FloatingActionButton add_post_btn;
    private String current_user_id;
    private FirebaseFirestore firebaseFirestore;
    private Home_Frament home_frament;
    private Notification_Fragment notification_fragment;
    private Account_Fragment account_fragment;
    private BottomNavigationView bottomNavigationView;

    private SwipeRefreshLayout swipeContainer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initialize FirebaseAuth here
//        Initialize FIrebaseFirestore here

//        Initialize all other Things here

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PhotoBlog");

            add_post_btn = findViewById(R.id.add_post_btn);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);


//          Initialze Fragment here

            home_frament = new Home_Frament();
            notification_fragment = new Notification_Fragment();
            account_fragment = new Account_Fragment();
            replaceFragment(home_frament);

//            Here i am seting the three fragment in bottom of main activity

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottom_home:
                            replaceFragment(home_frament);

                            return true;


                        case R.id.bott_account:
                            replaceFragment(account_fragment);
                            Intent m = new Intent(MainActivity.this,Setup_Page.class);
                            startActivity(m);


                            return true;
                    }


                    return false;
                }
            });


//          Here I am handling the floting button and set onclick

            add_post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent new_post_Act = new Intent(MainActivity.this, NewPostAct.class);
                    startActivity(new_post_Act);
                }
            });




    }

//    When My Application start it automatic check user login or not
//    if user is not login it send to Log in page

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

            SendToLogin();
        }else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("user").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

//                    if (task.isSuccessful()){
//
//                        if (!task.getResult().exists()){
//
//                            Intent setup_page = new Intent(MainActivity.this,Setup_Page.class);
//                            startActivity(setup_page);
//
//                        }else{
//
//                            String erromsg = task.getException().getMessage();
//                            Toast.makeText(MainActivity.this,"Error" + erromsg,Toast.LENGTH_LONG).show();
//
//
//
//                        }
//
//
//                    }

                }
            });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

// When Menu item Select it do someaction

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.tool_logout:

//           Log Out method

                logout();

                return true;

            case R.id.setting:
                Intent setup_page = new Intent(MainActivity.this,Setup_Page.class);
                startActivity(setup_page);


            default:
                return false;
        }


    }

//   Log out method here

    private void logout() {
        mAuth.signOut();

//      Creat another for Sending the user to Login page

        SendToLogin();
    }

    private void SendToLogin() {
        Intent intent = new Intent(MainActivity.this,Page_login.class);
        startActivity(intent);
    }

//   Here i replce the fragment because i want to make fragment clickable with transaction

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }
}
