package net.silentium.engine.core.menus.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MenuItem {

    private String key;
    private ItemStack itemStack;
    private List<String> actions;
    private String mask;
    private Map<String, Requirement> requirements;

    private transient String material;
    private transient int amount;
    private transient String name;
    private transient List<String> lore;
    private transient int customModelData;

}