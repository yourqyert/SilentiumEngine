package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface HoglinNPC extends NPC {
    boolean isBaby();
    void setBaby(boolean baby);
}
