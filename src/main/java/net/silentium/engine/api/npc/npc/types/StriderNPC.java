package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface StriderNPC extends NPC {
    boolean isSaddled();
    void setSaddled(boolean saddled);

    boolean isBaby();
    void setBaby(boolean baby);
}
