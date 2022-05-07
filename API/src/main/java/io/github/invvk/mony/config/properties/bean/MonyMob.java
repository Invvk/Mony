package io.github.invvk.mony.config.properties.bean;

import lombok.Getter;
import lombok.Setter;

public class MonyMob {

    @Getter @Setter
    private double price;
    @Getter @Setter private double multiplier = 1.0;

    @Setter private boolean disableCombo = false;

    public MonyMob() {
        // For configme bean mapper
    }

    public MonyMob(MonyMobBuilder builder) {
        this.price = builder.price;
        this.disableCombo = builder.disableCombo;
        this.multiplier = builder.multiplier;
    }

    public static MonyMobBuilder builder() {
        return new MonyMobBuilder();
    }

    public boolean isComboDisabled() {
        return disableCombo;
    }

    public static class MonyMobBuilder {
        private double price;
        private double multiplier;
        private boolean disableCombo;

        MonyMobBuilder() {
        }

        public MonyMobBuilder price(double price) {
            this.price = price;
            return this;
        }

        public MonyMobBuilder multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public MonyMobBuilder disableCombo(boolean disableCombo) {
            this.disableCombo = disableCombo;
            return this;
        }

        public MonyMob build() {
            return new MonyMob(this);
        }

        public String toString() {
            return "MonyMob.MonyMobBuilder(price=" + this.price + ", multiplier=" + this.multiplier + ", disableCombo=" + this.disableCombo + ")";
        }
    }
}
