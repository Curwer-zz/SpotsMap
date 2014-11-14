package net.eray.spotsmap.tabs;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import net.eray.spotsmap.MapActivity;
import net.eray.spotsmap.R;
import net.eray.spotsmap.floatingActionButton.FloatingActionButton;

/**
 * Created by Niclas on 2014-09-06.
 */
public class Login_frag extends Fragment {

    //Button btnLogin;
    EditText inputUsername;
    EditText inputPassword;
    View login;
    TextView loginErrorMsg;
    //FloatingActionButton  mFab;

    ParseUser currentUser = ParseUser.getCurrentUser();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        /*
        Looks if there is anyone logged in from the last time the app was opened.
        */
        //isLoggedIn();
        /*
        Set the layout for this fragmanet.
        */
        login = inflater.inflate(R.layout.login_frag, container, false);
        /*
        Defining all layout items
        */
        inputUsername = (EditText) login.findViewById(R.id.username);
        inputPassword = (EditText) login.findViewById(R.id.pword);

        loginErrorMsg = (TextView) login.findViewById(R.id.loginErrorMsg);
        //mFab = (FloatingActionButton)login.findViewById(R.id.fabbutton);

        //mFab.bringToFront();

        /*
        btnLogin = (Button) login.findViewById(R.id.login);
        btnLogin.setBackgroundResource(R.drawable.lock_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) btnLogin.getBackground();
        frameAnimation.start();
        */

        /*mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                    String uName = inputUsername.getText().toString();
                    String pWord = inputPassword.getText().toString();

                    ParseUser.logInInBackground(uName, pWord, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null){
                                Intent mIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                                startActivity(mIntent);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),"No such user exists, please signup!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if ( ( !inputUsername.getText().toString().equals("")) )
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Username and Password field are empty", Toast.LENGTH_SHORT).show();
                }

            }
        }); */



        return login;
    }

    public void isLoggedIn() {
        if (currentUser != null) {
            Log.i("Current user not null", "");
            Intent mIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
            startActivity(mIntent);
        } else {
            Log.i("Current user null","");
        }
    }


    public void login() {

        inputUsername = (EditText) getView().findViewById(R.id.username);
        inputPassword = (EditText) getView().findViewById(R.id.pword);

        if (( !inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals(""))) {
            String uName = inputUsername.getText().toString();
            String pWord = inputPassword.getText().toString();

            ParseUser.logInInBackground(uName, pWord, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null){
                        Intent mIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                        startActivity(mIntent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),"No such user exists, please signup!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (( !inputUsername.getText().toString().equals(""))) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Password field empty", Toast.LENGTH_SHORT).show();
        } else if (( !inputPassword.getText().toString().equals(""))) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Username field empty", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Username and Password field are empty", Toast.LENGTH_SHORT).show();
        }
    }
}





