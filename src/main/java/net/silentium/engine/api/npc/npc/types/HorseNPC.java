package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface HorseNPC extends NPC {
    boolean isTamed();
    void setTamed(boolean tamed);

    int getJumpStrength();
    void setJumpStrength(int strength);

    enum Color {
        WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN
    }

    Color getColor();
    void setColor(Color color);
}