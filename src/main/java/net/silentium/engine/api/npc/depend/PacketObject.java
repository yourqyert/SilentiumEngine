package net.silentium.engine.api.npc.depend;

import java.util.Collection;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public interface PacketObject {
  boolean isVisibleTo(Player paramPlayer);

  Collection<String> getVisiblePlayers();

  void showTo(Player paramPlayer);

  void removeTo(Player paramPlayer);

  void hideAll();

  @Nullable
  Player getOwner();

  void setOwner(Player paramPlayer);

  boolean isPublic();

  void setPublic(boolean paramBoolean);

  void remove();
}

