package com.banba.dictator.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.event.PlayRecordingEvent;
import com.banba.dictator.lib.L;
import com.banba.dictator.lib.adapter.Binder;
import com.banba.dictator.lib.adapter.SimpleAdapter;
import com.banba.dictator.lib.adapter.interfaces.ItemClickListener;
import com.banba.dictator.lib.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.lib.adapter.interfaces.StringExtractor;
import com.banba.dictator.lib.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayListFragment extends Fragment {
    SimpleAdapter<Recording> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview, container, false);
        final List<Recording> items = new ArrayList<Recording>();
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
                        return "Len: " + Util.getRecordingLength(item) + " Date: " +
                                DateTimeUtil.shortDateFormat(item.getStartTime());
                    }
                })
                .addBaseField(android.R.id.button1, new ItemClickListener() {
                    @Override
                    public void onClick(Object item, int position, View view) {
                        Recording recording = (Recording) item;
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
                })
                .addStaticImage(android.R.id.icon, new StaticImageLoader<Recording>() {
                    @Override
                    public void loadImage(Recording item, ImageView imageView, int position) {
                        Drawable d = Util.getImage(getActivity(), item);
                        imageView.setImageDrawable(d);
                    }
                }).build();

        adapter = new SimpleAdapter<Recording>(getActivity(), items, binder, R.layout.list_item_card);
        cardsList.setAdapter(adapter);
        //TODO someday when you figure out exactly how to do this better make it better
        Observable<List<Recording>> obs = Observable.create(new Observable.OnSubscribeFunc<List<Recording>>() {
            @Override
            public Subscription onSubscribe(final Observer<? super List<Recording>> observer) {
                final Handler handler = new Handler();
                final Thread t = new Thread(new Runnable() {
                    public void run() {
                        final List<Recording> results = Util.getAllRecordings(getActivity());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                observer.onNext(results);
                                observer.onCompleted();
                            }
                        });
                    }
                });
                t.start();
                return new Subscription() {
                    public void unsubscribe() {
                        L.w("Forcibly un-subscribed");
                    }
                };
            }
        });
        Subscription subscription = obs
                .subscribe(new Observer<List<Recording>>() {
                    @Override
                    public void onNext(List<Recording> values) {
                        items.addAll(values);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
        subscription.unsubscribe();

        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new PlayRecordingEvent(items.get(position)));
            }
        });

        return rootView;
    }
}
