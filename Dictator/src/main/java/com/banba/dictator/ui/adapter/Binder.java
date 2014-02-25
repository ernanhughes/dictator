package com.banba.dictator.ui.adapter;


import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.banba.dictator.ui.adapter.fields.BaseField;
import com.banba.dictator.ui.adapter.fields.CheckableField;
import com.banba.dictator.ui.adapter.fields.ConditionalVisibilityField;
import com.banba.dictator.ui.adapter.fields.DynamicImageField;
import com.banba.dictator.ui.adapter.fields.ProgressBarField;
import com.banba.dictator.ui.adapter.fields.StaticImageField;
import com.banba.dictator.ui.adapter.fields.StringField;
import com.banba.dictator.ui.adapter.interfaces.BooleanExtractor;
import com.banba.dictator.ui.adapter.interfaces.DynamicImageLoader;
import com.banba.dictator.ui.adapter.interfaces.IntegerExtractor;
import com.banba.dictator.ui.adapter.interfaces.ItemClickListener;
import com.banba.dictator.ui.adapter.interfaces.StaticImageLoader;
import com.banba.dictator.ui.adapter.interfaces.StringExtractor;

import java.util.ArrayList;
import java.util.List;

public class Binder<T> {
    private List<StringField<T>> mStringFields = null;
    private List<DynamicImageField<T>> mDynamicImageFields = null;
    private List<StaticImageField<T>> mStaticImageFields = null;
    private List<ConditionalVisibilityField<T>> mConditionalVisibilityFields = null;
    private List<ProgressBarField<T>> mProgressBarFields = null;
    private List<BaseField<T>> mBaseFields = null;
    private List<CheckableField<T>> mCheckableFields = null;

    public Binder() {
    }


    public List<StringField<T>> getStringFields() {
        return mStringFields;
    }

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public void bind(View view) {
        if (null != mStringFields)
            for (StringField<T> field : mStringFields) {
                TextView tv = get(view, field.viewResId);
                // add a typeface if the field has one
                if (field.typeface != null) {
                    tv.setTypeface(field.typeface);
                }
            }
        if (null != mDynamicImageFields)
            for (DynamicImageField<T> field : mDynamicImageFields) {
                ImageView iv = get(view, field.viewResId);
            }
        if (null != mStaticImageFields)
            for (StaticImageField<T> field : mStaticImageFields) {
                ImageView iv = get(view, field.viewResId);
            }
        if (null != mConditionalVisibilityFields)
            for (ConditionalVisibilityField<T> field : mConditionalVisibilityFields) {
                View iv = get(view, field.viewResId);
            }
        if (null != mProgressBarFields)
            for (ProgressBarField<T> field : mProgressBarFields) {
                View iv = get(view, field.viewResId);
            }
        if (null != mBaseFields)
            for (BaseField<T> field : mBaseFields) {
                View iv = get(view, field.viewResId);
            }
        if (null != mCheckableFields)
            for (CheckableField<T> field : mCheckableFields) {
                View iv = get(view, field.viewResId);
            }
    }


    public void showData(T item, int position, View view) {
        handleStringFields(item, view, position);
        handleDynamicImageFields(item, view, position);
        handleStaticImageFields(item, view, position);
        handleConditionalFields(item, view, position);
        handleProgressFields(item, view, position);
        handleBaseFields(item, view, position);
        handleCheckableFields(item, view, position);
    }

    private void handleCheckableFields(final T item, View rootView,
                                       final int position) {
        if (null != mCheckableFields)
            for (final CheckableField<T> field : mCheckableFields) {
                CompoundButton view = get(rootView, field.viewResId);
                view.setOnCheckedChangeListener(null);
                view.setChecked(field.checkedExtractor.getBooleanValue(item, position));
                setClickListener(item, position, field, view);
                if (field.checkedChangeListener != null) {
                    view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            field.checkedChangeListener
                                    .onCheckedChangedListener(item, position, compoundButton, b);
                        }
                    });
                }
            }
    }

    private void handleBaseFields(final T item, View rootView,
                                  final int position) {
        if (null != mBaseFields)
            for (final BaseField<T> field : mBaseFields) {
                View view = get(rootView, field.viewResId);
                setClickListener(item, position, field, view);
            }
    }

    private static <T> void setClickListener(final T item, final int position,
                                             final BaseField<T> field, View view) {
        if (field.clickListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    field.clickListener.onClick(item, position, view);
                }
            });
        }
    }

    private void handleProgressFields(final T item, View rootView, final int position) {
        if (null != mProgressBarFields)
            for (final ProgressBarField<T> field : mProgressBarFields) {
                ProgressBar view = get(rootView, field.viewResId);
                view.setMax(field.maxProgressExtractor.getIntValue(item, position));
                view.setProgress(field.progressExtractor.getIntValue(item, position));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        field.clickListener.onClick(item, position, view);
                    }
                });
            }
    }

    private void handleConditionalFields(T item, View rootView, int position) {
        if (null != mConditionalVisibilityFields)
            for (ConditionalVisibilityField<T> field : mConditionalVisibilityFields) {
                boolean condition = field.extractor.getBooleanValue(item, position);
                View view = get(rootView, field.viewResId);
                if (view != null) {
                    if (condition) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(field.visibilityIfFalse);
                    }
                }
                setClickListener(item, position, field, view);
            }
    }

    private void handleDynamicImageFields(T item, View rootView, int position) {
        if (null != mDynamicImageFields)
            for (DynamicImageField<T> field : mDynamicImageFields) {
                String url = field.extractor.getStringValue(item, position);
                ImageView view = get(rootView, field.viewResId);
                if (!TextUtils.isEmpty(url) && field.dynamicImageLoader != null && view != null) {
                    field.dynamicImageLoader.loadImage(url, view);
                }
                setClickListener(item, position, field, view);
            }
    }

    private void handleStaticImageFields(T item, View rootView, int position) {
        if (null != mStaticImageFields)
            for (StaticImageField<T> field : mStaticImageFields) {
                ImageView view = get(rootView, field.viewResId);
                if (item != null && field.staticImageLoader != null && view != null) {
                    field.staticImageLoader.loadImage(item, view, position);
                }
                setClickListener(item, position, field, view);
            }
    }

    private void handleStringFields(T item, View rootView, int position) {
        if (null != mStringFields)
            for (StringField<T> field : mStringFields) {
                String stringValue = field.extractor.getStringValue(item, position);
                TextView view = get(rootView, field.viewResId);
                if (!TextUtils.isEmpty(stringValue) && view != null) {
                    view.setText(stringValue);
                    view.setVisibility(View.VISIBLE);
                } else {
                    if (view != null) view.setVisibility(field.visibilityIfNull);
                }
                if (field.conditionalTextColorEntry != null) {
                    boolean condition =
                            field.conditionalTextColorEntry.getKey().getBooleanValue(item, position);
                    if (condition) {
                        view.setTextColor(field.conditionalTextColorEntry.getValue()[0]);
                    } else {
                        view.setTextColor(field.conditionalTextColorEntry.getValue()[1]);
                    }
                }

                setClickListener(item, position, field, view);
            }
    }

    public static class Builder<T> {
        Binder<T> binder = new Binder<T>();

        public static <T> List<T> add(List<T> list, T item) {
            if (list == null) {
                list = new ArrayList<T>();
                list.add(item);
            } else if (!list.contains(item)) {
                list.add(item);
            }
            return list;
        }

        public static <T> List<T> remove(List<T> list, T item) {
            if (list != null) {
                list.remove(item);
                if (list.isEmpty()) {
                    list = null;
                }
            }
            return list;
        }

        public Builder<T> addProgressBar(int viewResId, IntegerExtractor<T> progressExtractor,
                                         IntegerExtractor<T> maxProgressExtractor) {
            binder.mProgressBarFields = add(binder.mProgressBarFields, new ProgressBarField<T>(viewResId, progressExtractor, maxProgressExtractor));
            return this;
        }

        public Builder<T> addConditionalVisibility(int viewResId,
                                                   BooleanExtractor<T> extractor, int visibilityIfFalse) {
            binder.mConditionalVisibilityFields = add(binder.mConditionalVisibilityFields, new ConditionalVisibilityField<T>(viewResId, extractor, visibilityIfFalse));
            return this;
        }

        public Builder<T> addDynamicImage(int viewResId, StringExtractor<T> extractor,
                                          DynamicImageLoader dynamicImageLoader) {
            binder.mDynamicImageFields = add(binder.mDynamicImageFields, new DynamicImageField<T>(viewResId, extractor, dynamicImageLoader));
            return this;
        }

        public Builder<T> addString(int viewResId, StringExtractor<T> extractor) {
            binder.mStringFields = add(binder.mStringFields, new StringField<T>(viewResId, extractor));
            return this;
        }

        public Builder<T> addBase(int viewResId) {
            binder.mBaseFields = add(binder.mBaseFields, new BaseField<T>(viewResId));
            return this;
        }

        public Builder<T> addCheckable(int viewResId, BooleanExtractor<T> isCheckedExtractor) {
            binder.mCheckableFields = add(binder.mCheckableFields, new CheckableField<T>(viewResId, isCheckedExtractor));
            return this;
        }

        public Builder<T> addStaticImage(int viewResId, StaticImageLoader staticImageLoader) {
            binder.mStaticImageFields = add(binder.mStaticImageFields, new StaticImageField<T>(viewResId, staticImageLoader));
            return this;
        }

        public Builder<T> addBaseField(int viewResId, ItemClickListener listener) {
            BaseField<T> field = new BaseField<T>(viewResId);
            field.clickListener = listener;
            binder.mBaseFields = add(binder.mBaseFields, field);
            return this;
        }

        public Binder<T> build() {
            return binder;
        }
    }
}