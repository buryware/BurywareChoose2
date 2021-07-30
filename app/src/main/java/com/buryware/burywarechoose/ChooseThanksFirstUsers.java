package com.buryware.burywarechoose;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/*
 *
 * Created by Steve Stansbury on 11/13/2015.
 *
*/

public class ChooseThanksFirstUsers extends AppCompatActivity {

    int nLevel = 1;         // game level
    int nCells = 2;         // number of dots

    private boolean mNightMode = false;
    Intent intent = null;

   /* public static FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public String mUsername;*/

    //public static DatabaseReference mFirebaseDatabaseReference;
    //public static FirebaseAnalytics mFirebaseAnalytics;

    public static final String TAG = "ChooseThanksFirstUsersActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    public static final String ANONYMOUS = "anonymous";
    public static final int REQUEST_INVITE = 1;

    private Button mOkay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.thanksfirstusers_choose);
        intent= new Intent();

        mOkay = findViewById(R.id.okaybutton);
        mOkay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // all done, return...
                finish();
            }
        });
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Thanks First Users reconition page...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    try {
                        finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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

        //noinspection SimplifiableIfStatemen

        return super.onOptionsItemSelected(item);
    }
}
