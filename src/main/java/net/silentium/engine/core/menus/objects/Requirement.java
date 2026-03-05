package net.silentium.engine.core.menus.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Requirement {

    private final RequirementType type;
    private final Object value;

    public enum RequirementType {
        PERMISSION,
        MONEY,
        RUBLES,
        ITEM,
        PLACEHOLDER
    }
}