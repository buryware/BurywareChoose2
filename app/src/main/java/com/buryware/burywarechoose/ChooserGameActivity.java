package com.buryware.burywarechoose;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;*/
//import com.google.android.gms.appinvite.AppInviteInvitation;

/**
 *
 * Created by Steve Stansbury on 11/13/2015.
 *
 */
public class ChooserGameActivity extends AppCompatActivity {

    //private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj8mQN5gQY1Zlid9pzSXQF8UaPvaZDwJU9ZG0HCKBN4aVjCe8+d0Zeu0LGdRQFiGgVV3+RigKtSYIQU4iiOfldd0rfSlnjP/gzyQKA9+WeEcBBDLEcd/YVR/TuATyLdQgmb+86DJp19DEp5rVjHWq7Rf5I+s/bFfhNCAJp7LTwpXHc0Yutm9LO+FCwe9Q7hVjOXHcc+xv6FG3shgNRbadz6UWCrwS5AY7Fw7vrnbKwCUHgKfJDlh0PJHG0yAUDiNkzSK+vSYIrwb++mUKryRKCHQDC1cR8qLsr25r9jQ00QQDSjPUwNNWiu1sVVtrWldB8+VDV1MWLBfObz2FBka+6wIDAQAB";
    private static final Object FirebaseUserActions = null;
    private int nLevel = 1;
    private int nTotalWins, nTotalLoses, nWins, nLoser, nLoses, nWinner = 0;
    private int [] gameBoard = null;
    private boolean bLevelUp = false;

    private Random randomSeed;

    private GridView mGrid = null;
    private HorizontalScrollView mHorScrollView = null;
    private Button mResetButton = null;
    private ImageView mAwardsButton = null;
    private ImageView mTournamentButton = null;
    private ImageView mDonateButton = null;
    private ImageView mFirstUserRecButton = null;
    private TextView mGameStatus = null;
    private TextView mLevelStatus = null;
    private boolean mGameOver = false;

    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;

    public static DatabaseReference mFirebaseDatabaseReference;
    public static FirebaseAnalytics mFirebaseAnalytics;
    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleSignInClient mGoogleSignInClient;

    public GoogleApiClient mGoogleApiClient;


    public LinearLayoutManager mLinearLayoutManager;
    public EditText mMessageEditText;
    public ImageView mAddMessageImageView;
    //public AdView mAdView;

    public String mMsgID = null;
    public String mUsername = null;
    public String mTimestamp = null;
    public String mEmail = null;
    public String mDate = null;
    public String mStartTime = null;
    public String mTotalTime = null;
    public String mTourneyDate = null;
    public String mEntryAmount = null;
    public String mHistory = null;

    public static final String TAG = "ChooserGameActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String MESSAGES_CHILD2 = "game_dates";
    public static final String ANONYMOUS = "anonymous";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private static final int RC_SIGN_IN = 9001;

    public static String mCurrentDate = null;
    public static String mCurrentTime = null;
    public static String mFirstDayofMonth = null;
    public static String mLastDayofMonth = null;

    public static android.icu.text.SimpleDateFormat mCurrentDateTime = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private boolean mNightMode = false;
    Bundle extras;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();

        setContentView(R.layout.gamegridview);

        if (extras.getBoolean("night_mode") == true) {
            setupNightMode();

        } else {
            setupDayMode();
        }

        newGameGrid();

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            CheckableLayout l;
            ImageView i;

            l = (CheckableLayout) v;
            i = (ImageView) l.getChildAt(0);

            if (l.isChecked()) {

                //   i.setBackground(getResources().getDrawable(R.drawable.yellowbackground));

                Animation out = AnimationUtils.makeOutAnimation(getApplication(), true);
                i.startAnimation(out);
                i.setVisibility(View.INVISIBLE);


                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (gameBoard[position] == 0) {  // 0 - green   1 - loser

                    i.setImageDrawable(getDrawable(R.drawable.circle_green));
                    gameBoard[position] = -1;
                    nWins++;
                    nTotalWins--;

                } else if (gameBoard[position] == 1){

                    i.setImageDrawable(getDrawable(R.drawable.circle_red));
                    gameBoard[position] = -1;
                    nLoses++;
                    nTotalLoses--;
                }

                Animation in = AnimationUtils.loadAnimation(getApplication(), android.R.anim.fade_in);
                i.startAnimation(in);
                i.setVisibility(View.VISIBLE);

                UpdateLevelStatus();
                mGameStatus = findViewById(R.id.statusline);
                if ((nWins == nWinner) && !mGameOver){

                    mGameStatus.setText(R.string.action_win);
                    i.setImageDrawable(getDrawable(R.drawable.check));
                    bLevelUp = true;
                    UpdateLevelStatus();
                    mGameOver = true;

                } else if ((nLoses == nLoser) && !mGameOver){

                    mGameStatus.setText(R.string.action_lose);
                    i.setImageDrawable(getDrawable(R.drawable.error));
                    mGameOver = true;

                } else if (!mGameOver){

                    mGameStatus.setText(getString(R.string.Playing));
                }
            }
            }
        });

        mResetButton = findViewById(R.id.resetbutton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                resetGame();
            }
        });

        mAwardsButton = findViewById(R.id.awardsbutton);
        mAwardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ShowAwards();
            }
        });

        mDonateButton = findViewById(R.id.donatebutton);
        mDonateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DoDonation();
            }
        });

        mTournamentButton = findViewById(R.id.tournamnetbutton);
        mTournamentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DoTournament();
            }
        });

       /* mFirstUserRecButton = findViewById(R.id.highestawardsbutton);   // todo maybe later
        mFirstUserRecButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DoHighestAwardsScores();
            }
        });*/

        mFirstUserRecButton = findViewById(R.id.thanksbutton);
        mFirstUserRecButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DoFirstUserRec();
            }
        });

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);

        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder().build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 120L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

      /*  if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);

            return;
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "On Start .....");

        mDate = mCurrentDate;
        mStartTime =  mCurrentTime;
        mTotalTime = "00:00.00";
        mTourneyDate = "05/22/2019";
        mHistory = "{03/03/19}, {01/01/17}";

        // test values
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTimestamp = getCurentDateTime().toString();
            mDate = mCurrentDate;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);

                mUsername = mFirebaseUser.toString();
                mEmail = mFirebaseUser.getEmail();

            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(ChooserGameActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(ChooserGameActivity.this, ChooseActivity.class));
                            finish();
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Date getFirstofMonth() {

        return getFirstofMonth();
    }

    public Date getLastofMonth() {

        return getLastofMonth();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static android.icu.text.SimpleDateFormat getCurentDateTime() {

        Date todaysdate = new Date();

        mCurrentDateTime = new android.icu.text.SimpleDateFormat("MMM.dd.yyyy'T'hh:mm:ss aaa");
        String formattedDate = mCurrentDateTime.format(todaysdate);
        String[] str = formattedDate.split("T");

        mCurrentDate = str[0];
        mCurrentTime = str[1];

        return mCurrentDateTime;
    }

    public Action getMessageViewAction(FriendlyMessage friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getUserName(),
                        MESSAGE_URL.concat(friendlyMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    public Indexable getMessageIndexable(FriendlyMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mFirebaseUser.equals(friendlyMessage.getUserName()))
                .setName(friendlyMessage.getUserName())
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mFirebaseUser.toString())
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName("Sending a friendly Choose data stream...")
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
     //   if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
     //   }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activate();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config", e);
                        applyRetrievedLengthLimit();
                    }
                });
    }


    public void setupDayMode() {

        mNightMode = false;

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);

        TextView tx = findViewById(R.id.statusline);
        tx.setTextColor(getResources().getColor(R.color.colorBuryware));
        tx = findViewById(R.id.nGameLevel);
        tx.setTextColor(getResources().getColor(R.color.colorBuryware));

        mTournamentButton = findViewById(R.id.tournamnetbutton);
        Drawable tourneyDrawable = getResources().getDrawable(R.drawable.potofgoldnm);
        mTournamentButton.setImageDrawable(tourneyDrawable);

        mFirstUserRecButton = findViewById(R.id.thanksbutton);
        Drawable thanksDrawable = getResources().getDrawable(R.drawable.thanksfirstusers);
        mFirstUserRecButton.setImageDrawable(thanksDrawable);

        view.refreshDrawableState();
    }

    public void setupNightMode() {

        mNightMode = true;

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorBuryware));

        TextView tx = findViewById(R.id.statusline);
        tx.setTextColor(Color.WHITE);
        tx = findViewById(R.id.nGameLevel);
        tx.setTextColor(Color.WHITE);

        mTournamentButton = findViewById(R.id.tournamnetbutton);
        Drawable tourneyDrawable = getResources().getDrawable(R.drawable.potofgoldnm);
        mTournamentButton.setImageDrawable(tourneyDrawable);

        mFirstUserRecButton = findViewById(R.id.thanksbutton);
        Drawable thanksDrawable = getResources().getDrawable(R.drawable.thanksfirstusersnm);
        mFirstUserRecButton.setImageDrawable(thanksDrawable);

        view.refreshDrawableState();
    }

    @Override
    public void onBackPressed() {
        SaveGame();
        finish();

        System.exit(0);
    }

    public void newGameGrid() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            getCurentDateTime();   // update the time
        }

        mGrid = findViewById(R.id.gamegrid);
        mHorScrollView = findViewById(R.id.horScrollview);
        mGameStatus = findViewById(R.id.statusline);
        mLevelStatus = findViewById(R.id.nGameLevel);
        mResetButton = findViewById(R.id.resetbutton);

        if (bLevelUp) {
            bLevelUp = false;
            nLevel++;
        }

        mGrid.setNumColumns(nLevel);
        mGrid.setAdapter(new AppsAdapter());

        ViewGroup.LayoutParams lp = mGrid.getLayoutParams();
        ViewGroup.LayoutParams tx1 = mGameStatus.getLayoutParams();
        ViewGroup.LayoutParams tx2 = mLevelStatus.getLayoutParams();
        ViewGroup.LayoutParams btn = mResetButton.getLayoutParams();

        int uiControlsum = lp.height + tx1.height + tx2.height + tx2.height;
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int screenwidth = metrics.widthPixels;
        int screenheight = metrics.heightPixels;

        if (screenheight > uiControlsum + 50) {
            lp.height += (screenheight - uiControlsum);
        }

        if (lp.width < (nLevel * 48 * 4)) {
            mGrid.setLayoutParams(new LinearLayout.LayoutParams((nLevel * 48 * 4), lp.height));
        }

        if (nLevel == 1){
            mGrid.setVerticalSpacing((lp.height / 4) / nLevel);
        } else {
            mGrid.setVerticalSpacing((lp.height / 2) / nLevel);
        }
        mGrid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        buildGameGrid();

        mGameStatus = findViewById(R.id.statusline);
        mGameStatus.setText(R.string.action_play);
        UpdateLevelStatus();

        mTimestamp = new SimpleDateFormat( getString(R.string.TimeStampSeed)).format(new Date());
        mMsgID = getUniqueID(getString(R.string.MsgUniqueID));
    }

    public void resetGame() {

        SaveGameResults();

        nWins = nLoses = 0;
        newGameGrid();
    }

    public void startOver() {
        nLevel = 1;
        resetGame();
    }

    public void SaveGame() {
        // todo
    }

    public void SaveGameResults() {

       /* String mUser = mFirebaseUser.getDisplayName();  // todo
        if (mUser.isEmpty()) {
            mUser = mUsername;
        }*/

        mUsername = "Steve Stansbury (def)";

        FriendlyMessage friendlyMessage = new FriendlyMessage(mMsgID, mUsername, mEmail, String.valueOf(nLevel), String.valueOf(nWins), String.valueOf(nLoses), mDate,
                mTimestamp, mTotalTime, mTourneyDate, mHistory);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users/" + mUsername);
        if (usersRef != null) {
            // see if we need to do an update
            int i;
            i = 0;

            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
            mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
        }
        else {
            // new record, so add it.
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
            mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
        }
    }

    String getUniqueID(String randseed) {
        UUID uid = UUID.fromString(randseed);
        return UUID.randomUUID().toString();
    }

    public void ShowAwards() {
        Intent intent = new Intent(this, HighAwardsScores.class);
        intent.putExtra("night_mode", mNightMode);
        startActivity(intent);
    }

    public void DoDonation() {
        Intent intent = new Intent(this, DonationsActivity.class);
        startActivity(intent);
    }

    public void DoTournament() {
        Intent intent = new Intent(this, TournementActivity.class);
        intent.putExtra("night_mode", mNightMode);
        startActivity(intent);
    }

    private void DoHighestAwardsScores() {
        Intent intent = new Intent(this, HighestAwardsScores.class);
        intent.putExtra("night_mode", mNightMode);
        startActivity(intent);
    }

    public void DoFirstUserRec() {
        Intent intent = new Intent(this, ChooseThanksFirstUsers.class);
        intent.putExtra("night_mode", mNightMode);
        startActivity(intent);
    }

    public void buildGameGrid() {
        if (gameBoard != null) {
        }
        int size = (nLevel * nLevel);
        if (size == 1){ // 1 x 1 and  we want 2
            size = 2;
        }
        gameBoard = new int[size];
        mGameOver = false;

        fillGameBoard();
    }

    public final void fillGameBoard() {
        int seed;
        int max;

        if (randomSeed != null){
            //              onDestroy(randomSeed);
        }

        randomSeed = new Random();
        max = (nLevel * nLevel);
        nTotalWins = nTotalLoses = 0;

        if (nLevel == 1){
            nWinner = nLoser = 1;
        } else {
            nWinner = nLoser = ((nLevel * nLevel)/ 2);
        }
        if ((nLevel % 2 == 1) && (nLevel > 1)){
            nWinner++;  // odd number and not 1
        }
        nTotalWins = nWinner;
        nTotalLoses = gameBoard.length - nWinner;

        int tiles = 0;
        for (int i = 0; i <= (max * 100000); i++) {
            seed = randomSeed.nextInt(max);
            if (gameBoard[seed] == 0) {
                gameBoard[seed] = 1;     // winner
                tiles++;
            }
            if (tiles >= (nLoser)) {
                break;
            }
        }
    }

    public final void UpdateLevelStatus(){
        mLevelStatus = findViewById(R.id.nGameLevel);
        mLevelStatus.setText("Level : " + nLevel + "\nRemaining tiles...\nWins : " + nTotalWins + "  -  Loses : " + nTotalLoses);
    }

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableLayout l;
            ImageView iv;

            if (convertView == null) {
                iv = new ImageView(ChooserGameActivity.this);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
                l = new CheckableLayout(ChooserGameActivity.this);
                l.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,
                           GridView.LayoutParams.WRAP_CONTENT));
                l.addView(iv);
            } else {
                l = (CheckableLayout) convertView;
                iv = (ImageView) l.getChildAt(0);
            }

            //    ResolveInfo info = mApps.get(position);
            //    i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            if (!l.isChecked()){
                iv.setImageDrawable(getDrawable(R.drawable.circle_blue));
            }

            return l;
        }

        public final int getCount() {

            if (nLevel == 1) {
                return 2;
            } else {
                return nLevel * nLevel;
            }
        }

        public final Object getItem(int position) {

            return position;
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
          /*  setBackgroundDrawable(checked ?
                    getResources().getDrawable(R.drawable.yellowbackground)
                    : null);*/
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }

    public void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        //mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }
}

