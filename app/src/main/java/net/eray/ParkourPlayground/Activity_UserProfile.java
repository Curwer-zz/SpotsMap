package net.eray.ParkourPlayground;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.eray.ParkourPlayground.customView.Circle_ImageView;
import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class Activity_UserProfile extends ActionBarActivity {

    private TextView txtUsername, txtEmail, linkImage, linkChange, linkImageOk;
    private Circle_ImageView imgProfil;
    private FloatingActionButton mFab;
    private MaterialDialog mDialog;
    private View view;
    private Boolean imageChange = false;
    private static final int SELECT_PHOTO = 100;
    private Bitmap image;
    private ParseFile file;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<ParseObject> objList;


    ParseUser currentUser = ParseUser.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setTitle(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewsById();
        loadProfilPicture();
        //RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        SpotAdapter adapter = new SpotAdapter(null, null);
        recyclerView.setAdapter(adapter);
        //Start the object count
        countMapObject();

        //Action button color and onclick
        mFab.setColor(getResources().getColor(android.R.color.white));
        mFab.bringToFront();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view = LayoutInflater.from(Activity_UserProfile.this).inflate(R.layout.dialog_edit, null);
                linkImage = (TextView) view.findViewById(R.id.linkImage);
                linkChange = (TextView) view.findViewById(R.id.linkChange);
                linkImageOk = (TextView) view.findViewById(R.id.linkImageOk);

                mDialog = new MaterialDialog(Activity_UserProfile.this)
                        .setTitle("Edit")
                        .setContentView(view)

                        .setPositiveButton(getString(R.string.done), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (imageChange == true) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                                    byte[] bytes = stream.toByteArray();

                                    file = new ParseFile("proImage.jpeg", bytes);
                                    currentUser.put("profilPicture", file);
                                    try {
                                        currentUser.save();
                                        mDialog.dismiss();
                                        loadProfilPicture();
                                    } catch (ParseException e) {

                                    }
                                }

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                view = null;
                            }
                        });

                linkImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    }
                });

                linkChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                mDialog.show();
            }
        });
        txtUsername.setText(currentUser.getUsername());
        txtEmail.setText(currentUser.getEmail());

    }

    public void countMapObject() {
        objList = new ArrayList<ParseObject>();
        ParseQuery query = ParseQuery.getQuery("Map");
        ParseObject obj = ParseObject.createWithoutData("_User", currentUser.getObjectId());
        query.whereEqualTo("pointUploader", obj);
        //query.include("ImageFiles");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        objList.add(i , list.get(i));
                    }

                    SpotAdapter adapter = new SpotAdapter(createList(list.size()), Activity_UserProfile.this);
                    adapter.notifyDataSetChanged();
                    //createList(list.size());
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private List<UserSpots> createList(int size) {
        if (size != 0) {

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

            List<UserSpots> result = new ArrayList<UserSpots>();
            for (int i = 0; i < size; i++) {
                UserSpots userSpots = new UserSpots();
                userSpots.title = objList.get(i).getString("title");
                try {
                    userSpots.date = dateformat.format(objList.get(i).getCreatedAt()).toString();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                result.add(userSpots);
                //userSpots.location = objList.get(i).get("geoPoint").toString();
                //userSpots.image = objList.get(i).getParseObject("ImageFile").getParseFile("image");
            }

            Collections.reverse(result);
            return result;
        }
        return null;
    }

    public void loadProfilPicture() {
        ParseFile proPic = ParseUser.getCurrentUser().getParseFile("profilPicture");
        imgProfil.setParseFile(proPic);
        imgProfil.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    if (bytes == null) {
                        imgProfil.setImageResource(R.drawable.placeholder_profile_image);
                    }
                } else {

                }
            }
        });
    }

    public void findViewsById() {
        txtUsername = (TextView) findViewById(R.id.txtUsernam);
        imgProfil = (Circle_ImageView) findViewById(R.id.imgProfil);
        mFab = (FloatingActionButton) findViewById(R.id.mFab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtEmail = (TextView) findViewById(R.id.txt_email);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    image = BitmapFactory.decodeFile(filePath);
                    imageChange = true;
                    linkImageOk.setVisibility(View.VISIBLE);
                }
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
                Activity_UserProfile.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
