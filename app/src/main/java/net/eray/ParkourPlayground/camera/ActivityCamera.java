package net.eray.ParkourPlayground.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.eray.ParkourPlayground.GPSTracker;
import net.eray.ParkourPlayground.R;
import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityCamera extends FragmentActivity {

    //Map in detail view
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;

    private Camera mCamera;
    private CameraPreview mPreview;
    private int width, camWidth, camHeight, flashOn, checkWidth, checkHeight;
    boolean positionFound = true;
    private Button retry, captureButton;
    private ImageButton flashtoggle, btnBack;
    private Bitmap rotatedCropped;
    private Double dlong, dlat;
    private ParseFile file;
    private TextView txtImageList;
    private ArrayList<ParseFile> imageList = new ArrayList<ParseFile>();
    private FloatingActionButton mFab;
    private Dialog dialogImageDetalis;
    private EditText titleDialog, discriptionDialog;
    private RelativeLayout btnCancelDialog, btnUploadDialog;
    private Boolean isResume = false;
    private Boolean picCaptured = false;
    private ProgressBar dialog_progress;

    SharedPreferences sharedPreferences;
    Camera.Parameters parameters;
    SharedPreferences.Editor editor;

    //call the tracker
    private LocationListener locationListener;
    private LocationManager locationManager;
    private GPSTracker tracker;
    private Location location;
    private Marker marker;
    private View view;

    /**
     * Now after the user takes a photo, i need to put them in a list of bitmaps, max 3.
     * Then when the user presses upload. I want to loop the array and saveinbackground
     * all the images 1 by 1. since i have the objectId of the marker information from
     * my class Map, i will be able to create a "album" of images for one spot.
     */

    //Parse user
    ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        findViewsById();

        loadFlashToggle();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mCamera = getCameraInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCamera.this.finish();
            }
        });

        mFab.setColor(getResources().getColor(R.color.FloatingActionBarColor_4CAF50));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picCaptured == true) {
                    addToImageList();
                    mCamera.startPreview();
                    retry.setVisibility(View.GONE);
                    captureButton.setVisibility(View.VISIBLE);
                    flashtoggle.setVisibility(View.VISIBLE);
                    mFab.setDrawable(getResources().getDrawable(R.drawable.ic_content_create));
                    picCaptured = false;
                } else {
                    mCamera.stopPreview();
                    dialogImageDetalis = new Dialog(ActivityCamera.this);
                    dialogImageDetalis.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogImageDetalis.setContentView(R.layout.dialog_image_details);
                    dialogImageDetalis.setCancelable(true);

                    setUpMapIfNeeded();

                    titleDialog = (EditText) dialogImageDetalis.findViewById(R.id.titleDialog);
                    discriptionDialog = (EditText) dialogImageDetalis.findViewById(R.id.discriptionDialog);
                    btnCancelDialog = (RelativeLayout) dialogImageDetalis.findViewById(R.id.btnCancelDialog);
                    btnUploadDialog = (RelativeLayout) dialogImageDetalis.findViewById(R.id.btnUploadDialog);
                    dialog_progress = (ProgressBar) dialogImageDetalis.findViewById(R.id.dialog_progress);

                    if (positionFound == false) {
                        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(LatLng latLng) {
                                if (marker != null) {
                                    marker.remove();
                                    marker = null;
                                }
                                marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_place)).title("You"));
                            }
                        });
                    }

                    map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {

                        }
                    });

                    dialogImageDetalis.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getSupportFragmentManager().beginTransaction().remove(supportMapFragment).commit();
                            map = null;
                            dialog.dismiss();
                            dialog.cancel();
                            mCamera.startPreview();
                        }
                    });

                    btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogImageDetalis.dismiss();
                            mCamera.startPreview();
                        }
                    });

                    btnUploadDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_progress.setVisibility(View.VISIBLE);
                            titleDialog.setVisibility(View.INVISIBLE);
                            discriptionDialog.setVisibility(View.INVISIBLE);
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                //secondly checks if longitude and latitude has received values from the GPS
                                if (marker.getPosition() != null) {
                                    if (!titleDialog.getText().toString().equals("")) {
                                        if (!discriptionDialog.getText().toString().equals("")) {
                                            //Set the name for the image to the date
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                            final String imagename = df.format(c.getTime());
                                            //random picture from the res catalogue
                                            ParseGeoPoint userPoint = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            // Compress image to lower quality scale 1 - 100
                                            rotatedCropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] image = stream.toByteArray();
                                            //insert image to a ParseFile
                                            file = new ParseFile(imagename + ".jpeg", image);
                                            ParseObject upload = new ParseObject("Map");
                                            upload.put("uploader", currentUser.getUsername());
                                            upload.put("pointUploader", ParseObject.createWithoutData("_User", currentUser.getObjectId()));
                                            upload.put("description", discriptionDialog.getText().toString());
                                            upload.put("title", titleDialog.getText().toString());
                                            upload.put("validate", true);
                                            upload.put("geopoint", userPoint);
                                            upload.put("size", imageList.size());
                                            upload.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    ParseQuery query = ParseQuery.getQuery("Map");
                                                    query.whereEqualTo("uploader", currentUser.getUsername());
                                                    query.orderByDescending("createdAt");
                                                    query.getFirstInBackground(new GetCallback() {
                                                        @Override
                                                        public void done(ParseObject parseObject, ParseException e) {
                                                            for (int i = 0; i < imageList.size(); i++) {
                                                                ParseObject imageUplaod = new ParseObject("ImageFiles");
                                                                imageUplaod.put("geoPosition", ParseObject.createWithoutData("Map", parseObject.getObjectId()));
                                                                imageUplaod.put("image", imageList.get(i));
                                                                imageUplaod.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        Log.e("Image done uploading", "DONE");
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            showImageUploadedToUser();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "We need you to fill in the description aswell.", Toast.LENGTH_SHORT).show();
                                            dialog_progress.setVisibility(View.GONE);
                                            titleDialog.setVisibility(View.VISIBLE);
                                            discriptionDialog.setVisibility(View.VISIBLE);
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please set the title for this spot", Toast.LENGTH_SHORT).show();
                                        dialog_progress.setVisibility(View.GONE);
                                        titleDialog.setVisibility(View.VISIBLE);
                                        discriptionDialog.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Please wait until your GPS received the location data.", Toast.LENGTH_SHORT).show();
                                    dialog_progress.setVisibility(View.GONE);
                                    titleDialog.setVisibility(View.VISIBLE);
                                    discriptionDialog.setVisibility(View.VISIBLE);
                                }
                            } else {
                                showGPSDisabledAlertToUser();
                                dialog_progress.setVisibility(View.GONE);
                                titleDialog.setVisibility(View.VISIBLE);
                                discriptionDialog.setVisibility(View.VISIBLE);
                            }

                        }

                    });
                    dialogImageDetalis.show();
                }
            }
        });

        flashtoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashOn != 2) {
                    flashOn++;
                } else {
                    flashOn = 0;
                }
                getFlshToggle(flashOn);
                saveFlashToggle(flashOn);

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.startPreview();
                retry.setVisibility(View.GONE);
                //btnDone.setVisibility(View.GONE);
                flashtoggle.setVisibility(View.VISIBLE);
                captureButton.setVisibility(View.VISIBLE);
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageList.size() <= 2) {
                    mCamera.takePicture(null, null, mPicture);
                } else {
                    captureButton.setBackgroundResource(R.drawable.ic_image_center_focus_strong);
                }
            }
        });
    }

    public void getFlshToggle(int count) {
        if (count == 1) {
            try {
                parameters.setFlashMode(parameters.FLASH_MODE_ON);
                flashtoggle.setImageResource(R.drawable.ic_action_flash_on);
            } catch (NullPointerException e) {

            }
        } else if (count == 2) {
            try {
                parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
                flashtoggle.setImageResource(R.drawable.ic_action_flash_automatic);
            } catch (NullPointerException e) {

            }
        } else if (count == 0) {
            try {
                parameters.setFlashMode(parameters.FLASH_MODE_OFF);
                flashtoggle.setImageResource(R.drawable.ic_action_flash_off);
            } catch (NullPointerException e) {

            }

        }
    }

    public void saveFlashToggle(int count) {
        sharedPreferences = getSharedPreferences("flash", getApplicationContext().MODE_WORLD_READABLE);
        editor = sharedPreferences.edit();
        editor.putInt("flashToggle", count);
        editor.commit();
    }

    public void loadFlashToggle() {
        sharedPreferences = getSharedPreferences("flash", getApplicationContext().MODE_WORLD_READABLE);
        flashOn = sharedPreferences.getInt("flashToggle", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();              // release the camera immediately on pause event
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
            locationManager = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isResume == true) {
                mCamera = getCameraInstance();
                isResume = false;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try {
                mCamera.stopPreview();
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(bytes, 0, bytes.length, true);
                Bitmap cropped = decoder.decodeRegion(new Rect(0, 0, camHeight, camHeight), null);
                rotatedCropped = rotate(cropped, 90);

                retry.setVisibility(View.VISIBLE);
                picCaptured = true;
                mFab.setDrawable(getResources().getDrawable(R.drawable.ic_action_done));
                captureButton.setVisibility(View.INVISIBLE);
                flashtoggle.setVisibility(View.INVISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void addToImageList() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String imagename = df.format(c.getTime());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        rotatedCropped.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        //insert image to a ParseFile
        file = new ParseFile(imagename + ".jpeg", image);
        imageList.add(file);
        mCamera.startPreview();
        txtImageList.setVisibility(View.VISIBLE);
        txtImageList.setText(imageList.size() + "/3");

        if (imageList.size() == 3) {
            captureButton.setBackgroundResource(R.drawable.ic_image_center_focus_strong);
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        parameters = c.getParameters();
        parameters.getSupportedPreviewSizes();
        List<Camera.Size> listSupportedPreviewSizes = parameters.getSupportedPreviewSizes();

        List<Camera.Size> listSupportedPictureSizes = parameters.getSupportedPictureSizes();


        for (int i = 0; i < listSupportedPictureSizes.size(); i++) {
            //if(listSupportedPictureSizes.get(i).width>1000){
            for (int x = 0; x < listSupportedPreviewSizes.size(); x++) {
                if (listSupportedPictureSizes.get(i).width == listSupportedPreviewSizes.get(x).width) {
                    if (listSupportedPictureSizes.get(i).height == listSupportedPreviewSizes.get(x).height) {

                        checkWidth = listSupportedPreviewSizes.get(x).width;
                        checkHeight = listSupportedPreviewSizes.get(x).height;

                        if (checkWidth >= camWidth || checkWidth >= camHeight || checkHeight >= camHeight || checkHeight >= camWidth) {
                            camWidth = checkWidth;
                            camHeight = checkHeight;

                        }
                    }
                }
            }
        }

        //}
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        parameters.setPreviewSize(camWidth, camHeight);
        parameters.setPictureSize(camWidth, camHeight);
        //if we want effects, use code like below
        Context context = this;
        PackageManager packageManager = context.getPackageManager();
        // if device support flash?
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            getFlshToggle(flashOn);
            Log.i("camera", "This device has flash supported!");
        } else {
            //no
            Log.i("camera", "This device has no flash support!");
            flashtoggle.setEnabled(false);
            flashtoggle.setClickable(false);
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            Log.i("autofocus", "This devise has autofocus support!");
        } else {
            Log.i("autofocus", "This devise has no autofocus support!");
        }
        // parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
        c.setParameters(parameters);
        mPreview = new CameraPreview(this, c);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
        relativeLayoutControls.bringToFront();

        RelativeLayout layout_hollow = (RelativeLayout) findViewById(R.id.relativeLayout_hollow);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_hollow.getLayoutParams();
        lp.height = width;
        return c; // returns null if camera is unavailable
    }

    //Runs this alert dialog if user has not enabled GPS when he uploads picture to server
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto settings page to enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Runs this alert dialog if user had a successful upload of the image and info
    private void showImageUploadedToUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (imageList.size() > 1) {
            alertDialog.setMessage("Image(s) uploaded successfully.");
        } else {
            alertDialog.setMessage("Image uploaded successfully.");
        }
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCamera.this.finish();
            }
        });
        alertDialog.show();
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return b;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uploader, menu);
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

    public void findViewsById() {
        mFab = (FloatingActionButton) findViewById(R.id.fabbutton_camera);
        captureButton = (Button) findViewById(R.id.btnCapture);
        txtImageList = (TextView) findViewById(R.id.txt_imageList);
        retry = (Button) findViewById(R.id.btnRefrech);
        flashtoggle = (ImageButton) findViewById(R.id.btnFlash);
        btnBack = (ImageButton) findViewById(R.id.btnBackCam);
    }

    private void setUpMapIfNeeded() {
        /**
         *Do a null check to confirm that the map is not already instantiated
         **/
        if (map == null) {
            //Try to obtain the map from the SupportMapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapPosition);

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
            if (tracker.getIsGpsEnabled()) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (tracker.getIsNetworkEnabled()) {
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

            marker = map.addMarker(new MarkerOptions().position(latlng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_place)).title("You"));
        } else {
            positionFound = false;
        }
    }

}
