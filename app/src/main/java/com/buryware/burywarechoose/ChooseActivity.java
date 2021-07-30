package com.buryware.burywarechoose;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/*
 *
 * Created by Steve Stansbury on 11/13/2015.
 *
*/

public class ChooseActivity extends AppCompatActivity {

    int nLevel = 1;         // game level
    int nCells = 2;         // number of dots

    private boolean mNightMode = true;
    Intent intent = null;

   /* public static FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    public static DatabaseReference mFirebaseDatabaseReference;
    public static FirebaseAnalytics mFirebaseAnalytics;*/

    public static final String TAG = "ChooseActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    public static final String ANONYMOUS = "anonymous";
    public static final int REQUEST_INVITE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose);
        intent= new Intent();

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Start the game...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startNewGame();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_startover) {
            startNewGame();
            setContentView(R.layout.gamegridview);

            return true;
        }

        if (id == R.id.action_donate) {
            Intent intent = new Intent(this, DonationsActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_invite) {
            sendInvitation();

            return true;
        }

        if (id == R.id.action_nightmode) {
            mNightMode = !mNightMode;
            if (mNightMode) {
                item.setTitle(R.string.night_mode_on);
            } else{
                item.setTitle(R.string.night_mode_off);
            }
            return true;
        }

        if (id == R.id.action_HSmode) {
            Intent intent = new Intent(this, HighAwardsScores.class);
            intent.putExtra("night_mode", mNightMode);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_Highestmode) {
            Intent intent = new Intent(this, HighestAwardsScores.class);
            intent.putExtra("night_mode", mNightMode);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_Thanksmode) {
            Intent intent = new Intent(this, ChooseThanksFirstUsers.class);
            intent.putExtra("night_mode", mNightMode);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, ChooseAboutActivity.class);
            intent.putExtra("night_mode", mNightMode);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    public void startNewGame() {

        intent = new Intent(this, ChooserGameActivity.class);
        intent.putExtra("night_mode", mNightMode);
        startActivity(intent);
    }

    public int getCells() {

        return nLevel * nLevel;
    }
}
