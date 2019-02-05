package com.buryware.burywarechoose;

import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/*
 *
 * Created by Steve Stansbury on 11/13/2015.
 *
*/

public class ChooseActivity extends AppCompatActivity {

    int nLevel = 1;         // game level
    int nCells = 2;         // number of dots


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        //   startNewGame();
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        if (id == R.id.action_settings) {

            startNewGame();
            setContentView(R.layout.gamegridview);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startNewGame() {

        Intent intent = new Intent(this, ChooserGameActivity.class);
        startActivity(intent);
    }
    public int getCells() {

        return nLevel * nLevel;
    }
}
