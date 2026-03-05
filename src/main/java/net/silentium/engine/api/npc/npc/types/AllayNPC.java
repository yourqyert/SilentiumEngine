package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface AllayNPC extends NPC {
    void setCanPickupItems(boolean canPickup);
    boolean canPickupItems();
}