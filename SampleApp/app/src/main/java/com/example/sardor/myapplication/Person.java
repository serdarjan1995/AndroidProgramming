package com.example.sardor.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sardor on 21/03/2017.
 */

public class Person implements Parcelable{
    private String userName;
    private String passwd;
    private String name;
    private String surname;
    private String gender;

    public Person(String usrnm, String pswd, String name, String surname, String gender){
        userName=usrnm;
        passwd=pswd;
        this.name=name;
        this.surname=surname;
        this.gender=gender;
    }

    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPasswd(){
        return passwd;
    }
    public String getUserName(){
        return userName;
    }
    public boolean isWoman(){
        if(gender.equals("woman")){
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.userName,this.passwd,this.name,this.surname,this.gender
        });
    }
    public Person(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.userName = data[0];
        this.passwd = data[1];
        this.name = data[2];
        this.surname = data[3];
        this.gender = data[4];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
