package com.banba.dictator.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.banba.dictator.R;
import com.banba.dictator.activity.ApacheActivity;
import com.banba.dictator.activity.BanbaActivity;
import com.banba.dictator.activity.HelpActivity;
import com.banba.dictator.lib.activity.ExceptionActivity;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }

    public void onClick(View view) {
        ImageButton button = (ImageButton) view;
        String tag = (String) button.getTag();
        if (tag.equals("Apps")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Banba+Inc."));
            startActivity(browserIntent);
        }
        if (tag.equals("Help")) {
            Intent i = new Intent(getActivity(), HelpActivity.class);
            startActivity(i);
        }
        if (tag.equals("Licence")) {
            Intent i = new Intent(getActivity(), ApacheActivity.class);
            startActivity(i);
        }
        if (tag.equals("Bug")) {
            Intent i = new Intent(getActivity(), ExceptionActivity.class);
            startActivity(i);
        }
        if (tag.equals("Rate")) {
            String packageName = getActivity().getApplicationContext().getPackageName();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            startActivity(browserIntent);
        }
        if (tag.equals("Contactus")) {
            Intent i = new Intent(getActivity(), BanbaActivity.class);
            startActivity(i);
        }
    }

}