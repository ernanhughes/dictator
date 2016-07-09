package programmer.ie.dictator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import programmer.ie.dictator.adapter.interfaces.AdapterFilter;
import programmer.ie.dictator.adapter.interfaces.ResourceLoader;
import programmer.ie.dictator.util.L;

public class SimpleAdapter<T> extends BaseAdapter implements Filterable {
    protected final Context mContext;
    protected final List<Integer> mLayoutResourceIds;
    protected final Binder<T> mBinder;
    protected List<T> mDataItems;
    protected Filter mFilter;

    public SimpleAdapter(Context context, List<T> dataItems,
                         Binder<T> binder, Integer... layoutResourceIds) {
        this.mContext = context;
        this.mDataItems = dataItems;
        this.mLayoutResourceIds = new ArrayList<Integer>(Arrays.asList(layoutResourceIds));
        this.mBinder = binder;
    }

    @Override
    public int getViewTypeCount() {
        return mLayoutResourceIds.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mLayoutResourceIds.size() == 1) {
            return mLayoutResourceIds.get(0);
        }
        T item = getItem(position);
        if (item instanceof ResourceLoader) {
            int id = ((ResourceLoader) item).getLayout();
            return mLayoutResourceIds.indexOf(id);
        }
        L.e("Ambigouous layout for item");
        // uber hack assume we have a header than an item...
        return position == 0 ? 0 : 1;
    }

    @Override
    public int getCount() {
        return (mDataItems == null || mBinder == null)
                ? 0 : mDataItems.size();
    }

    @Override
    public T getItem(int position) {
        return mDataItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            int resourceId = getItemViewType(position);
            convertView = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
            mBinder.bind(convertView);
        }
        final T item = getItem(position);
        mBinder.showData(item, position, convertView);
        return convertView;
    }

    public void updateData(ArrayList<T> dataItems) {
        mDataItems = dataItems;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public SimpleAdapter<T> filter(final AdapterFilter<T> filter, String text) {
        if (filter == null) {
            throw new IllegalArgumentException("Cannot pass a null filter to SimpleAdapter");
        }
        mFilter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                @SuppressWarnings("unchecked") ArrayList<T> list = (ArrayList<T>) results.values;
                mDataItems = list;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<T> filtered = filter.filter(constraint.toString(), mDataItems);
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }
        };
        return this;
    }
}
