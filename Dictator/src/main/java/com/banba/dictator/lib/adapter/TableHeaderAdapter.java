package com.banba.dictator.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.banba.dictator.R;
import com.banba.dictator.lib.adapter.fields.StringField;
import com.banba.dictator.lib.tablefixheaders.adapters.TableAdapter;

import java.util.List;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class TableHeaderAdapter<T> extends SimpleAdapter<T> implements TableAdapter {
    protected List<String> headers;
    protected List<Integer> widths;
    protected ItemView itemView;

    public TableHeaderAdapter(Context context, List<T> dataItems,
                              Binder<T> binder, List<String> headers, List<Integer> widths,
                              ItemView itemView) {
        super(context, dataItems, binder, 0);
        this.headers = headers;
        this.widths = widths;
        this.itemView = itemView;
    }

    @Override
    public int getRowCount() {
        return super.getCount();
    }

    @Override
    public int getColumnCount() {
        return headers.size() - 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(itemView.getResourceId(row, column), parent, false);
            mBinder.bind(convertView);
        }
        if (row == -1) {
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            String header = headers.get(column + 1);
            tv.setText(header);
        } else {
            final T item = getItem(row);
            StringField<T> field = mBinder.getStringFields().get(column + 1);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            String val = field.extractor.getStringValue(item, column + 1);
            tv.setText(val);
        }
        return convertView;
    }

    @Override
    public int getWidth(int column) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(widths.get(column + 1) * density);
    }

    @Override
    public int getHeight(int row) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(mContext.getResources().getDimensionPixelSize(R.dimen.table_height) * density);
    }

    @Override
    public int getItemViewType(int row, int column) {
        return itemView.getItemViewType(row, column);
    }

    @Override
    public int getViewTypeCount() {
        return itemView.getCount();
    }

    public interface ItemView {
        int getCount();

        int getItemViewType(int row, int column);

        int getResourceId(int row, int column);
    }
}
