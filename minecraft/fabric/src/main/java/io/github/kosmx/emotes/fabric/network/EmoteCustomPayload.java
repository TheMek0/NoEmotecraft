package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.executor.EmoteInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class EmoteCustomPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EmoteCustomPayload> TYPE = new Type<>(ServerNetwork.channelID);
    public static final StreamCodec<FriendlyByteBuf, EmoteCustomPayload> CODEC = CustomPacketPayload.codec(EmoteCustomPayload::write, EmoteCustomPayload::new);
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public EmoteCustomPayload(byte[] data){
        this.data = data;
    }

    public EmoteCustomPayload(FriendlyByteBuf buf) {
        try {
            var r = buf.readableBytes();
            var d = new byte[r];
            buf.readBytes(d);
            data = d;
        } catch (Exception e) {
            EmoteInstance.instance.getLogger().log(Level.SEVERE, "Failed to read emote payload", e);
        }
    }

    public void write(FriendlyByteBuf buf){
        buf.writeBytes(data);
    }

    @Override
    public @NotNull Type<? extends EmoteCustomPayload> type() {
        return TYPE;
    }
}
