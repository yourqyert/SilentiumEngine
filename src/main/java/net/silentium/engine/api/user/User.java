package net.silentium.engine.api.user;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    @NonNull private UUID uuid;
    @NonNull private String nickname;

    private String language;
    private int roubles;
    private long playtime;
    private Timestamp lastSeen;
    private int playerKills;
    private int mutantKills;
    private int bossKills;
    private int deaths;
    private String title;
    private String faction;

}