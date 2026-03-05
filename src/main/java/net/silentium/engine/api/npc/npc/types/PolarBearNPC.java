package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface PolarBearNPC extends NPC {
    boolean isBaby();
    void setBaby(boolean baby);

    boolean isStanding();
    void setStanding(boolean standing);
}
