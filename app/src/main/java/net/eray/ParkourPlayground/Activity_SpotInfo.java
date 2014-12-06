package net.eray.ParkourPlayground;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Activity_SpotInfo extends ActionBarActivity {

    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    public Location location;
    private ParseQuery query;
    private ParseImageView ImageView;
    private ProgressBar ImageViewProgress;
    private TextView title, description, uploaderID;
    private String objID;
    private ViewPager mPager;
    private CustomPagerAdapter mPagerAdapter;
    public LatLng latLng;
    public Marker marker;
    private Toolbar toolbar;
    private int size;
    private RelativeLayout uploaderRel;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__spot_info);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        findViewsbyId();

        uploaderRel.bringToFront();

        Intent mIntent = getIntent();

        objID = mIntent.getStringExtra("objID");
        getSupportActionBar().setTitle(mIntent.getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        description.setText(mIntent.getStringExtra("description"));
        uploaderID.setText(mIntent.getStringExtra("uploader"));
        latLng = new LatLng(mIntent.getDoubleExtra("lat", 0), mIntent.getDoubleExtra("lng", 0));
        size = mIntent.getIntExtra("size", 0);

        setUpMapIfNeeded();
        //findImage();

        mPagerAdapter = new CustomPagerAdapter(size, objID);
        mPager.setOffscreenPageLimit(3);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mPager.setAdapter(mPagerAdapter);

    }

    private void findViewsbyId() {
        scrollView = (ScrollView) findViewById(R.id.infoScroll);
        uploaderRel = (RelativeLayout) findViewById(R.id.uploaderRel);
        title = (TextView) findViewById(R.id.title);
        uploaderID = (TextView) findViewById(R.id.uploaderID);
        description = (TextView) findViewById(R.id.descriptionDetail);
        mPager = (ViewPager) findViewById(R.id.view);
    }

    private void setUpMapIfNeeded() {
        /*
        Do a null check to confirm that the map is not already instantiated
         */
        if (map == null) {
            //Try to obtain the map from the SupportMapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.detailMap);

            map = supportMapFragment.getExtendedMap();
            //Check if it was successful
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //Enable my location
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        //Show the current marker location in maps
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in
        map.animateCamera(CameraUpdateFactory.zoomTo(13));

        marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_place)));

    }

    /**
     * A placeholder fragment containing a simple view. This fragment
     * would include your content.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ad, container, false);
            return rootView;
        }
    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            super.onPause();
            mAdView.pause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            mAdView.resume();
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            super.onDestroy();
            mAdView.destroy();
        }

    }

    public class CustomPagerAdapter extends PagerAdapter {
        View page;
        int count;
        String objID;


        public CustomPagerAdapter(int count, String objID) {
            super();
            this.count = count;
            this.objID = objID;
            //Might not be needed
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            //super.destroyItem(container, position, object);
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int pos) {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            page = inflater.inflate(R.layout.album_frag, container, false);
            ImageView = (ParseImageView) page.findViewById(R.id.albumPager);
            ImageViewProgress = (ProgressBar) page.findViewById(R.id.progressBar2);

            fetchImages fI = new fetchImages(ImageView, objID, ImageViewProgress);
            fI.execute(pos);

            container.addView(page);
            return page;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Activity_SpotInfo.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
