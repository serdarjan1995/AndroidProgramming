package com.example.sardor.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private Button exit;
    private EditText loginField;
    private EditText passwdField;
    private ArrayList <Person> personArrayList= new ArrayList<Person>();
    private SmsNotifyReceiver smsNotify;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNewPerson();
        loginField = (EditText) findViewById(R.id.userId);
        passwdField = (EditText) findViewById(R.id.passwd);

        login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            Context context = getApplicationContext();

            public void onClick(View view) {
                String userName = loginField.getEditableText().toString();
                String password = passwdField.getEditableText().toString();
                int loginResult = matchUsernamePassword(userName, password);
                switch (loginResult){
                    case -1: Toast.makeText(context, "Invalid username!", Toast.LENGTH_SHORT).show();
                            break;
                    case -2: Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT).show();
                            break;
                    default: Toast.makeText(context, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, SecondActivity.class);
                        i.putExtra("loggedInUser", personArrayList.get(loginResult));
                        if(userName.equals("admin")){
                            i.putExtra("usersArrayList", personArrayList);
                        }
                        startActivity(i);
                }
            }
        });

        exit = (Button) findViewById(R.id.buttonExit);
        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

    private void createNewPerson(){
        personArrayList.add(new Person("admin","1234","Sardor","Hazratov", "man"));
        personArrayList.add(new Person("user2","5678","John","Steel", "man"));
        personArrayList.add(new Person("user3","9101","Ray","Samrock", "man"));
        personArrayList.add(new Person("user4","4321","Alice","Fraught", "woman"));
    }

    private int matchUsernamePassword(String username, String password) {
        for (int i = 0; i < personArrayList.size(); i++) {
            if (username.equals( personArrayList.get(i).getUserName()) ) {
                if (password.equals( personArrayList.get(i).getPasswd()) ) {
                    return i;
                }
                return -2;
            }
        }
        return -1;
    }



}