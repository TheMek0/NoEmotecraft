package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.GeyserEmotePacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.fabric.FabricWrapper;
import io.github.kosmx.emotes.server.network.AbstractServerEmotePlay;
import io.github.kosmx.emotes.server.network.IServerNetworkInstance;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class ServerNetwork extends AbstractServerEmotePlay<Player> {
    public static final ResourceLocation channelID = new ResourceLocation(CommonData.MOD_ID, CommonData.playEmoteID);
    public static final ResourceLocation geyserChannelID = new ResourceLocation("geyser", "emote");

    public static ServerNetwork instance = new ServerNetwork();

    public void init(){
        PayloadTypeRegistry.playC2S().register(GeyserEmoteCustomPayload.TYPE, GeyserEmoteCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GeyserEmoteCustomPayload.TYPE, GeyserEmoteCustomPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.registerReceiver(handler, EmoteCustomPayload.TYPE, this::receivePayload);
            ServerPlayNetworking.registerReceiver(handler, GeyserEmoteCustomPayload.TYPE, this::receiveGeyserPayload);
        });
    }

    void receivePayload(EmoteCustomPayload type, ServerPlayNetworking.Context ctx){
        try {
            receiveMessage(type.getData(), ctx.player(), (INetworkInstance) ctx.player().connection);
        } catch (IOException e) {
            FabricWrapper.logger.error("Failed to receive payload", e);
        }
    }

    void receiveGeyserPayload(GeyserEmoteCustomPayload type, ServerPlayNetworking.Context ctx){
        receiveGeyserMessage(ctx.player(), type.getData());
    }

    @Override
    protected UUID getUUIDFromPlayer(Player player) {
        return player.getUUID();
    }

    @Override
    protected Player getPlayerFromUUID(UUID player) {
        return FabricWrapper.SERVER_INSTANCE.getPlayerList().getPlayer(player);
    }

    @Override
    protected long getRuntimePlayerID(Player player) {
        return player.getId();
    }

    @Override
    protected IServerNetworkInstance getPlayerNetworkInstance(Player player) {
        return (IServerNetworkInstance) ((ServerPlayer)player).connection; //If the mixin works, this should suffice//
    }

    @Override
    protected void sendForEveryoneElse(GeyserEmotePacket packet, Player player) {
        PlayerLookup.tracking(player).forEach(serverPlayer -> {
            try {
                if (serverPlayer != player && ServerPlayNetworking.canSend(serverPlayer, geyserChannelID)){
                    ServerPlayNetworking.send(serverPlayer, new GeyserEmoteCustomPayload(packet.write()));
                }
            }catch (IOException e){
                FabricWrapper.logger.error("Failed to send geyser emote packet to {}", serverPlayer.getScoreboardName(), e);
            }
        });
    }

    @Override
    protected void sendForEveryoneElse(NetData data, GeyserEmotePacket emotePacket, Player player) {
        data.player = player.getUUID();
        PlayerLookup.tracking(player).forEach(serverPlayerEntity -> {
            try {
                if (serverPlayerEntity != player) {
                    if (ServerPlayNetworking.canSend(serverPlayerEntity, channelID)) {
                        EmotePacket.Builder packetBuilder = new EmotePacket.Builder(data);
                        packetBuilder.setVersion(((IServerNetworkInstance)serverPlayerEntity.connection).getRemoteVersions());
                        ServerPlayNetworking.send(serverPlayerEntity, new EmoteCustomPayload(packetBuilder.build().write().array()));
                    } else if (ServerPlayNetworking.canSend(serverPlayerEntity, geyserChannelID) && emotePacket != null) {
                        ServerPlayNetworking.send(serverPlayerEntity, new GeyserEmoteCustomPayload(emotePacket.write()));
                    }
                }
            } catch (IOException e) {
                FabricWrapper.logger.error("Failed to send emote packet to {}", serverPlayerEntity.getScoreboardName(), e);
            }
        });
    }

    @Override
    protected void sendForPlayerInRange(NetData data, Player player, UUID target) {
        PlayerLookup.tracking(player).forEach(serverPlayerEntity -> targetFinder(serverPlayerEntity, data, target));
    }

    @Override
    protected void sendForPlayer(NetData data, Player player, UUID target) {
        PlayerLookup.all(Objects.requireNonNull(player.getServer())).forEach(serverPlayerEntity -> targetFinder(serverPlayerEntity, data, target));
    }

    private void targetFinder(ServerPlayer serverPlayerEntity, NetData data, UUID target){
        if (serverPlayerEntity.getUUID().equals(target)) {
            try {
                EmotePacket.Builder packetBuilder = new EmotePacket.Builder(data);
                packetBuilder.setVersion(((IServerNetworkInstance)serverPlayerEntity.connection).getRemoteVersions());
                ServerPlayNetworking.send(serverPlayerEntity, new EmoteCustomPayload(packetBuilder.build().write().array()));
            } catch (IOException e) {
                FabricWrapper.logger.error("Failed to send emote packet to {}", serverPlayerEntity.getScoreboardName(), e);
            }
        }
    }
}
