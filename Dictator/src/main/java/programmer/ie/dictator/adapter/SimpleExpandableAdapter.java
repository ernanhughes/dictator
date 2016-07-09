package programmer.ie.dictator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import programmer.ie.dictator.adapter.interfaces.ChildExtractor;

public class SimpleExpandableAdapter<G, C> extends BaseExpandableListAdapter {

    protected final Context mContext;
    private final int mGroupLayoutResource;
    private final int mChildLayoutResource;
    private final Binder<G> mGroupDictionary;
    private final Binder<C> mChildDictionary;
    private final ChildExtractor<G, C> mChildExtractor;
    protected List<G> mDataItems;

    public SimpleExpandableAdapter(Context context, List<G> dataItems,
                                   Binder<G> groupDictionary, Binder<C> childDictionary,
                                   int groupLayoutResource, int childLayoutResource,
                                   ChildExtractor<G, C> childExtractor) {
        this.mContext = context;
        this.mDataItems = dataItems;
        this.mGroupLayoutResource = groupLayoutResource;
        this.mChildLayoutResource = childLayoutResource;
        this.mGroupDictionary = groupDictionary;
        this.mChildDictionary = childDictionary;
        this.mChildExtractor = childExtractor;
    }


    public void updateData(ArrayList<G> dataItems) {
        this.mDataItems = dataItems;
        notifyDataSetChanged();
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(mGroupLayoutResource, parent, false);
            mGroupDictionary.bind(convertView);
        }
        final G item = getGroup(groupPosition);
        mGroupDictionary.showData(item, groupPosition, convertView);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(mChildLayoutResource, parent, false);
            mChildDictionary.bind(convertView);
        }
        final C item = getChild(groupPosition, childPosition);
        mChildDictionary.showData(item, childPosition, convertView);
        return convertView;
    }

    @Override
    public C getChild(int groupPosition, int childPosition) {
        return mChildExtractor.extractChild(mDataItems.get(groupPosition), childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildExtractor.getChildrenCount(mDataItems.get(groupPosition));
    }

    @Override
    public G getGroup(int groupPosition) {
        return mDataItems.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return (mDataItems == null) ? 0 :
                mDataItems.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
