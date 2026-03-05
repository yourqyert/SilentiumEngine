package net.silentium.engine.internal.cooldown;

import net.silentium.engine.api.cooldown.Cooldown;
import net.silentium.engine.api.cooldown.CooldownService;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownServiceImpl implements CooldownService {

    private final CooldownDAO cooldownDAO;
    private final Map<UUID, Map<String, Long>> cooldownCache = new ConcurrentHashMap<>();

    public CooldownServiceImpl(CooldownDAO cooldownDAO) {
        this.cooldownDAO = cooldownDAO;
    }

    @Override
    public CompletableFuture<Void> setCooldown(UUID owner, String key, long duration, TimeUnit unit) {
        long expiryTimestamp = System.currentTimeMillis() + unit.toMillis(duration);
        Cooldown cooldown = new Cooldown(owner, key, expiryTimestamp);

        cooldownCache.computeIfAbsent(owner, k -> new ConcurrentHashMap<>()).put(key, expiryTimestamp);

        return cooldownDAO.saveOrUpdate(cooldown);
    }

    @Override
    public boolean isOnCooldown(UUID owner, String key) {
        Map<String, Long> userCooldowns = cooldownCache.get(owner);
        if (userCooldowns == null) {
            return false;
        }
        Long expiryTimestamp = userCooldowns.get(key);
        if (expiryTimestamp == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiryTimestamp) {
            userCooldowns.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public Optional<Duration> getRemainingTime(UUID owner, String key) {
        if (!isOnCooldown(owner, key)) {
            return Optional.empty();
        }
        long remainingMillis = cooldownCache.get(owner).get(key) - System.currentTimeMillis();
        return Optional.of(Duration.ofMillis(remainingMillis));
    }

    @Override
    public CompletableFuture<Void> resetCooldown(UUID owner, String key) {
        cooldownCache.computeIfPresent(owner, (k, v) -> {
            v.remove(key);
            return v;
        });

        return cooldownDAO.delete(owner, key);
    }

    public void loadUserCache(UUID owner) {
        cooldownDAO.loadAllFor(owner).thenAccept(cooldowns -> {
            Map<String, Long> userCache = new ConcurrentHashMap<>();
            for (Cooldown cd : cooldowns) {
                if (System.currentTimeMillis() < cd.getExpiryTimestamp()) {
                    userCache.put(cd.getKey(), cd.getExpiryTimestamp());
                } else {
                    cooldownDAO.delete(cd.getOwner(), cd.getKey());
                }
            }
            cooldownCache.put(owner, userCache);
            System.out.println("Cooldown cache loaded for " + owner);
        });
    }

    public void clearUserCache(UUID owner) {
        cooldownCache.remove(owner);
        System.out.println("Cooldown cache cleared for " + owner);
    }
}