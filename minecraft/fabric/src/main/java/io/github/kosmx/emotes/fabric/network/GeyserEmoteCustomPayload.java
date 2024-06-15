package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.executor.EmoteInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class GeyserEmoteCustomPayload implements CustomPacketPayload {
    public static final Type<GeyserEmoteCustomPayload> TYPE = new Type<>(ServerNetwork.geyserChannelID);
    public static final StreamCodec<FriendlyByteBuf, GeyserEmoteCustomPayload> CODEC = CustomPacketPayload.codec(GeyserEmoteCustomPayload::write, GeyserEmoteCustomPayload::new);
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public GeyserEmoteCustomPayload(byte[] data){
        this.data = data;
    }

    public GeyserEmoteCustomPayload(FriendlyByteBuf buf) {
        try {
            var r = buf.readableBytes();
            var d = new byte[r];
            buf.readBytes(d);
            data = d;
        } catch (Exception e) {
            EmoteInstance.instance.getLogger().log(Level.SEVERE, "Failed to read geyser emote payload", e);
        }
    }

    public void write(FriendlyByteBuf buf){
        buf.writeBytes(data);
    }

    @Override
    public @NotNull Type<? extends GeyserEmoteCustomPayload> type() {
        return TYPE;
    }
}
