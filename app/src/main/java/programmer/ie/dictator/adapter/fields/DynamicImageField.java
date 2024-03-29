package programmer.ie.dictator.adapter.fields;

import programmer.ie.dictator.adapter.interfaces.DynamicImageLoader;
import programmer.ie.dictator.adapter.interfaces.ItemClickListener;
import programmer.ie.dictator.adapter.interfaces.StringExtractor;


/**
 * This class allows enable fetching images from the web in a ListView.
 *
 * @param <T>
 * @author Ami G
 */
public class DynamicImageField<T> extends BaseStringField<T> {

    public DynamicImageLoader dynamicImageLoader;

    /**
     * @param viewResId          - The resource ID of the view you want to bind to (Example:
     *                           R.id.image).
     * @param extractor          - An implementation that will extract the URL from the model
     *                           object.
     * @param dynamicImageLoader - A previously set-up image fetcher that will load images from
     *                           the provided URL.
     */
    public DynamicImageField(int viewResId, StringExtractor<T> extractor,
                             DynamicImageLoader dynamicImageLoader) {
        super(viewResId, extractor);
        this.dynamicImageLoader = dynamicImageLoader;
    }

    @Override
    public DynamicImageField<T> onClick(ItemClickListener<T> onClickListener) {

        return (DynamicImageField<T>) super.onClick(onClickListener);
    }
}
