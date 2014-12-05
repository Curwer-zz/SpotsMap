package net.eray.ParkourPlayground;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;


public class Activity_register extends Activity {

    FloatingActionButton mFab;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewsById();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals(""))) {
                    if (inputUsername.getText().toString().length() > 4 ){

                        String uName = inputUsername.getText().toString();
                        String eMail = inputEmail.getText().toString();
                        String pWord = inputPassword.getText().toString();

                        user = new ParseUser();

                        user.setUsername(uName);
                        user.setPassword(pWord);
                        user.setEmail(eMail);

                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Activity_register.this);
                                    builder1.setTitle("Success!");
                                    builder1.setMessage("Your account has been created!\nDon´t forget to verify your email!");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    ParseUser.logOut();
                                                    user = null;
                                                    Intent mIntent = new Intent(Activity_register.this, Activity_login.class);
                                                    startActivity(mIntent);
                                                    dialog.cancel();
                                                    Activity_register.this.finish();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                    resetTextField();
                                } else {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Activity_register.this);
                                    builder1.setTitle("Error registering!");
                                    builder1.setMessage("Something went wrong. Try again please!");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void findViewsById() {
        mFab = (FloatingActionButton) findViewById(R.id.fabbutton_reg);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.pword);
    }

    public void resetTextField() {
        inputUsername.setText("");
        inputPassword.setText("");
        inputEmail.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_register, menu);
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
