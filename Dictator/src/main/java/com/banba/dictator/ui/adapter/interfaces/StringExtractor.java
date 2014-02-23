package com.banba.dictator.ui.adapter.interfaces;


/**
 * Interface to extract String data from a specified model object
 *
 * @param <T>
 * @author Amig
 */
public interface StringExtractor<T> {
    public String getStringValue(T item, int position);
}
