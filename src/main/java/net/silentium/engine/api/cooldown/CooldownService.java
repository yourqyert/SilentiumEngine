package net.silentium.engine.api.cooldown;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface CooldownService {

    CompletableFuture<Void> setCooldown(UUID owner, String key, long duration, TimeUnit unit);

    boolean isOnCooldown(UUID owner, String key);

    Optional<Duration> getRemainingTime(UUID owner, String key);

    CompletableFuture<Void> resetCooldown(UUID owner, String key);
}