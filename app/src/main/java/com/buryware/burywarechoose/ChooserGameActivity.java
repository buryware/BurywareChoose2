package com.buryware.burywarechoose;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.Random;

import static android.R.attr.max;

/**
 *
 * Created by Steve Stansbury on 11/13/2015.
 *
 */
public class ChooserGameActivity extends Activity {

    private int nLevel = 1;
    private int nTotalWins, nTotalLoses, nWins, nLoser, nLoses, nWinner = 0;
    private int [] gameBoard = null;
    private boolean bLevelUp = false;

    private Random randomSeed;
    private GridView mGrid = null;
    private Button mResetButton = null;
    private TextView mGameStatus = null;
    private TextView mLevelStatus = null;
    private boolean mGameOver = false;

 //   private List<ResolveInfo> mApps;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gamegridview);

        newGameGrid();

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
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
                    mGameStatus = (TextView) findViewById(R.id.statusline);
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

                        mGameStatus.setText(" ");
                    }
                }
            }
        });

        mResetButton = (Button) findViewById(R.id.resetbutton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                resetGame();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ChooserGame Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void newGameGrid() {
        mGrid = (GridView) findViewById(R.id.gamegrid);

        if (bLevelUp) {
            bLevelUp = false;
            nLevel++;
        }

        mGrid.setNumColumns(nLevel);
        mGrid.setAdapter(new AppsAdapter());
        mGrid.setHorizontalSpacing(10);

        ViewGroup.LayoutParams lp = mGrid.getLayoutParams();
        if (lp.width < (nLevel * 48 * 4)) {
            mGrid.setLayoutParams(new LinearLayout.LayoutParams((nLevel * 48 * 4), lp.height));
        }
        if (nLevel == 1) {
            mGrid.setVerticalSpacing((lp.height / 4) / 2);
        } else {
            mGrid.setVerticalSpacing((lp.height /4) / nLevel);
        }

        mGrid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        buildGameGrid();

        mGameStatus = (TextView) findViewById(R.id.statusline);
        mGameStatus.setText(R.string.action_play);
        UpdateLevelStatus();
    }

    public void resetGame() {
        nWins = nLoses = 0;
        newGameGrid();
    }

    public void startOver() {
        nLevel = 1;
        resetGame();
    }

    public void buildGameGrid() {
        if (gameBoard != null) {
        }
        {
//                destroy(gameBoard);
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
        mLevelStatus = (TextView) findViewById(R.id.nGameLevel);
        mLevelStatus.setText("Level : " + nLevel + "\nWins : " + nTotalWins + "      Loses : " + nTotalLoses);
    }

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableLayout l;
            ImageView i;

            if (convertView == null) {
                i = new ImageView(ChooserGameActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
                l = new CheckableLayout(ChooserGameActivity.this);
                l.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,
                           GridView.LayoutParams.WRAP_CONTENT));
                l.addView(i);
            } else {
                l = (CheckableLayout) convertView;
                i = (ImageView) l.getChildAt(0);
            }

            //    ResolveInfo info = mApps.get(position);
            //    i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            if (!l.isChecked()){
                i.setImageDrawable(getDrawable(R.drawable.circle_blue));
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
    //        return mApps.get(position);
     //       return gameBoard[position];
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

    private class LayoutParams {
    }
}
