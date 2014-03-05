package com.banba.dictator.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.event.PlayRecordingEvent;
import com.banba.dictator.lib.ColorUtil;
import com.banba.dictator.lib.adapter.Binder;
import com.banba.dictator.lib.adapter.SimpleAdapter;
import com.banba.dictator.lib.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.lib.adapter.interfaces.StringExtractor;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ManageFragment extends Fragment {
    SimpleAdapter<Recording> cardsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        final List<Recording> items = Util.getAllRecordings(getActivity());
        final ListView cardsList = (ListView) rootView.findViewById(R.id.listview);
        Binder<Recording> binder = new Binder.Builder<Recording>()
                .addString(android.R.id.title, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return item.getName();
                    }
                })
                .addString(android.R.id.content, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return Util.getShortName(item.getFileName());
                    }
                })
                .addStaticImage(android.R.id.icon, new StaticImageLoader<Recording>() {
                    @Override
                    public void loadImage(Recording item, ImageView imageView, int position) {
                        Drawable d = Util.getImage(getActivity(), (Recording) item);
                        imageView.setImageDrawable(d);
                    }
                }).build();

        cardsAdapter = new SimpleAdapter<Recording>(getActivity(), items, binder, R.layout.list_item_manage);
        cardsList.setAdapter(cardsAdapter);
        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv = (ImageView) view.findViewById(android.R.id.icon);
                ColorUtil.applyTempNegativeColorFilter(iv.getDrawable());
                EventBus.getDefault().post(new PlayRecordingEvent(items.get(position)));
            }
        });

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome-webfont.ttf");

        final Recording selectedItem = null;
        Button editButton = (Button) rootView.findViewById(R.id.editButton);
        setFont(editButton, tf);
        editButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Recording recording = (Recording) selectedItem;
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Rename file");
                alert.setMessage("Rename file: " + recording.getName());
                final EditText input = new EditText(getActivity());
                alert.setView(input);
                alert.setPositiveButton("Rename",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                String value = input.getText().toString();
                                Util.renameRecording(getActivity(), recording, value);
                                cardsAdapter.notifyDataSetChanged();
                            }
                        }).create().show();
            }
        });

        Button deleteButton = (Button) rootView.findViewById(R.id.deleteButton);
        setFont(deleteButton, tf);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        });

        Button playButton = (Button) rootView.findViewById(R.id.playButton);
        setFont(playButton, tf);
        playButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        });

        Button detailsButton = (Button) rootView.findViewById(R.id.detailsButton);
        setFont(editButton, tf);
        editButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        });

        Button shareButton = (Button) rootView.findViewById(R.id.shareButton);
        setFont(shareButton, tf);
        shareButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        });
        return rootView;
    }

    public void setFont(Button b, Typeface tf) {
        b.setTypeface(tf, Typeface.BOLD);
    }
}
