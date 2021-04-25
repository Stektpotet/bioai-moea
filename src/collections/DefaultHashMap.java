package collections;

import java.util.HashMap;
import java.util.function.Supplier;

public class DefaultHashMap<TKey, TValue> extends HashMap<TKey, TValue> {

    private Supplier<TValue> defaultValueSupplier;
    public DefaultHashMap(Supplier<TValue> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public DefaultHashMap(Supplier<TValue> defaultValueSupplier, int initialCapacity) {
        super(initialCapacity);
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    public TValue get(Object key) {
        TValue value = super.get(key);
        if (value == null) {
            final TValue newEntry = defaultValueSupplier.get();
            this.put((TKey) key, newEntry);
            return newEntry;
        }
        return value;
    }
}
