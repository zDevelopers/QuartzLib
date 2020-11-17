package fr.zcraft.quartzlib.components.commands.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class CommandMethod {
    private final Method method;
    private final String name;

    CommandMethod(Method method) {
        this.method = method;
        this.name = method.getName();
    }

    public String getName() {
        return name;
    }

    public void run(Object target, String[] args) {
        try {
            this.method.invoke(target, (Object[]) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // TODO
        }
    }
}
