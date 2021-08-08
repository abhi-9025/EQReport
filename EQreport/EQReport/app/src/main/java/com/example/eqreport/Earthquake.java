package com.example.eqreport;

public class Earthquake {


    //Variables to hold the data
   // private String mMagnitude;
    private double mMagnitude;
    private String mLocation;
    //private String mDate;
    private long mTimeInMilliseconds;

    private String mUrl;


    public Earthquake(double mMagnitude, String mLocation,long TimeInMilliseconds,String url ) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        //this.mDate = mDate;
        this.mTimeInMilliseconds=TimeInMilliseconds;
        this.mUrl=url;
    }
    // Getters as Variables are private

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

//    public String getDate() {
//        return mDate;
//    }
    public String getUrl()
    {
        return mUrl;
    }

    public  long getTimeInMilliseconds()
    {
        return mTimeInMilliseconds;
    }
}
