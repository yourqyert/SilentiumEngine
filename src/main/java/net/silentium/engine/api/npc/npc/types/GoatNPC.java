package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface GoatNPC extends NPC {
    boolean isScreaming();
    void setScreaming(boolean screaming);

    boolean isJumping();
    void setJumping(boolean jumping);
}
