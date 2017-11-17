package com.webege.profause.excelsdiary.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by Emmanuel Mensah on 8/22/2016.
 */
public class Measurement extends RealmObject implements Parcelable {
    private String name;
    private String value;
    private int unit;

    private static ArrayList<Measurement> tempFemale = new ArrayList<>();
    private static ArrayList<Measurement> tempMale = new ArrayList<>();
    private static String[] temp1 = {"Bust", "Across Back", "Sleeve Length", "Arm hole",
            "Arm size", "Shoulder to waist", "Tigh", "Top Length", "Trouser Length", "Claff"};
    private static String[] temp2 = {
            "Bust", "Hip", "High Hip", "Back waist Length", "Front waist Length",
            "Arm hole", "Arm size", "Slit Length", "Breast Dart", "Skirt Length",
            "Top Length", "Shoulder to waist"
            , "Sleeve Length"
    };

    protected Measurement(Parcel in) {
        name = in.readString();
        value = in.readString();
        unit=in.readInt();
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Measurement() {
    }

    public Measurement(String name, String value,int unit) {
        this.name = name;
        this.value = value;
        this.unit=unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(value);
        parcel.writeInt(unit);
    }



    public static ArrayList<Measurement> getTempMeasure(int gender) {
        if (gender == 1) {
            for (String s : temp1
                    ) {
                tempMale.add(new Measurement(s, "",1));
            }
            return tempMale;
        } else if (gender == 2) {
            for (String s : temp2
                    ) {
                tempFemale.add(new Measurement(s, "",1));
            }
            return tempFemale;
        }
        return null;
    }

}
