package fr.zcraft.quartzlib.components.commands.internal;

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
}
