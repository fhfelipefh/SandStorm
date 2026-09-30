package com.fhfelipefh.sandstorm.content.technical;

import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record TechnicalSpecEntry(List<Component> details) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Component> list = new ArrayList<>();

        public Builder add(Component component) {
            this.list.add(component);
            return this;
        }

        public TechnicalSpecEntry build() {
            return new TechnicalSpecEntry(Collections.unmodifiableList(new ArrayList<>(list)));
        }
    }
}
