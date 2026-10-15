package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.util.StringRepresentable;

public enum VerticalConnection implements StringRepresentable {
    SINGLE("single"),
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;

    VerticalConnection(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
