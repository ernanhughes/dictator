package com.banba.dictator.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.banba.dictator.R;
import com.banba.dictator.lib.adapter.Binder;
import com.banba.dictator.lib.adapter.SimpleAdapter;
import com.banba.dictator.lib.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.lib.adapter.interfaces.StringExtractor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import rx.util.functions.Func0;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton b = (ImageButton) rootView.findViewById(R.id.searchButton);
        final AutoCompleteTextView tv = (AutoCompleteTextView) rootView.findViewById(R.id.searchText);
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
                final SimpleAdapter<String> cardsAdapter = new SimpleAdapter<String>(getActivity(), searchResults, binder, R.layout.list_item_card);
                ListView cardsList = (ListView) rootView.findViewById(android.R.id.list);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionList);
        tv.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        return rootView;
    }
}