package com.banba.dictator.lib.autowire;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAutowireFragment extends Fragment {
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidAutowire.loadFieldsFromBundle(savedInstanceState, this, BaseAutowireFragment.class);

        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            int layoutId = AndroidAutowire.getLayoutResourceByAnnotation(this, getActivity(), BaseAutowireFragment.class);
            if (layoutId == 0) {
                return rootView;
            }
            rootView = inflater.inflate(layoutId, container, false);
        }
        AndroidAutowire.autowireFragment(this, BaseAutowireFragment.class, rootView, getActivity());
        return afterAutowire(rootView, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AndroidAutowire.saveFieldsToBundle(outState, this, BaseAutowireFragment.class);
    }

    protected abstract View afterAutowire(View rootView, ViewGroup container, Bundle savedInstanceState);
}
