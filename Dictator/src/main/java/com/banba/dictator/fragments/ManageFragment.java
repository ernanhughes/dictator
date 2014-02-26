package com.banba.dictator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.ui.adapter.Binder;
import com.banba.dictator.ui.adapter.TableHeaderAdapter;
import com.banba.dictator.ui.adapter.interfaces.StringExtractor;
import com.banba.dictator.ui.tablefixheaders.TableFixHeaders;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ManageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        List<Recording> items = Util.getAllRecordings(getActivity());
        List<String> headers = Arrays.asList(new String[]{
                "Name",
                "Start Time",
                "End Timer",
                "Size",
                "Location"
        });
        List<Integer> widths = Arrays.asList(new Integer[]{
                20,
                20,
                20,
                20,
                10,
                100
        });
        Binder<Recording> binder = new Binder.Builder<Recording>()
                .addString(-1, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getName());
                    }
                })
                .addString(0, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getStartTime());
                    }
                })
                .addString(1, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getEndTime());
                    }
                })
                .addString(2, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getFileSize());
                    }
                })
                .addString(3, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getFileName());
                    }
                })
                .addString(4, new StringExtractor<Recording>() {
                    @Override
                    public String getStringValue(Recording item, int position) {
                        return String.valueOf(item.getFileName());
                    }
                })
                .build();

        TableHeaderAdapter.ItemView itemView = new TableHeaderAdapter.ItemView() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public int getItemViewType(int row, int column) {
                if (row < 0) {
                    return 0;
                } else {
                    if (row % 2 == 0)
                        return 1;
                    return 2;
                }
            }

            @Override
            public int getResourceId(int row, int column) {
                final int layoutResource;
                switch (getItemViewType(row, column)) {
                    case 0:
                        layoutResource = R.layout.table_list_table_header;
                        break;
                    case 1:
                        layoutResource = R.layout.table_list_item_blue;
                        break;
                    case 2:
                        layoutResource = R.layout.table_list_item_white;
                        break;
                    default:
                        throw new RuntimeException("wtf?");
                }
                return layoutResource;
            }
        };

        TableFixHeaders tableFixHeaders = (TableFixHeaders) rootView.findViewById(R.id.history_list);
        TableHeaderAdapter adapter = new TableHeaderAdapter(getActivity(), items, binder,
                headers, widths, itemView);
        tableFixHeaders.setAdapter(adapter);
        return rootView;
    }
}
