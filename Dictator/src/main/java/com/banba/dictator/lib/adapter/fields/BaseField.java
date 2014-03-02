package com.banba.dictator.lib.adapter.fields;

import com.banba.dictator.lib.adapter.interfaces.ItemClickListener;

public class BaseField<T> {
    public int viewResId;
    public ItemClickListener<T> clickListener;

    public BaseField(int viewResId) {
        this.viewResId = viewResId;
    }

    public BaseField<T> onClick(ItemClickListener<T> onClickListener) {
        clickListener = onClickListener;

        return this;
    }
}