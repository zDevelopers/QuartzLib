package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.Field;

public interface GroupClassInstanceSupplier {
    static GroupClassInstanceSupplier backingField(Field field) {
        return (instance) -> {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e); // TODO
            }
        };
    }

    Object supply(Object parent);
}
