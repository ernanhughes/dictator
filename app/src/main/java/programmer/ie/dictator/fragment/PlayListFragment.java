package programmer.ie.dictator.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.R;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.adapter.Binder;
import programmer.ie.dictator.adapter.SimpleAdapter;
import programmer.ie.dictator.adapter.interfaces.ItemClickListener;
import programmer.ie.dictator.adapter.interfaces.StaticImageLoader;
import programmer.ie.dictator.adapter.interfaces.StringExtractor;
import programmer.ie.dictator.data.Recording;
import programmer.ie.dictator.event.PlayRecordingEvent;
import programmer.ie.dictator.util.DateTimeUtil;

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
//        //TODO someday when you figure out exactly how to do this better make it better
//        Observable<List<Recording>> obs = Observable.create(new Observable.OnSubscribe<List<Recording>>() {
//            @Override
//            public Subscription onSubscribe(final Observer<? super List<Recording>> observer) {
//                final Handler handler = new Handler();
//                final Thread t = new Thread(new Runnable() {
//                    public void run() {
//                        final List<Recording> results = Util.getAllRecordings(getActivity());
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                observer.onNext(results);
//                                observer.onCompleted();
//                            }
//                        });
//                    }
//                });
//                t.start();
//                return new Subscription() {
//                    public void unsubscribe() {
//                        L.w("Forcibly un-subscribed");
//                    }
//                };
//            }
//        });
//        Subscription subscription = obs
//                .subscribe(new Observer<List<Recording>>() {
//                    @Override
//                    public void onNext(List<Recording> values) {
//                        items.addAll(values);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                });
//        subscription.unsubscribe();

        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new PlayRecordingEvent(items.get(position)));
            }
        });

        return rootView;
    }
}
