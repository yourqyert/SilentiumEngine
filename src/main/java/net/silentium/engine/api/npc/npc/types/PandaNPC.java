package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;

public interface PandaNPC extends NPC {
    enum MainGene { NORMAL, LAZY, WORRIED, PLAYFUL, BROWN, WEAK, AGGRESSIVE }

    MainGene getMainGene();
    void setMainGene(MainGene gene);

    boolean isSitting();
    void setSitting(boolean sitting);
}
