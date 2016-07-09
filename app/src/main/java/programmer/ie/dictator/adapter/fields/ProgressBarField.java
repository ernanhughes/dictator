package programmer.ie.dictator.adapter.fields;

import programmer.ie.dictator.adapter.interfaces.IntegerExtractor;
import programmer.ie.dictator.adapter.interfaces.ItemClickListener;


/**
 * A field to handle progress bar information. Good for showing user progress.
 *
 * @param <T>
 * @author Ami
 */
public class ProgressBarField<T> extends BaseField<T> {
    public IntegerExtractor<T> progressExtractor;
    public IntegerExtractor<T> maxProgressExtractor;

    public ProgressBarField(int viewResId,
                            IntegerExtractor<T> progerssExtractor,
                            IntegerExtractor<T> maxProgressExtractor) {
        super(viewResId);
        this.progressExtractor = progerssExtractor;
        this.maxProgressExtractor = maxProgressExtractor;
    }

    @Override
    public ProgressBarField<T> onClick(ItemClickListener<T> onClickListener) {

        return (ProgressBarField<T>) super.onClick(onClickListener);
    }
}
