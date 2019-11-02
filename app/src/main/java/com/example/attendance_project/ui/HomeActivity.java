package com.example.attendance_project.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.attendance_project.R;
import com.example.attendance_project.announcements.FeedActivity;
import com.example.attendance_project.authentication.LoginActivity;
import com.example.attendance_project.authentication.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private NavigationView navigationView;
    ProgressDialog dialog;
    TextView userName,email;
    MenuItem signIn,signOut;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Menu nav_menu=navigationView.getMenu();
        signIn=nav_menu.findItem(R.id.nav_item3);
        signOut=nav_menu.findItem(R.id.nav_item4);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        user=new User(getApplicationContext());
        if (user.getmAuth().getCurrentUser() != null) {
            FirebaseUser firebaseUser = user.getmAuth().getCurrentUser();
            user.updateUI(firebaseUser);
            updateLoggedInNavigation();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                if(id==R.id.nav_item1){
                    //pass
                }
                else if(id==R.id.nav_item2){
                    startActivity(new Intent(HomeActivity.this, FeedActivity.class));
                }
                else if(id==R.id.nav_item3){
                    Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                    intent.putExtra("extra",true);
                    startActivityForResult(intent,123);
                }
                else if(id==R.id.nav_item4){
                    signOut();
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

    }
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==123 && resultCode==1){
            updateLoggedInNavigation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void updateLoggedInNavigation(){
        signIn.setVisible(false);
        signOut.setVisible(true);
        userName=findViewById(R.id.userNameId);
        email=findViewById(R.id.emailId);
        userName.setText(User.getUserName());
        email.setText(User.getUserEmail());
    }
    void updateLoggedOutNavigation(){
        signIn.setVisible(true);
        signOut.setVisible(false);
        userName=findViewById(R.id.userNameId);
        email=findViewById(R.id.emailId);
        userName.setText("");
        email.setText("");
    }
    void signOut(){
        // Firebase sign out
        dialog=new ProgressDialog(this);
        dialog.setTitle("Logging In");
        dialog.setMessage("Please wait...");
        dialog.show();
        dialog.setCancelable(false);
        user.getmAuth().signOut();
        // Google sign out
        user.getmGoogleSignInClient().signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        user.updateUI(null);
                        Toast.makeText(HomeActivity.this,"You are logged out...", Toast.LENGTH_SHORT).show();
                        updateLoggedOutNavigation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(HomeActivity.this,"Failed to Logout.. please Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
