package semesterproject.mobileprogramming.com.trackme;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public  class SettingsDialog extends DialogFragment implements View.OnClickListener{
    private  Button saveButton;
    private Button cancelButton;
    private EditText gpsFreq;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_settings_dialog, container, false);
        saveButton = (Button) v.findViewById(R.id.button_save_settings);
        cancelButton = (Button) v.findViewById(R.id.button_cancel_settings);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        gpsFreq = (EditText) v.findViewById(R.id.editText_gps_freq);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedName),Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if(sharedPref.contains("gpsFreq")){
            gpsFreq.setText(sharedPref.getString("gpsFreq","1"));
        }
        getDialog().setTitle("Set GPS Frequency");
        return v;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_save_settings){
            if(Integer.parseInt(gpsFreq.getText().toString())>100){
                Toast.makeText(getActivity(),"Please set value between 0-100",Toast.LENGTH_SHORT).show();
            }else{
                editor.putString("gpsFreq",gpsFreq.getText().toString());
                editor.commit();
                this.dismiss();
            }
        }
        else if (view.getId()==R.id.button_cancel_settings){
            this.dismiss();
        }
    }
}