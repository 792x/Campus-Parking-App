package com.tue.main.maps;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mert.testproj2.R;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;

public class SearchBarActivity extends AppCompatActivity {

    SearchBox search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);


        search = (SearchBox) findViewById(R.id.searchbox);

        search.enableVoiceRecognition(this);

        for (int x = 0; x < 10; x++) {
            Log.i("SEARCH", "??");

            SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(R.drawable.cast_ic_notification_0));
            search.addSearchable(option);
        }
        search.setLogoText("My App");
        search.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(SearchBarActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String s) {

            }


            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(SearchBarActivity.this, searchTerm + " Searched", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }


            @Override
            public void onSearchCleared() {

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (true && requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == SearchBarActivity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
