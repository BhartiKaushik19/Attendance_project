package com.example.attendance_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Firebase_input_clubs extends AppCompatActivity {



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_input_clubs);

        final EditText club_name,club_notification_year,club_venue,club_heading,club_details;
        Button club_submit_btn;
        Date club_date_of_event;
        final DatabaseReference databaseReference;
        final Member member;
        final long[] maxid = {0};


        club_name=findViewById(R.id.club_name);
        club_notification_year=findViewById(R.id.club_notification_years);
        club_venue=findViewById(R.id.club_venue);
        club_heading=findViewById(R.id.club_heading);
        club_details=findViewById(R.id.club_details);
        club_submit_btn=findViewById(R.id.club_submit_button);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member");
        member=new Member();



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid[0] =(dataSnapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        club_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                member.setClub_name(club_name.getText().toString());
                member.setClub_notification_year(club_notification_year.getText().toString());
                member.setClub_heading(club_heading.getText().toString());
                member.setClub_details(club_details.getText().toString());
                member.setClub_venue(club_venue.getText().toString());
                databaseReference.child(String.valueOf(maxid[0] +1)).setValue(member);
                Toast.makeText(Firebase_input_clubs.this, "Successful", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
