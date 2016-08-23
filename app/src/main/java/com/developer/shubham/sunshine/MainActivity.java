package com.developer.shubham.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mLocation;
    private boolean mMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        mMetric = Utility.isMetric(this);

        setContentView(R.layout.main);
        if(findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!mTwoPane);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_map){
            openPrefferedLocationinMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPrefferedLocationinMap() {
        //SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //String loc=pref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String loc = Utility.getPreferredLocation(this);

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",loc)
                .build();

        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(geoLocation);
        if(i.resolveActivity(getPackageManager())!=null) {
            startActivity(i);
        }
        else {
            Log.d(this.getClass().getSimpleName(),"Couldn't call "+ loc + ", no recieving apps installed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        boolean metric = Utility.isMetric(this);

        if(location != null && !location.equals(mLocation) || mMetric != metric) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df=(DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null != df){
                df.onLocationChanged(location);
            }
            mLocation = location;
            mMetric = metric;
        }
    }

    @Override
        public void onItemSelected(Uri contentUri) {
            if (mTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                Bundle args = new Bundle();
                args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                            .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                            .commit();
            } else {
                Intent intent = new Intent(this, DetailActivity.class)
                                .setData(contentUri);
                startActivity(intent);
            }
    }
}
