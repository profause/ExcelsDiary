package com.webege.profause.excelsdiary.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class Client extends RealmObject implements Parcelable{
    @PrimaryKey
    private String name;
    private int gender;
    private String telephone;
    private String imageUri;
    private RealmList<Measurement> measurements;

    protected Client(Parcel in) {
        name = in.readString();
        gender = in.readInt();
        telephone = in.readString();
        imageUri = in.readString();
        dateAdded = in.readString();
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    private String dateAdded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public RealmList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(RealmList<Measurement> measurements) {
        this.measurements = measurements;
    }

    public Client(String name, String telephone, int gender, String imageUri,
                  String dateAdded) {
        this.setName(name);
        this.setGender(gender);
        this.setTelephone(telephone);
        this.setImageUri(imageUri);
        this.setDateAdded(dateAdded);
    }

    public Client() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(gender);
        parcel.writeString(telephone);
        parcel.writeString(imageUri);
        parcel.writeString(dateAdded);
    }
}
