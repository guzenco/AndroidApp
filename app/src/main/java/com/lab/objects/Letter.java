package com.lab.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.lab.R;

public class Letter implements Parcelable {

    public static String null_letter = "*";
    public char l;
    public int p;
    public boolean v;

    public Letter(char l, int p, boolean v) {
        this.l = l;
        this.p = p;
        this.v = v;
    }

    public Letter() {
        this.l = '_';
        this.p = -1;
        this.v = false;
    }

    protected Letter(Parcel in) {
        l = (char) in.readInt();
        p = in.readInt();
        v = in.readByte() != 0;
    }

    public static final Creator<Letter> CREATOR = new Creator<Letter>() {
        @Override
        public Letter createFromParcel(Parcel in) {
            return new Letter(in);
        }

        @Override
        public Letter[] newArray(int size) {
            return new Letter[size];
        }
    };

    @Override
    public String toString() {
        return v ? Character.toString(l) : null_letter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt((int) l);
        parcel.writeInt(p);
        parcel.writeByte((byte) (v ? 1 : 0));
    }
}
