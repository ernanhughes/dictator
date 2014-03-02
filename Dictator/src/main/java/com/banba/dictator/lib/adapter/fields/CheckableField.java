package com.banba.dictator.lib.adapter.fields;

import com.banba.dictator.lib.adapter.interfaces.BooleanExtractor;
import com.banba.dictator.lib.adapter.interfaces.CheckedChangeListener;

public class CheckableField<T> extends BaseField<T> {

    public final BooleanExtractor<T> checkedExtractor;
    public CheckedChangeListener<T> checkedChangeListener;

    public CheckableField(int viewResId, BooleanExtractor<T> isCheckedExtractor) {
        super(viewResId);
        checkedExtractor = isCheckedExtractor;
    }

    public void setCheckedChangeListener(CheckedChangeListener<T> checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }
}
