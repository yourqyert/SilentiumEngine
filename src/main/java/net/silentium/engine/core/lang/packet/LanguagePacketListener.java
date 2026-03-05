package net.silentium.engine.core.lang.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import net.kyori.adventure.text.Component;
import net.silentium.engine.SilentiumEngine;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class LanguagePacketListener extends PacketAdapter {

    private final SilentiumEngine plugin;

    public LanguagePacketListener(SilentiumEngine plugin) {
        super(plugin,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SET_ACTION_BAR_TEXT,
                PacketType.Play.Server.SET_TITLE_TEXT,
                PacketType.Play.Server.SET_SUBTITLE_TEXT,
                PacketType.Play.Server.OPEN_WINDOW,
                PacketType.Play.Server.SCOREBOARD_TEAM,
                PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
                PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.KICK_DISCONNECT
        );
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        PacketType packetType = event.getPacketType();

        try {
            if (isSimpleTextPacket(packetType)) {
                if (event.getPacket().getChatComponents().size() > 0) {
                    translateChatComponent(event, 0);
                }
            } else if (packetType == PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {
                if (event.getPacket().getChatComponents().size() > 0) translateChatComponent(event, 0);
                if (event.getPacket().getChatComponents().size() > 1) translateChatComponent(event, 1);
            } else if (packetType == PacketType.Play.Server.ENTITY_METADATA) {
                translateEntityMetadata(event);
            } else if (packetType == PacketType.Play.Server.SCOREBOARD_TEAM) {
                translateTeamPacket(event);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to process packet " + packetType.name());
            e.printStackTrace();
        }
    }

    private void translateTeamPacket(PacketEvent event) {
        if (event.getPacket().getChatComponents().size() > 1) translateChatComponent(event, 1);
        if (event.getPacket().getChatComponents().size() > 2) translateChatComponent(event, 2);
        if (event.getPacket().getChatComponents().size() > 3) translateChatComponent(event, 3);
    }

    private void translateChatComponent(PacketEvent event, int index) {
        WrappedChatComponent chatComponent = event.getPacket().getChatComponents().read(index);
        if (chatComponent != null && chatComponent.getHandle() instanceof Component) {
            Component translated = plugin.getLanguageManager().translateComponent((Component) chatComponent.getHandle(), event.getPlayer());
            event.getPacket().getChatComponents().write(index, WrappedChatComponent.fromHandle(translated));
        }
    }

    private void translateEntityMetadata(PacketEvent event) {
        List<WrappedDataValue> dataValues = event.getPacket().getDataValueCollectionModifier().read(0);
        for (WrappedDataValue dataValue : dataValues) {
            if (dataValue.getIndex() == 2 && dataValue.getValue() instanceof Optional) {
                Optional<?> optionalValue = (Optional<?>) dataValue.getValue();
                if (optionalValue.isPresent() && optionalValue.get() instanceof Component) {
                    Component originalName = (Component) optionalValue.get();
                    Component translatedName = plugin.getLanguageManager().translateComponent(originalName, event.getPlayer());
                    dataValue.setValue(Optional.of(translatedName));
                }
            }
        }
    }

    private boolean isSimpleTextPacket(PacketType type) {
        return type == PacketType.Play.Server.SYSTEM_CHAT ||
                type == PacketType.Play.Server.CHAT ||
                type == PacketType.Play.Server.SET_ACTION_BAR_TEXT ||
                type == PacketType.Play.Server.SET_TITLE_TEXT ||
                type == PacketType.Play.Server.SET_SUBTITLE_TEXT ||
                type == PacketType.Play.Server.OPEN_WINDOW ||
                type == PacketType.Play.Server.KICK_DISCONNECT;
    }
}