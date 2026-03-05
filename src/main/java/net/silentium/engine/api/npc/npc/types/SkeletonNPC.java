package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface SkeletonNPC extends NPC {
    boolean isWitherSkeleton();
    void setWitherSkeleton(boolean wither);

    boolean isBurning();
    void setBurning(boolean burning);
}
