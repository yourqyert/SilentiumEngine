package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface DrownedNPC extends NPC {
    boolean isBaby();
    void setBaby(boolean baby);

    boolean isConverted();
    void setConverted(boolean converted);
}
