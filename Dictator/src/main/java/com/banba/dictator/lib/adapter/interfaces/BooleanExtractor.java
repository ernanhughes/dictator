package com.banba.dictator.lib.adapter.interfaces;


public interface BooleanExtractor<T> {
    public boolean getBooleanValue(T item, int position);
}
