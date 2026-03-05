package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface GlowSquidNPC extends NPC {
    boolean isGlowing();
    void setGlowing(boolean glowing);
}
