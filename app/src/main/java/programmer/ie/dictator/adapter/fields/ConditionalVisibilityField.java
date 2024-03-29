package programmer.ie.dictator.adapter.fields;

import programmer.ie.dictator.adapter.interfaces.BooleanExtractor;
import programmer.ie.dictator.adapter.interfaces.ItemClickListener;

/**
 * A field to conditionally show a view based on some boolean value. Good for
 * showing list item decorations for certain conditions.
 *
 * @param <T>
 * @author Ami
 */
public class ConditionalVisibilityField<T> extends BaseField<T> {

    public BooleanExtractor<T> extractor;
    public int visibilityIfFalse;

    public ConditionalVisibilityField(int viewResId,
                                      BooleanExtractor<T> extractor, int visibilityIfFalse) {
        super(viewResId);
        this.extractor = extractor;
        this.visibilityIfFalse = visibilityIfFalse;
    }

    @Override
    public ConditionalVisibilityField<T> onClick(ItemClickListener<T> onClickListener) {

        return (ConditionalVisibilityField<T>) super.onClick(onClickListener);
    }

}
