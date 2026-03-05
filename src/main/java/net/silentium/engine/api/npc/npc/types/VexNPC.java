package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface VexNPC extends NPC {
    boolean isCharging();
    void setCharging(boolean charging);
}