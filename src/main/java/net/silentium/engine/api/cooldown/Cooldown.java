package net.silentium.engine.api.cooldown;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Cooldown {
    private final UUID owner;
    private final String key;
    private long expiryTimestamp;
}