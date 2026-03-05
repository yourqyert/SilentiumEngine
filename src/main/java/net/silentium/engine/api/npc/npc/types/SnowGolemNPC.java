package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface SnowGolemNPC extends NPC {
    boolean isPumpkinVisible();
    void setPumpkinVisible(boolean visible);
}