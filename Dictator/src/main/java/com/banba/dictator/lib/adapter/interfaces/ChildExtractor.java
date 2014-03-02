package com.banba.dictator.lib.adapter.interfaces;

public interface ChildExtractor<G, C> {
    public C extractChild(G group, int childPosition);

    public int getChildrenCount(G group);
}
