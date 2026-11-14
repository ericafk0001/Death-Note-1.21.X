package dev.deathnote.network;

import dev.deathnote.DeathNote;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DeathNoteVictimPayload() implements CustomPayload {
    public static final CustomPayload.Id<DeathNoteVictimPayload> ID = new CustomPayload.Id<>(
            Identifier.tryParse(DeathNote.MOD_ID + ":death_note_victim"));
    public static final PacketCodec<RegistryByteBuf, DeathNoteVictimPayload> CODEC =
            PacketCodec.unit(new DeathNoteVictimPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
