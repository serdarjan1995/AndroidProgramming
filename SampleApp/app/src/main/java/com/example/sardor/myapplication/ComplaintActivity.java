package com.example.sardor.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Sardor on 18/04/2017.
 */

public class ComplaintActivity extends AppCompatActivity {
    private Button submit;
    private Button close;
    private Button showDb;
    private EditText header;
    private EditText description;
    private EditText personalId;
    private EditText phoneNo;
    private DatabaseHelper dbHelper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);
        final Context context = getApplicationContext();

        header=(EditText)findViewById(R.id.editTextHeader);
        description=(EditText)findViewById(R.id.editTextDescription);
        personalId=(EditText)findViewById(R.id.editTextPersonalId);
        phoneNo=(EditText)findViewById(R.id.editTextPhoneNo);

        dbHelper=new DatabaseHelper(this);

        submit=(Button)findViewById(R.id.buttonsubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList <String> arr = new ArrayList<String>();
                dbHelper.addComplaint( header.getText().toString(), description.getText().toString(),
                       Integer.parseInt(personalId.getText().toString()), phoneNo.getText().toString() );
                arr=dbHelper.getComplaint(dbHelper.getLastIndex());
                Toast.makeText(context, "User with id " + arr.get(3) + " has made complaint" + arr.get(1)
                        + ", that ID is " + arr.get(0), Toast.LENGTH_LONG).show();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,"nizamettin@ce.yildiz.edu.tr");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,arr.get(1));
                emailIntent.putExtra(Intent.EXTRA_TEXT, arr.get(2) + "\n\n\nGönderen: " + arr.get(3)
                        + "\nTel no: " + arr.get(4));
                if(emailIntent.resolveActivity(getPackageManager())!=null){
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }

            }
        });

        close=(Button)findViewById(R.id.buttonQuit);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        showDb=(Button)findViewById(R.id.buttonDbShow);
        showDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ComplaintActivity.this,DbShow.class);
                startActivity(i);
            }
        });

    }

}
