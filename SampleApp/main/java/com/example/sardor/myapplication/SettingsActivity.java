package com.example.sardor.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sardor on 04/04/2017.
 */

public class SettingsActivity extends AppCompatActivity{
    private Button cancelButton;
    private Button loadToDefaultButton;
    private Button saveButton;
    private EditText highScoreEditText;
    private Spinner gameDifficultySpinner;
    private Spinner defaultLevelSpinner;
    private Spinner numberOfFriendsSpinner;
    private Spinner numberOfEnemiesSpinner;
    private ArrayAdapter<CharSequence> adapter;
    private ArrayAdapter<CharSequence> adapterGameDifficulty;
    SharedPreferences sharedPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Context context = getApplicationContext();

        sharedPref = getSharedPreferences(getString(R.string.sharedName),Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        highScoreEditText = (EditText) findViewById(R.id.editTextHighScore);
        highScoreEditText.setText("1234");

        gameDifficultySpinner = (Spinner) findViewById(R.id.spinnerGameDifficulty);
        adapterGameDifficulty = ArrayAdapter.createFromResource(
                this, R.array.difficulty, android.R.layout.simple_spinner_item);
        adapterGameDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameDifficultySpinner.setAdapter(adapterGameDifficulty);

        defaultLevelSpinner = (Spinner) findViewById(R.id.spinnerDefaultLevel);
        numberOfFriendsSpinner = (Spinner) findViewById(R.id.spinnerNumberOfFriends);
        numberOfEnemiesSpinner = (Spinner) findViewById(R.id.spinnerNumberOfEmenies);

        adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        defaultLevelSpinner.setAdapter(adapter);
        numberOfFriendsSpinner.setAdapter(adapter);
        numberOfEnemiesSpinner.setAdapter(adapter);

        cancelButton =(Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });

        loadToDefaultButton =(Button)findViewById(R.id.loadToDefaultButton);
        loadToDefaultButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                gameDifficultySpinner.setSelection(0);
                numberOfFriendsSpinner.setSelection(2);
                numberOfEnemiesSpinner.setSelection(7);
                defaultLevelSpinner.setSelection(0);
                highScoreEditText.setText("1234");
            }
        });

        saveButton =(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("gameDifficulty",gameDifficultySpinner.getSelectedItem().toString());
                editor.putString("highScore",highScoreEditText.getText().toString());
                editor.putString("numberOfFriends",numberOfFriendsSpinner.getSelectedItem().toString());
                editor.putString("numberOfEnemies",numberOfEnemiesSpinner.getSelectedItem().toString());
                editor.putString("defaultLevel",defaultLevelSpinner.getSelectedItem().toString());
                editor.commit();
            }
        });

        if(sharedPref.contains("highScore")){
            highScoreEditText.setText(sharedPref.getString("highScore","1234"));
        }

        if(sharedPref.contains("defaultLevel")){
            defaultLevelSpinner.setSelection(adapter.getPosition(sharedPref.getString("defaultLevel","Easy")));
        }

        if(sharedPref.contains("gameDifficulty")){
            gameDifficultySpinner.setSelection(adapter.getPosition(sharedPref.getString("gameDifficulty","1")));
        }

        if(sharedPref.contains("numberOfFriends")){
            numberOfFriendsSpinner.setSelection(adapter.getPosition(sharedPref.getString("numberOfFriends","3")));
        }

        if(sharedPref.contains("numberOfEnemies")){
            numberOfEnemiesSpinner.setSelection(adapter.getPosition(sharedPref.getString("numberOfEnemies","8")));
        }
    }
}
