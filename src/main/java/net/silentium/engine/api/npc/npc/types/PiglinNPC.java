package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface PiglinNPC extends NPC {
    boolean isImmuneToZombification();
    void setImmuneToZombification(boolean immune);

    boolean isBaby();
    void setBaby(boolean baby);
}
