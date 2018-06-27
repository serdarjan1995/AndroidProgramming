package com.example.sardor.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sardor on 25/04/2017.
 */

public class DbShow extends AppCompatActivity{
    private Button close;
    private ListView lstView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> arrayList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_layout);
        dbHelper=new DatabaseHelper(this);

        arrayList=dbHelper.getAllComplaints();
        lstView=(ListView)findViewById(R.id.dbList);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        lstView.setAdapter(arrayAdapter);

        close=(Button)findViewById(R.id.button_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
