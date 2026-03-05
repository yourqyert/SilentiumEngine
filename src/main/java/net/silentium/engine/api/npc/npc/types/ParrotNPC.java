package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface ParrotNPC extends NPC {
    enum Variant {
        RED, BLUE, GREEN, YELLOW, CYAN
    }

    Variant getVariant();
    void setVariant(Variant variant);

    boolean isTamed();
    void setTamed(boolean tamed);
}
