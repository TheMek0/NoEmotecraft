package io.github.kosmx.emotes.fabric.mixin;


import io.github.kosmx.emotes.api.proxy.AbstractNetworkInstance;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.fabric.FabricWrapper;
import io.github.kosmx.emotes.fabric.network.EmoteCustomPayload;
import io.github.kosmx.emotes.fabric.network.ServerNetwork;
import io.github.kosmx.emotes.server.network.EmotePlayTracker;
import io.github.kosmx.emotes.server.network.IServerNetworkInstance;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkInstance implements IServerNetworkInstance {

    @Unique
    private final EmotePlayTracker emoteTracker = new EmotePlayTracker();

    @Shadow public abstract ServerPlayer getPlayer();

    @Unique
    HashMap<Byte, Byte> versions = new HashMap<>();
    @Override
    public HashMap<Byte, Byte> getRemoteVersions() {
        return versions;
    }

    @Override
    public void setVersions(HashMap<Byte, Byte> map) {
        versions = map;
    }

    @Override
    public EmotePlayTracker getEmoteTracker() {
        return emoteTracker;
    }

    @Override
    public boolean sendPlayerID() {
        return true;
    }

    @Override
    public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) throws IOException {
        sendMessage(AbstractNetworkInstance.safeGetBytesFromBuffer(builder.setVersion(getRemoteVersions()).build().write()), null);
    }

    public void sendMessage(byte[] bytes, @Nullable UUID target) {
        this.sendPacket(ServerPlayNetworking.createS2CPacket(new EmoteCustomPayload(bytes)));
    }

    @Unique
    public void sendPacket(Packet<?> packet){
        ServerGamePacketListenerImpl s = (ServerGamePacketListenerImpl) (Object) this;
        //noinspection UnreachableCode
        s.send(packet);
    }

    @Override
    public void sendConfigCallback() {
        EmotePacket.Builder builder = new EmotePacket.Builder().configureToConfigExchange(true);
        try {
            this.sendPacket(ServerPlayNetworking.createS2CPacket(new EmoteCustomPayload(builder.build().write().array())));
        } catch (IOException e){
            FabricWrapper.logger.error("Failed to send config callback", e);
        }
    }

    @Override
    public void presenceResponse() {
        IServerNetworkInstance.presenceResponse(this);
        for (ServerPlayer player : PlayerLookup.tracking(this.getPlayer())) {
                ServerNetwork.getInstance().playerStartTracking(player, this.getPlayer());
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
