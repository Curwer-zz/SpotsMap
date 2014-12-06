package net.eray.ParkourPlayground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.CountCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;

import io.codetail.animation.Animator;
import io.codetail.animation.ViewAnimationUtils;


public class Activity_login extends Activity {

    EditText inputUsername;
    EditText inputPassword;
    FloatingActionButton mFab;
    TextView loginErrorMsg;
    ProgressBar pg;
    TextView pass_txt, txt_problem;
    RelativeLayout signup_link;
    ProgressDialog progress;
    View view;

    ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewsById();

        isLoggedIn();

        txt_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Activity_PassReset.class);
                startActivity(mIntent);
            }
        });

        signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Activity_register.class);
                startActivity(mIntent);
            }
        });

        mFab.setColor(getResources().getColor(R.color.FloatingActionBarColor_4CAF50));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (( !inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals(""))) {
                    String uName = inputUsername.getText().toString();
                    String pWord = inputPassword.getText().toString();
                    inputUsername.setVisibility(View.INVISIBLE);
                    inputPassword.setVisibility(View.INVISIBLE);
                    pg.setVisibility(View.VISIBLE);


                    ParseUser.logInInBackground(uName, pWord, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null){
                                Intent mIntent = new Intent(getApplicationContext(), MapActivity.class);
                                startActivity(mIntent);
                                overridePendingTransition(0,0);
                                Activity_login.this.finish();
                            } else {
                                pg.setVisibility(View.GONE);
                                inputUsername.setVisibility(View.VISIBLE);
                                inputPassword.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "No such user exists, please signup!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else if (( !inputUsername.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                } else if (( !inputPassword.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Username and Password field are empty", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void startAnimation() {
        view = findViewById(R.id.loginSlide);
        //int cx = view.getRight() - 150;
        //int cy = view.getBottom() - 150;

        int cx = Math.round(mFab.getX()+100);
        int cy = Math.round(mFab.getY()+100);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) + 100;
        int startRadius = Math.max(mFab.getWidth()/2, mFab.getHeight()) / 2;
        Animator animator = (Animator)
                ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, finalRadius);
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
        txt_problem = (TextView) findViewById(R.id.txt_problem);
        signup_link = (RelativeLayout) findViewById(R.id.signup_link);
        pass_txt = (TextView) findViewById(R.id.txt_problem);
        pg = (ProgressBar)findViewById(R.id.progressBar);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.pword);
        mFab = (FloatingActionButton) findViewById(R.id.fabbutton_login);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);
    }

    public void isLoggedIn() {
        if (currentUser != null) {
            progressDefault();

            ParseQuery query = ParseQuery.getQuery("_User");
            query.whereMatches("username", currentUser.getUsername());
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int i, ParseException e) {
                    if (e == null) {
                        if (i == 1) {
                            progressDismiss();
                            Intent mIntent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(mIntent);
                            overridePendingTransition(0,0);
                            Activity_login.this.finish();
                        } else if (i == 0) {
                            progressDismiss();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Activity_login.this);
                            builder1.setMessage("Your profile could not be found.");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            currentUser = null;
                                            ParseUser.logOut();
                                        }
                                    });
                            AlertDialog skip = builder1.create();
                            skip.show();
                        }
                    }
                }
            });


        } else {
        }
    }

    public void progressDefault(){
        progress = ProgressDialog.show(this, "Loading", "Please wait...", true);
    }
    public void progressDismiss(){
        progress.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
