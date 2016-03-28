package programmer.ie.dictator.adapter.interfaces;

import android.widget.ImageView;

public interface StaticImageLoader<T> {
    public void loadImage(T item, ImageView imageView, int position);
}
