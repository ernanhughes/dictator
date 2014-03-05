package com.banba.dictator.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.lib.adapter.Binder;
import com.banba.dictator.lib.adapter.SimpleAdapter;
import com.banba.dictator.lib.adapter.interfaces.BooleanExtractor;
import com.banba.dictator.lib.adapter.interfaces.CheckedChangeListener;
import com.banba.dictator.lib.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.lib.adapter.interfaces.StringExtractor;
import com.banba.dictator.lib.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        final ListView listView = (ListView) rootView.findViewById(R.id.listview);
        final List<Recording> selected = new ArrayList<Recording>();
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
                        return "Length: " + Util.getRecordingLength(item) + " Date: " +
                                DateTimeUtil.shortDateFormat(item.getStartTime());
                    }
                })
                .addCheckable(R.id.check, new BooleanExtractor<Recording>() {
                            @Override
                            public boolean getBooleanValue(Recording item, int position) {
                                return selected.contains(item);
                            }
                        }, new CheckedChangeListener<Recording>() {
                            @Override
                            public void onCheckedChangedListener(Recording recording, int position, View view, boolean b) {
                                if (selected.contains(recording)) {
                                    selected.remove(selected.indexOf(recording));
                                } else {
                                    selected.add(recording);
                                }
                            }
                        }
                )
                .addStaticImage(android.R.id.icon, new StaticImageLoader<Recording>() {
                    @Override
                    public void loadImage(Recording item, ImageView imageView, int position) {
                        Drawable d = Util.getImage(getActivity(), item);
                        imageView.setImageDrawable(d);
                    }
                }).build();

        cardsAdapter = new SimpleAdapter<Recording>(getActivity(), items, binder, R.layout.list_item_manage);
        listView.setAdapter(cardsAdapter);
        ImageButton editButton = (ImageButton) rootView.findViewById(R.id.editButton);
        editButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (final Recording recording : selected) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Rename recording");
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
            }
        });

        ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (final Recording recording : selected) {
                    Util.deleteRecording(getActivity(), recording);
                    items.remove(items.indexOf(recording));
                }
                selected.clear();
                cardsAdapter.notifyDataSetChanged();
            }
        });

        ImageButton sortButton = (ImageButton) rootView.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Collections.reverse(items);
                cardsAdapter.notifyDataSetChanged();
            }
        });

        ImageButton detailsButton = (ImageButton) rootView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (final Recording recording : selected) {
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_details);
                    dialog.setTitle(recording.getName());

                    TextView text = (TextView) dialog.findViewById(R.id.startTime);
                    text.setText(DateTimeUtil.normalDateFormat(recording.getStartTime()));

                    text = (TextView) dialog.findViewById(R.id.endTime);
                    text.setText(DateTimeUtil.normalDateFormat(recording.getEndTime()));

                    text = (TextView) dialog.findViewById(R.id.fileSize);
                    text.setText(String.valueOf(recording.getFileSize()) + " KB");

                    text = (TextView) dialog.findViewById(R.id.filePath);
                    text.setText(recording.getFileName());
                    dialog.show();
                }
            }
        });

        ImageButton shareButton = (ImageButton) rootView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (final Recording recording : selected) {
                    Uri uri = Uri.parse(recording.getFileName());
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Dictation"));
                }
            }
        });
        return rootView;
    }
}
