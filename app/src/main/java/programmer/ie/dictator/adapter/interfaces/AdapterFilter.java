package programmer.ie.dictator.adapter.interfaces;

import java.util.List;

public interface AdapterFilter<T> {
    public List<T> filter(String constraint, List<T> original);
}
