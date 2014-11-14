package net.eray.spotsmap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import net.eray.spotsmap.floatingActionButton.FloatingActionButton;



public class Activity_PassReset extends Activity {

    FloatingActionButton mFab;
    EditText email;
    TextView alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__pass_reset);

        findViewsById();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailReset = email.getText().toString();
                if (mailReset == null) {
                    Toast.makeText(getApplicationContext(), "You must enter a valid email", Toast.LENGTH_SHORT).show();
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
        });
    }

    public void findViewsById() {
        mFab = (FloatingActionButton) findViewById(R.id.fabbutton_passRes);
        email = (EditText) findViewById(R.id.forpas);
        alert = (TextView) findViewById(R.id.alert);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__pass_reset, menu);
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
