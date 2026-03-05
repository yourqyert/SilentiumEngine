package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface CamelNPC extends NPC {
    boolean hasChest();
    void setChest(boolean chest);

    boolean isSprinting();
    void setSprinting(boolean sprinting);
}
