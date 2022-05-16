package io.github.invvk.mony.internal.config.properties.bean;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MobBean {

    @Getter
    @Setter
    private double defaultPrice = 100;

    @Getter
    @Setter
    private double comboMultiplier = 1.2;

    private final Map<String, MonyMob> custom_price = new HashMap<>();

    public MobBean() {
        // Default values for the bean mapper
        this.custom_price.put("ZOMBIE", MonyMob.builder().price(120).multiplier(1.1).build());
        this.custom_price.put("SKELETON", MonyMob.builder().price(130).multiplier(0).build());
    }

    public MonyMob getMob(final EntityType type) {
        final MonyMob bean = custom_price.get(type.name());
        if (bean == null)
            return MonyMob.builder()
                    .multiplier(this.comboMultiplier)
                    .price(this.defaultPrice).build();
        return bean;
    }

    public Map<String, MonyMob> getMobMap() {
        return custom_price;
    }
}
