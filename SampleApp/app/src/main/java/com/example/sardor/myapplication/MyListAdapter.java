package com.example.sardor.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sardor on 28/03/2017.
 */

public class MyListAdapter extends BaseAdapter {
    private LayoutInflater bInflater;
    private List<Person> bPersonList;

    public MyListAdapter(Activity activity, List <Person> usersList){
        bInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bPersonList = usersList;
    }

    public MyListAdapter(Activity activity, Person user){
        bInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bPersonList=new ArrayList<Person>();
        bPersonList.add(user);
    }

    public int getCount(){
        return bPersonList.size();
    }
    public Person getItem(int position){
        return bPersonList.get(position);
    }
    public long getItemId(int i){return 1;}

    public View getView(int position, View convertView, ViewGroup parent){
        View rowView;
        rowView=bInflater.inflate(R.layout.row_view,null);
        TextView textView = (TextView)rowView.findViewById(R.id.namesurname);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.picture);
        Person person = bPersonList.get(position);
        textView.setText(person.getName()+ " " +person.getSurname());
        if(person.isWoman()){
            imageView.setImageResource(R.drawable.woman);
        }
        else{
            imageView.setImageResource(R.drawable.man);
        }
        return rowView;
    }
}
