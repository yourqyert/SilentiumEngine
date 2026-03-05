package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;
import org.bukkit.inventory.ItemStack;

public interface WitchNPC extends NPC {
    boolean isDrinkingPotion();
    void setDrinkingPotion(boolean drinking);

    ItemStack potion();
    void setPotion(ItemStack potion);
}
