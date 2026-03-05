package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface FrogNPC extends NPC {
    enum Variant {
        TEMPERATE, WARM, COLD;
    }

    Variant getVariant();
    void setVariant(Variant variant);
}
