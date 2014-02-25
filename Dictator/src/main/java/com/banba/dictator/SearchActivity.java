package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.banba.dictator.ui.adapter.Binder;
import com.banba.dictator.ui.adapter.SimpleAdapter;
import com.banba.dictator.ui.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.ui.adapter.interfaces.StringExtractor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import rx.util.functions.Func0;

/**
 * Created by Ernan on 30/01/14.
 * Copyrite Banba Inc. 2013.
 */
public class SearchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.search));
        ImageButton b = (ImageButton) findViewById(R.id.searchButton);
        final AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.searchText);

        final Func0 searchFunction = new Func0() {
            @Override
            public Object call() {
                final Hashtable<String, Object> searchResultItems = new Hashtable<String, Object>();
                String searchText = tv.getText().toString().toLowerCase();
                Binder<String> binder = new Binder.Builder<String>()
                        .addString(android.R.id.title, new StringExtractor<String>() {
                            @Override
                            public String getStringValue(String item, int position) {
                                return item;
                            }
                        })
                        .addString(android.R.id.content, new StringExtractor<String>() {
                            @Override
                            public String getStringValue(String item, int position) {
                                return item;
                            }
                        })
                        .addStaticImage(android.R.id.icon, new StaticImageLoader<String>() {
                            @Override
                            public void loadImage(String item, ImageView imageView, int position) {
                                Object result = searchResultItems.get(item);
                            }
                        }).build();
                List<String> searchResults = new ArrayList<String>(searchResultItems.keySet());
                final SimpleAdapter<String> cardsAdapter = new SimpleAdapter<String>(SearchActivity.this, searchResults, binder, R.layout.list_item_card);
                ListView cardsList = (ListView) findViewById(android.R.id.list);
                cardsList.setAdapter(cardsAdapter);
                cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String) searchResultItems.keySet().toArray()[position];
                        Object result = searchResultItems.get(item);
                    }
                });
                return null;
            }
        };


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFunction.call();
            }
        });
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFunction.call();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        List<String> suggestionList = new ArrayList<String>();
        tv.setThreshold(3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suggestionList);
        tv.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }


}
