package programmer.ie.dictator.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.R;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.adapter.Binder;
import programmer.ie.dictator.adapter.SimpleAdapter;
import programmer.ie.dictator.adapter.interfaces.BooleanExtractor;
import programmer.ie.dictator.adapter.interfaces.CheckedChangeListener;
import programmer.ie.dictator.adapter.interfaces.StaticImageLoader;
import programmer.ie.dictator.adapter.interfaces.StringExtractor;
import programmer.ie.dictator.data.Recording;
import programmer.ie.dictator.event.PlayRecordingEvent;
import programmer.ie.dictator.util.DateTimeUtil;

public class ManageFragment extends Fragment {
    SimpleAdapter<Recording> adapter;
    final BaseSpringSystem mSpringSystem = SpringSystem.create();
    ExampleSpringListener mSpringListener;
    Spring mScaleSpring;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        final List<Recording> items = Util.getAllRecordings(getActivity());
        final ListView listView = (ListView) rootView.findViewById(R.id.listview);
        final List<Recording> selected = new ArrayList<>();
        mScaleSpring = mSpringSystem.createSpring();

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

        adapter = new SimpleAdapter<>(getActivity(), items, binder, R.layout.list_item_manage);
        listView.setAdapter(adapter);
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
                                    adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton sortButton = (ImageButton) rootView.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Collections.reverse(items);
                adapter.notifyDataSetChanged();
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
        mSpringListener = new ExampleSpringListener(detailsButton);

        // Add an OnTouchListener to the root view.
        detailsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mScaleSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mScaleSpring.setEndValue(0);
                        break;
                }
                return true;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recording recording = adapter.getItem(position);
                EventBus.getDefault().post(new PlayRecordingEvent(recording));
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add a listener to the spring when the Activity resumes.
        mScaleSpring.addListener(mSpringListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove the listener to the spring when the Activity pauses.
        mScaleSpring.removeListener(mSpringListener);
    }


    private class ExampleSpringListener extends SimpleSpringListener {
        View view;

        ExampleSpringListener(View view){
            this.view = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            // On each update of the spring value, we adjust the scale of the image view to match the
            // springs new value. We use the SpringUtil linear interpolation function mapValueFromRangeToRange
            // to translate the spring's 0 to 1 scale to a 100% to 50% scale range and apply that to the View
            // with setScaleX/Y. Note that rendering is an implementation detail of the application and not
            // Rebound itself. If you need Gingerbread compatibility consider using NineOldAndroids to update
            // your view properties in a backwards compatible manner.
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
            view.setScaleX(mappedValue);
            view.setScaleY(mappedValue);
        }
    }

}
