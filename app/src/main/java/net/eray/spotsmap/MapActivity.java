package net.eray.spotsmap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.eray.spotsmap.camera.ActivityCamera;
import net.eray.spotsmap.floatingActionButton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;


public class MapActivity extends FragmentActivity {

    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private Marker spotMark;
    private ParseQuery query;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private GPSTracker tracker;
    private Location location;
    private ParseImageView parseImageView;
    private TextView descText, uploaderText, markerTitle;
    private FloatingActionButton mFab_cam, mFab_location;
    private RelativeLayout actionBarRel;
    private LatLng latLng;
    private HashMap<Marker, ParseObject> infoIndex = new HashMap<Marker, ParseObject>();
    private HashMap<Integer, ParseFile> albumIndex = new HashMap<Integer, ParseFile>();
    private ViewPager mPager;
    private CustomPagerAdapter mPagerAdapter;
    private ParseImageView ImageView;
    private ProgressBar ImageViewProgress;
    private RelativeLayout logoutRel, refrechRel;

    ParseUser currentUser = ParseUser.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUpMapIfNeeded();

        findViewsById();

        getSpotsQuery();

        actionBarRel.bringToFront();


        //map.setClustering(new ClusteringSettings().enabled(true).addMarkersDynamically(true).clusterSize(2));
        map.setClustering(new ClusteringSettings().clusterSize(10).enabled(true));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                    markerTitle.setText(infoIndex.get(marker).getString("title"));
                    uploaderText.setText(infoIndex.get(marker).getString("uploader"));
                    descText.setText(infoIndex.get(marker).getString("description"));

                    query = ParseQuery.getQuery("ImageFiles");
                    final ParseObject obj = ParseObject.createWithoutData("Map", infoIndex.get(marker).getObjectId());
                    query.whereEqualTo("geoPosition", obj);
                    query.countInBackground(new CountCallback() {
                        @Override
                        public void done(int count, ParseException e) {
                            mPagerAdapter = new CustomPagerAdapter(count, obj.getObjectId());
                            mPager.setAdapter(mPagerAdapter);
                        }
                    });
                    /*query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            ParseFile file = (ParseFile) list.get(0).getParseFile("image");
                            parseImageView.setParseFile(file);
                            parseImageView.loadInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if (e != null) {

                                    }
                                }
                            });
                        }
                    });*/

                }
        });

        mPagerAdapter = new CustomPagerAdapter(0, "");
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

        mFab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), ActivityCamera.class);
                startActivity(mIntent);
            }
        });

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
                builder1.setMessage("Are you sure you want to logout?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
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
                builder1.setNegativeButton("No",
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
                map.clear();
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


    public void findViewsById() {
        mPager = (ViewPager) findViewById(R.id.mapPagerAlbum);
        mFab_location = (FloatingActionButton) findViewById(R.id.fabbutton_location);
        mFab_cam = (FloatingActionButton) findViewById(R.id.fabbutton_cam);
        logoutRel = (RelativeLayout) findViewById(R.id.logoutRel);
        refrechRel = (RelativeLayout) findViewById(R.id.refrechRel);
        descText = (TextView) findViewById(R.id.decsripText);
        uploaderText = (TextView) findViewById(R.id.uploader);
        markerTitle = (TextView )findViewById(R.id.marker_titlev2);
        actionBarRel = (RelativeLayout) findViewById(R.id.actionBarRel);
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

    public class CustomPagerAdapter extends PagerAdapter {
        View page;
        int count;
        String objID;


        public CustomPagerAdapter(int count, String objID) {
            super();
            this.count = count;
            this.objID = objID;
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
            page = inflater.inflate(R.layout.album_frag,  container, false);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

