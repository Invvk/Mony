package io.github.invvk.mony.internal.config.properties.bean;

import lombok.Getter;
import lombok.Setter;

public class MonyMob {

    @Getter @Setter
    private double price;
    @Getter @Setter private double multiplier = 1.0;

    public MonyMob() {
        // For configme bean mapper
    }

    public MonyMob(MonyMobBuilder builder) {
        this.price = builder.price;
        this.multiplier = builder.multiplier;
    }

    public static MonyMobBuilder builder() {
        return new MonyMobBuilder();
    }

    public static class MonyMobBuilder {
        private double price;
        private double multiplier;

        MonyMobBuilder() {}

        public MonyMobBuilder price(double price) {
            this.price = price;
            return this;
        }

        public MonyMobBuilder multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public MonyMob build() {
            return new MonyMob(this);
        }

        public String toString() {
            return "MonyMob.MonyMobBuilder(price=" + this.price + ", multiplier=" + this.multiplier + ")";
        }
    }
}
