package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface ShulkerNPC extends NPC {
    enum Color {
        WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK,
        GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN,
        RED, BLACK, DEFAULT;
    }

    Color getColor();
    void setColor(Color color);

    boolean isClosed();
    void setClosed(boolean closed);
}
