package net.eray.ParkourPlayground.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import net.eray.ParkourPlayground.R;

/**
 * Created by Niclas on 2014-09-06.
 */
public class PassRes_frag extends Fragment {

    EditText email;
    TextView alert;
    //Button resetpass;
    View passRes;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        passRes = inflater.inflate(R.layout.reset_frag, container, false);


        email = (EditText)passRes.findViewById(R.id.forpas);
        alert = (TextView)passRes.findViewById(R.id.alert);
       /* resetpass = (Button)passRes.findViewById(R.id.respass);

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailReset = email.getText().toString();
                if (mailReset == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "You must enter a valid email", Toast.LENGTH_SHORT).show();
                } else if (mailReset != null) {
                    ParseUser.requestPasswordResetInBackground(mailReset, new RequestPasswordResetCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // An email was successfully sent with reset instructions.
                                    } else {
                                        Log.i("PasswordReset", "" + e.getMessage());

                                    }
                                }
                            });
                }
            }



        }); */
        return passRes;
    }

    public void signUp() {
        String mailReset = email.getText().toString();
        if (mailReset == null) {
            Toast.makeText(getActivity().getApplicationContext(), "You must enter a valid email", Toast.LENGTH_SHORT).show();
        } else if (mailReset != null) {
            ParseUser.requestPasswordResetInBackground(mailReset, new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // An email was successfully sent with reset instructions.
                    } else {
                        Log.i("PasswordReset", "" + e.getMessage());

                    }
                }
            });
        }
    }

    public void helloLogin() {
        Toast.makeText(getActivity().getApplicationContext(), "Hello Reset", Toast.LENGTH_SHORT).show();
    }

}
