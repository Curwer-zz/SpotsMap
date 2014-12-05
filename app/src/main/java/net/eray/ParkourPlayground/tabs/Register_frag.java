package net.eray.ParkourPlayground.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.eray.ParkourPlayground.R;

/**
 * Created by Niclas on 2014-09-06.
 */
public class Register_frag extends Fragment {

    /**
     * Defining layout items.
     */

    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    TextView registerErrorMsg;
    View reg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        reg = inflater.inflate(R.layout.reg_frag, container, false);

        /*
        Defining all layout items
        */
        inputUsername = (EditText)reg.findViewById(R.id.username);
        inputEmail = (EditText)reg.findViewById(R.id.email);
        inputPassword = (EditText)reg.findViewById(R.id.pword);
        //btnRegister = (Button)reg.findViewById(R.id.register);

        /*btnRegister.setBackgroundResource(R.drawable.reg_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) btnRegister.getBackground();
        frameAnimation.start();

         btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals(""))) {
                    if ( inputUsername.getText().toString().length() > 4 ){
                        ParseUser user = new ParseUser();
                        String uName = inputUsername.getText().toString();
                        String eMail = inputEmail.getText().toString();
                        String pWord = inputPassword.getText().toString();

                        user.setUsername(uName);
                        user.setPassword(pWord);
                        user.setEmail(eMail);


                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Conngratulations! You are now signed up.", Toast.LENGTH_SHORT).show();
                                    resetTextField();
                                } else {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity().getApplicationContext());
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
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        }); */

        return reg;
    }
    public void resetTextField() {
        inputUsername.setText("");
        inputPassword.setText("");
        inputEmail.setText("");
    }

    public void helloLogin() {
        Toast.makeText(getActivity().getApplicationContext(), "Hello Register", Toast.LENGTH_SHORT).show();
    }

    public void signup() {
        if (( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals(""))) {
            if ( inputUsername.getText().toString().length() > 4 ){
                ParseUser user = new ParseUser();
                String uName = inputUsername.getText().toString();
                String eMail = inputEmail.getText().toString();
                String pWord = inputPassword.getText().toString();

                user.setUsername(uName);
                user.setPassword(pWord);
                user.setEmail(eMail);


                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Conngratulations! You are now signed up.", Toast.LENGTH_SHORT).show();
                            resetTextField();
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity().getApplicationContext());
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
                Toast.makeText(getActivity().getApplicationContext(),
                        "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(),
                    "One or more fields are empty", Toast.LENGTH_SHORT).show();
        }
    }

}
