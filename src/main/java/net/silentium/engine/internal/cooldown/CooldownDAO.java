package net.silentium.engine.internal.cooldown;

import net.silentium.engine.api.cooldown.Cooldown;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CooldownDAO {
    CompletableFuture<Void> saveOrUpdate(Cooldown cooldown);
    CompletableFuture<Void> delete(UUID owner, String key);
    CompletableFuture<List<Cooldown>> loadAllFor(UUID owner);
    void createTable();
}