package collections;

import java.util.HashMap;
import java.util.function.Supplier;

public class DefaultHashMap<TKey, TValue> extends HashMap<TKey, TValue> {

    private Supplier<TValue> defaultValueSupplier;
    public DefaultHashMap(Supplier<TValue> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    public TValue get(Object key) {
        TValue value = super.get(key);
        return (value != null) ? value : put((TKey) key, defaultValueSupplier.get());
    }
}
