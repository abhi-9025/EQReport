package com.example.eqreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthquakeAdapter mAdapter;

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        EarthquakeAsyncTask Task=new EarthquakeAsyncTask();
        Task.execute(USGS_REQUEST_URL);

     //get the list from QueryUtils
       // ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();


        ListView earthquakesListView=(ListView)findViewById(R.id.list);

       mAdapter=new EarthquakeAdapter(this,new ArrayList<Earthquake>());

       earthquakesListView.setAdapter(mAdapter);

       earthquakesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               Earthquake currentEarthquake=mAdapter.getItem(position);

               Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

               Intent websiteIntent=new Intent(Intent.ACTION_VIEW,earthquakeUri);
               startActivity(websiteIntent);
           }
       });

    }

    private class EarthquakeAsyncTask extends AsyncTask<String,Void, List<Earthquake>>{

        @Override
        protected List<Earthquake> doInBackground(String... urls)
        {
           if (urls.length<1||urls[0]==null)
                return null;

           List<Earthquake>result=QueryUtils.fetchEarthquakeData(urls[0]);
           return result;
        }
        protected void onPostExecute(List<Earthquake>data)
        {
        mAdapter.clear();

        if(data!=null &&!data.isEmpty())
        {
            mAdapter.addAll(data);
        }
        }
    }

}