package com.buryware.burywarechoose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarViewEx;
import com.applandeo.materialcalendarview.EventDay;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.buryware.burywarechoose.ChooserGameActivity.mFirebaseAnalytics;
import static com.buryware.burywarechoose.ChooserGameActivity.mFirebaseDatabaseReference;
import static com.buryware.burywarechoose.ChooserGameActivity.mFirebaseRemoteConfig;
import static com.buryware.burywarechoose.ChooserGameActivity.mFirebaseUser;

public class TournementActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private Button mJoin;
    private Button mUpdate;
    private Button mCancel;
    private Button mHints;

    private RadioGroup radioGroup;
    private ImageView mBuryware;

    private boolean bGodMode = false;
    private boolean bJoin = false;
    private boolean bUpdate = false;

    private View TitleView;
    private CalendarView calender;
    private TextView PotAmountView;
    private PopupWindow ContestDatePicker = null;

    private RecyclerView mWinnersRecyclerView;
    private RecyclerView mPlayersRecyclerView;
    private LinearLayoutManager mWinnersLayoutManager;
    private LinearLayoutManager mPlayersLayoutManager;

    public static FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
   /* public static DatabaseReference mFirebaseDatabaseReference;
    public static FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fullmessageTextView;

        public MessageViewHolder(View v) {
            super(v);

            itemView.setOnClickListener(this);

            TextView mMessageTextView = itemView.findViewById(R.id.fullmessageTextView);
            mMessageTextView.setTextColor(getResources().getColor(R.color.colorBuryware));

            mMessageTextView.setText("Test");
        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(v.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
            FriendlyMessage msg = mFirebaseAdapter.getItem(getLayoutPosition());
        }
    }

    public static final String TAG = "TournementActivity";
    public static final String MESSAGES_CHILD = "messages/games/players";
    public static final String MESSAGES_CHILD2 = "messages/games/dates";
    public static final String MESSAGES_CHILD3 = "messages/games/winners";
    public static final String MESSAGES_CHILD4 = "messages/games/pots";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private boolean bJoinMode = false;
    private boolean bUpdateMode = false;
    private boolean bRadioPicked = false;
    private boolean bBidAmount = false;

    private String selectedMoneytext = null;
    private RadioButton radioButton = null;
    private Date mselectedDate = null;
    public String mPotAmout = null;
    public int mPotTotal = 0;

    private boolean mNightMode = false;
    private Bundle extras;
    private Context context;

    private String[] DollarBids = {"1-10", "10-50", "50-200", "200-1000", ">1000" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        context = getApplicationContext();
        setContentView(R.layout.choosetournaments);

        if (extras.getBoolean("night_mode") == true) {
            setupNightMode();

        } else {
            setupDayMode();
        }

        mPlayersRecyclerView = findViewById(R.id.PlayersRecyclerView);
        mPlayersLayoutManager = new LinearLayoutManager(this);
        mPlayersLayoutManager.setStackFromEnd(true);

        mWinnersRecyclerView = findViewById(R.id.WinnersRecyclerView);
        mWinnersLayoutManager = new LinearLayoutManager(this);
        mWinnersLayoutManager.setStackFromEnd(true);

        TitleView = this.getWindow().getDecorView();

        mJoin = findViewById(R.id.joinbutton);
        mJoin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DoJoin();
            }
        });

        mUpdate = findViewById(R.id.updatebutton);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DoUpdate();
            }
        });

        mCancel = findViewById(R.id.cancelbutton);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DoCancel();
            }
        });

        mHints = findViewById(R.id.hintsbutton);
        mHints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DoHints();
            }
        });

        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        mBuryware = findViewById(R.id.BurywareImageView);
        mBuryware.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ip.contentEquals("192.168.1.145")) {
                    bGodMode = !bGodMode;
                }

                if (bGodMode) {

                    setupGodMode();

                } else {

                    extras = getIntent().getExtras();
                    if (extras.getBoolean("night_mode") == true) {
                        setupNightMode();

                    } else {
                        setupDayMode();
                    }
                }
            }
        });

        radioGroup = findViewById(R.id.radioButtonGroup1);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
        //    ((RadioButton) radioGroup.getChildAt(i)).setText((String) values(i));
        //    ((RadioButton) radioGroup.getChildAt(i)).setText("$0");
            ((RadioButton) radioGroup.getChildAt(i)).setText(DollarBids[i]);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int radioButtonID = radioGroup.getCheckedRadioButtonId();

                mPotAmout = (DollarBids[(radioButtonID - R.id.radioButton1)]);
                PotAmountView.setText(mPotAmout);

                bBidAmount = true;
                bRadioPicked = true;
            }
        });

        mPlayersRecyclerView = findViewById(R.id.PlayersRecyclerView);
        mPlayersLayoutManager = new LinearLayoutManager(this);
        mPlayersLayoutManager.setStackFromEnd(true);

        mWinnersRecyclerView = findViewById(R.id.winnersRecyclerView);
        mWinnersLayoutManager = new LinearLayoutManager(this);
        mWinnersLayoutManager.setStackFromEnd(true);

        PotAmountView = findViewById(R.id.pottotalstatus);
        mPotAmout = getString(R.string.pot_amount_zero_text);
        PotAmountView.setText(mPotAmout);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API).build();

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

        FirebaseRecyclerOptions<FriendlyMessage> options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>().setQuery(messagesRef, parser).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder, int position, FriendlyMessage friendlyMessage) {

                if (friendlyMessage.getUserName() != null) {
                    viewHolder.fullmessageTextView.setText(friendlyMessage.getUserName());
                    viewHolder.fullmessageTextView.setText(friendlyMessage.getUserName() + friendlyMessage.getEmail());
                    viewHolder.fullmessageTextView.setTextColor(getResources().getColor(R.color.colorBuryware));

                    // write this message to the on-device index
           //         FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));

                    // log a view action on it
       //             FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mPlayersLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mPlayersLayoutManager.scrollToPosition(positionStart);
                }
            }
        });

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder().build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 120L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        // setup our calendar entries
        SetupCalendar();
    }

    private Action getMessageViewAction(FriendlyMessage friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION).setObject(friendlyMessage.getUserName(), MESSAGE_URL.concat(friendlyMessage.getId())).setMetadata(new Action.Metadata.Builder().setUpload(false)).build();
    }

    private Indexable getMessageIndexable(FriendlyMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder().setIsSelf(mFirebaseUser.equals(friendlyMessage.getUserName())).setName(friendlyMessage.getUserName()).setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder().setName(mFirebaseUser.toString()).setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder().setName("Sending a friendly Choose data stream...").setUrl(MESSAGE_URL.concat(friendlyMessage.getId())).setSender(sender).setRecipient(recipient).build();

        return messageToIndex;
    }

    @SuppressLint("ResourceAsColor")
    public void setupDayMode() {

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);

        ImageView title = findViewById(R.id.titleview);
        title.setImageResource(R.drawable.tournament);

        //LinearLayout lv = findViewById(R.id.layout4);
        //lv.setBackgroundColor(R.color.colorBuryware);

        radioGroup = findViewById(R.id.radioButtonGroup1);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            //    ((RadioButton) radioGroup.getChildAt(i)).setText((String) values(i));
            //    ((RadioButton) radioGroup.getChildAt(i)).setText("$0");
            ((RadioButton) radioGroup.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorBuryware));
            ((RadioButton) radioGroup.getChildAt(i)).setHighlightColor(getResources().getColor(R.color.colorBuryware));
        }

        //lv = findViewById(R.id.layout6);
        //lv.setBackgroundColor(R.color.colorBuryware);

        view.refreshDrawableState();
    }

    public void setupNightMode() {

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorBuryware));

        ImageView title = findViewById(R.id.titleview);
        title.setImageResource(R.drawable.tourneynm);

        LinearLayout lv = findViewById(R.id.layout4);
        lv.setBackgroundColor(getResources().getColor(R.color.colorBuryware));

        radioGroup = findViewById(R.id.radioButtonGroup1);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            //    ((RadioButton) radioGroup.getChildAt(i)).setText((String) values(i));
            //    ((RadioButton) radioGroup.getChildAt(i)).setText("$0");
            ((RadioButton) radioGroup.getChildAt(i)).setTextColor(Color.WHITE);

        }
        view.refreshDrawableState();
    }

    public void setupGodMode() {

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorGodMode));

        ImageView title = findViewById(R.id.titleview);
        title.setImageResource(R.drawable.tourneynm);

        LinearLayout lv = findViewById(R.id.layout4);
        lv.setBackgroundColor(getResources().getColor(R.color.colorGodMode));

        radioGroup = findViewById(R.id.radioButtonGroup1);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            //    ((RadioButton) radioGroup.getChildAt(i)).setText((String) values(i));
            //    ((RadioButton) radioGroup.getChildAt(i)).setText("$0");
            ((RadioButton) radioGroup.getChildAt(i)).setTextColor(Color.WHITE);
        }

        view.refreshDrawableState();
    }

    public int getPotTotal(String strBidAmount) {
        //
        //  If bid amount is null, return ALL, else just the specified one
        //

        mPotTotal = -1;

     /*   String mStartDay = calender.getFirstofMonth().toString();
        String mEndDay = calender.getFirstofMonth().toString();

        final Query queryRef = mFirebaseDatabaseReference.orderByChild("messages")
                .queryStartingAtValue(mStartDay)
                .queryEndingAtValue(mEndDay).limitToLast(100);
        queryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    if (friendlyMessage != null) {

                        mPotTotal += Integer.parseInt(friendlyMessage.getEntryAmount().toString());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


       mFirebaseDatabaseReference.child("Game_dates")
                .queryOrderedByChild(posterId)
                .queryStartingAtValue(getFirstofMonth().toString())
                .queryEndingAtValue(getLastofMonth().toString())
                .observeEventType(.Value, withBlock: { snapshot in print(snapshot.key)
        })*/

        return mPotTotal;
    }

    @Override
    public void onPause() {
       /* if (mAdView != null) {
            mAdView.pause();
        }*/
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
     /*   if (mAdView != null) {
            mAdView.resume();
        }*/
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
     //   if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
      //  }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                mFirebaseRemoteConfig.activate();
                applyRetrievedLengthLimit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // There has been an error fetching the config
                Log.w(TAG, "Error fetching config", e);
                applyRetrievedLengthLimit();
            }
        });
    }

    public void SetupCalendar() {

        calender = findViewById(R.id.calendar);

        // Add Listener in calendar
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            // In this Listener have one method
            // and in this method we will
            // get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                // Store the value of date with
                // format in String type Variable
                // Add 1 in month because month
                // index is start with 0
                String Date = (month + 1) + "-" + dayOfMonth + "-" + year;

                if (bGodMode) {

                    DoAddContestCalendarEvents(Date);

                } else if (bJoinMode && bRadioPicked && bBidAmount) {

                    DoNewPlayerCalendarEvents(Date);

                } else if (bUpdateMode) {

                    DoUpdatePlayerCalendarEvents(Date);

                } else {

                    if (bJoinMode && !bRadioPicked) {

                        DoErrorAmount(getString(R.string.ErrorJoinSession));

                    } else if (bJoinMode && !bBidAmount) {

                        DoErrorAmount(getString(R.string.ErrorBidAmount));
                    }
                }
            }
        });
    }

    public void DoErrorAmount(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.ErrorJoinSession))
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog,int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoErrorSelect(String strErrorMsg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(strErrorMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoJoin() {

        mJoin = findViewById(R.id.joinbutton);
        mJoin.setText(getString(R.string.ADD));
        mJoin.setBackgroundColor(Color.LTGRAY);
        mJoin.setTextColor(getResources().getColor(R.color.colorHighlight));
        bJoinMode = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.content_datepicker, null); //custom_layout is your xml file which contains popuplayout
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.contest_datepicker);

        // Creating the PopupWindow
        ContestDatePicker = new PopupWindow(context);
        ContestDatePicker.setContentView(layout);
        ContestDatePicker.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        ContestDatePicker.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ContestDatePicker.setFocusable(true);
        ContestDatePicker.setTouchable(true);
        ContestDatePicker.setOutsideTouchable(false);
        ContestDatePicker.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 124;
        ContestDatePicker.showAtLocation(layout, Gravity.NO_GRAVITY, OFFSET_X, OFFSET_Y);

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
   //     events.add(new EventDay(calendar, R.drawable.circle_green));

        CalendarViewEx calendarView = (CalendarViewEx) findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        ContestDatePicker.update();
    }

    public void DoUpdate() {

        mUpdate = findViewById(R.id.updatebutton);
        mUpdate.setText(getString(R.string.END));
        mUpdate.setTextColor(getResources().getColor(R.color.colorHighlight));
        bUpdateMode = true;
    }

    public void DoHints() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.RulesHint))
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoCancel() {

        mJoin.setText("JOIN");
        mJoin.setTextColor(Color.WHITE);

        mUpdate.setText("UPDATE");
        mUpdate.setTextColor(Color.WHITE);

        if (bRadioPicked) {
            radioButton.setBackgroundColor(Color.WHITE);
            radioButton.setTextColor(Color.BLACK);
            radioButton = null;
        }

        bRadioPicked = bUpdateMode = bJoinMode = false;

        mPotAmout = getString(R.string.pot_amount_zero_text);
        PotAmountView.setText(mPotAmout);
        mPotTotal = 0;
    }

    public void DoNewPlayerCalendarEvents(final String Date) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoDeleteDatetoFirebase(Date);
            }
        });
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoCancel();
                dialog.cancel();
            }
        });
        builder.setMessage("The brand new date : " + Date)
                .setCancelable(false)
                .setNeutralButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        UpdateUserInfoinFirebase(mFirebaseUser.toString());
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoUpdatePlayerCalendarEvents(final String Date) {

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoCancel();
                dialog.cancel();
            }
        });
        builder.setMessage("The updated date : " + Date)
                .setCancelable(false)
                .setNeutralButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        UpdateUserInfoinFirebase(mFirebaseUser.toString());
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoAddContestCalendarEvents(final String Date) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoDeleteDatetoFirebase(Date);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoCancel();
                dialog.cancel();
            }
        });
        builder.setMessage("The new EVENT date : " + Date)
                .setCancelable(false)
                .setNeutralButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DoStoreDatetoFirebase(Date);
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void DoStoreDatetoFirebase(final String newDate) {

        mFirebaseDatabaseReference.child(MESSAGES_CHILD2).push().setValue(newDate);
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
    }

    public void UpdateUserInfoinFirebase(final String mUser) {

        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(mUser);
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
    }

    public void DoDeleteDatetoFirebase(final String deleteDate) {

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("messages/games/dates").child(deleteDate);
        mPostReference.removeValue();
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
    }

    public void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        //mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }
}
