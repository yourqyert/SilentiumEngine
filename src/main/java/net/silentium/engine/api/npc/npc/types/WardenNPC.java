package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface WardenNPC extends NPC {
    int getAngerLevel();
    void setAngerLevel(int level);

    boolean isListening();
    void setListening(boolean listening);
}
