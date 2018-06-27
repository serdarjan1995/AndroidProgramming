package com.example.sardor.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sardor on 21/03/2017.
 */

public class SecondActivity extends AppCompatActivity {

    private Person loggedInUser;
    private ArrayList <Person> usersArrayList= new ArrayList <Person>();
    private ListView listView;
    private MyListAdapter myListAdapter;
    private TextView helloTextView;
    private TextView textViewForUserType;
    private Button logoutButton;
    private Button goToSettingsButton;
    private Button complaintButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle extras = getIntent().getExtras();
        final Context context = getApplicationContext();
        loggedInUser=extras.getParcelable("loggedInUser");

        helloTextView = (TextView) findViewById(R.id.helloText);
        helloTextView.setText("Hello " + loggedInUser.getName() + " " + loggedInUser.getSurname());

        textViewForUserType = (TextView) findViewById(R.id.textViewForAdmin);
        listView = (ListView)findViewById(R.id.usersList);

        usersArrayList = extras.getParcelableArrayList("usersArrayList");
        if(usersArrayList!=null){
            textViewForUserType.setText("ADMIN");
            myListAdapter = new MyListAdapter(this,usersArrayList);
            listView.setAdapter(myListAdapter);
        }
        else {
            textViewForUserType.setText("USER");
            myListAdapter=new MyListAdapter(this,loggedInUser);
            listView.setAdapter(myListAdapter);
        }

        logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(context, "Bye " + loggedInUser.getName() + " " + loggedInUser.getSurname() , Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        goToSettingsButton = (Button) findViewById(R.id.goToSettingsButton);
        goToSettingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(SecondActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        complaintButton = (Button) findViewById(R.id.buttonComplaint);
        complaintButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(SecondActivity.this, ComplaintActivity.class);
                startActivity(i);
            }
        });


    }

}
