package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface AxolotlNPC extends NPC {
    enum Variant {
        LUCY, WILD, GOLD, CYAN, BLUE;
    }

    Variant getVariant();
    void setVariant(Variant variant);

    boolean isPlayingDead();
    void setPlayingDead(boolean playingDead);
}