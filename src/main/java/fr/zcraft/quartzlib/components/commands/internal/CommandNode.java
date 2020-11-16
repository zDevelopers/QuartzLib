package fr.zcraft.quartzlib.components.commands.internal;

abstract class CommandNode {
    private final String name;

    protected CommandNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
