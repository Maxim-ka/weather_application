package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Condition implements Parcelable {

    @SerializedName("text")
    @Expose
    private final String text;
    @SerializedName("icon")
    @Expose
    private final String icon;

    public Condition(String text, String icon) {
        this.text = text;
        this.icon = icon;
    }

    private Condition(Parcel in) {
        text = in.readString();
        icon = in.readString();        
    }

    public static final Creator<Condition> CREATOR = new Creator<Condition>() {
        @Override
        public Condition createFromParcel(Parcel in) {
            return new Condition(in);
        }

        @Override
        public Condition[] newArray(int size) {
            return new Condition[size];
        }
    };

    public String getText() {
        return text;
    }

    public String getIcon() {
        return icon;
    }    

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(icon);        
    }
}
