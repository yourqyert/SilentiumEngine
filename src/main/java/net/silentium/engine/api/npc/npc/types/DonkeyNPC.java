package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface DonkeyNPC extends NPC {
    boolean isTamed();
    void setTamed(boolean tamed);
}
