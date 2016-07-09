package programmer.ie.dictator.adapter.interfaces;

import android.view.View;

public interface CheckedChangeListener<T> {
    public void onCheckedChangedListener(T item, int position, View view, boolean b);
}
