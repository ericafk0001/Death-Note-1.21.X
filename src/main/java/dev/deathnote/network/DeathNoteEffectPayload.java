package dev.deathnote.network;

import dev.deathnote.DeathNote;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DeathNoteEffectPayload(String victimName) implements CustomPayload {
    public static final int MAX_NAME_LENGTH = 16;

    public static final CustomPayload.Id<DeathNoteEffectPayload> ID = new CustomPayload.Id<>(
            Identifier.tryParse(DeathNote.MOD_ID + ":death_note_effect"));
    public static final PacketCodec<RegistryByteBuf, DeathNoteEffectPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.string(MAX_NAME_LENGTH),
            DeathNoteEffectPayload::victimName,
            DeathNoteEffectPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
