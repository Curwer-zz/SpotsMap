package net.eray.ParkourPlayground;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import io.codetail.animation.Animator;
import io.codetail.animation.ViewAnimationUtils;

import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.eray.ParkourPlayground.camera.ActivityCamera;
import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;


public class MapActivity extends ActionBarActivity {

    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private Marker spotMark;
    private ParseQuery query;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private GPSTracker tracker;
    private Location location;
    private TextView descText, uploaderText, markerTitle;
    private FloatingActionButton mFab_cam, mFab_location;
    private LatLng latLng;
    private HashMap<Marker, ParseObject> infoIndex = new HashMap<Marker, ParseObject>();
    private ViewPager mPager;
    //private CustomPagerAdapter mPagerAdapter;
    private ParseImageView ImageView;
    private ProgressBar ImageViewProgress;
    private RelativeLayout logoutRel, refrechRel;
    private View view;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;

    ParseUser currentUser = ParseUser.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        toolbar.bringToFront();
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }
        getSupportActionBar().setTitle(R.string.toolbar_title);

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
               toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        findViewsById();

        setUpMapIfNeeded();

        getSpotsQuery();

        //map.setClustering(new ClusteringSettings().enabled(true).addMarkersDynamically(true).clusterSize(2));
        map.setClustering(new ClusteringSettings().clusterSize(10).enabled(true));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                markerTitle.setText(infoIndex.get(marker).getString("title"));
                uploaderText.setText(infoIndex.get(marker).getString("uploader"));
                descText.setText(infoIndex.get(marker).getString("description"));

                Intent mIntent = new Intent(MapActivity.this, Activity_SpotInfo.class);
                mIntent.putExtra("title", infoIndex.get(marker).getString("title"));
                mIntent.putExtra("lat", marker.getPosition().latitude);
                mIntent.putExtra("lng", marker.getPosition().longitude);
                mIntent.putExtra("description", infoIndex.get(marker).getString("description"));
                mIntent.putExtra("objID", infoIndex.get(marker).getObjectId());
                mIntent.putExtra("uploader", infoIndex.get(marker).getString("uploader"));
                mIntent.putExtra("size", infoIndex.get(marker).getInt("size"));


                startActivity(mIntent);

            }
        });

        mFab_cam.setColor(getResources().getColor(R.color.FloatingActionBarColor_4CAF50));
        mFab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), ActivityCamera.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
            }
        });

        mFab_location.setColor(getResources().getColor(R.color.FloatingActionBarColor_FFFFFF));
        mFab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               tracker = new GPSTracker(MapActivity.this);
               latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
               map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        logoutRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MapActivity.this);
                builder1.setMessage(R.string.logout+"?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                currentUser = null;
                                ParseUser.logOut();
                                dialog.cancel();
                                MapActivity.this.finish();
                                Intent mIntent = new Intent(getApplicationContext(), Activity_login.class);
                                startActivity(mIntent);
                            }
                        });
                builder1.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog skip = builder1.create();
                skip.show();
            }
        });

        refrechRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpotsQuery();
            }
        });



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    private void startAnimation() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int width = display.widthPixels - 172;
        int height = display.heightPixels - 250;

        Display display1 = getWindowManager().getDefaultDisplay();

        int x = display1.getWidth();

        int y = display1.getHeight();

        int cx = Math.round(mFab_cam.getX()+100);
        int cy = Math.round(mFab_cam.getY()+100);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(x, y);
        int startRadius = Math.max(75,75);
        Animator animator = (Animator)
                ViewAnimationUtils.createCircularReveal(view, width, height, startRadius, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(500);

        if(Animator.LOLLIPOP){
            android.animation.Animator a = animator.getNativeAnimator();
        }else{
            ObjectAnimator a = (ObjectAnimator)
                    animator.getSupportAnimator();
        }
        animator.start();
    }


    public void findViewsById() {
        view = (View) findViewById(R.id.sliding_layout);
        mPager = (ViewPager) findViewById(R.id.mapPagerAlbum);
        mFab_location = (FloatingActionButton) findViewById(R.id.fabbutton_location);
        mFab_cam = (FloatingActionButton) findViewById(R.id.fabbutton_cam);
        logoutRel = (RelativeLayout) findViewById(R.id.logoutRel);
        refrechRel = (RelativeLayout) findViewById(R.id.refrechRel);
        descText = (TextView) findViewById(R.id.decsripText);
        uploaderText = (TextView) findViewById(R.id.uploader);
        markerTitle = (TextView )findViewById(R.id.marker_titlev2);
        startAnimation();
    }

    private void setUpMapIfNeeded() {
        /*
        Do a null check to confirm that the map is not already instantiated
         */
        if(map == null) {
            //Try to obtain the map from the SupportMapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapfrag);

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
        //Get LocationManager object from System Service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Setting up the gps tracker, in order to find the current position of the user.
        tracker = new GPSTracker(this);
        if (tracker.canGetLocation()) {
            if (tracker.isGPSEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (tracker.isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //Get Latitude
            double latitude = tracker.getLatitude();
            //Get Longitude
            double Longitude = tracker.getLongitude();
            //Create a LatLng object for current location
            LatLng latlng = new LatLng(latitude, Longitude);
            //Show the current location in maps
            map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            //Zoom in
            map.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }
    //This will pause the gps when the user presses the home key or hit the back key.
    @Override
    protected void onPause() {
        super.onPause();
        // TODO Auto-generated method stub
        tracker.stopUsingGPS();
    }
    //Run a query against Parse.com, Map is the class object, we get the two geopoints (long & lat), and a title for the spot.
    //This will loop the size object we get from Parse.com, and add a new marker for every loop it dose
    public void getSpotsQuery() {
        map.clear();
        map.getProjection();
        ParseQuery query = ParseQuery.getQuery("Map");
        query.whereEqualTo("validate", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        String title = list.get(i).getString("title");
                        double geo1Lang = list.get(i).getParseGeoPoint("geopoint").getLatitude();
                        double geo2Long = list.get(i).getParseGeoPoint("geopoint").getLongitude();
                        spotMark = map.addMarker(new MarkerOptions().position(new LatLng(geo1Lang, geo2Long)).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_place)));
                        infoIndex.put(spotMark, list.get(i));
                    }
                } else {

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getSpotsQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

