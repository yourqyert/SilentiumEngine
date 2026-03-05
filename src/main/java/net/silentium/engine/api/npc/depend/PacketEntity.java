package net.silentium.engine.api.npc.depend;

import org.bukkit.Location;

public interface PacketEntity extends PacketObject {

  int getEntityID();

  Location getLocation();

  void onTeleport(Location paramLocation);

}

