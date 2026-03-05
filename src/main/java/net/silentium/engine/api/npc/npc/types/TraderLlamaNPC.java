package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface TraderLlamaNPC extends NPC {
    enum Color {
        CREAMY, WHITE, BROWN, GRAY
    }

    Color getColor();
    void setColor(Color color);
}
