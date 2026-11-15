package dev.deathnote.network;

import dev.deathnote.DeathNote;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record DeathNoteBookPayload(List<String> pages, int hand) implements CustomPayload {
    private static final PacketCodec<ByteBuf, List<String>> PAGES = PacketCodecs.collection(
            ArrayList::new,
            PacketCodecs.string(WritableBookContentComponent.MAX_PAGE_LENGTH),
            WritableBookContentComponent.MAX_PAGE_COUNT);

    public static final CustomPayload.Id<DeathNoteBookPayload> ID = new CustomPayload.Id<>(
            Identifier.tryParse(DeathNote.MOD_ID + ":death_note_book"));
    public static final PacketCodec<RegistryByteBuf, DeathNoteBookPayload> CODEC = PacketCodec.tuple(
            PAGES,
            DeathNoteBookPayload::pages,
            PacketCodecs.VAR_INT,
            DeathNoteBookPayload::hand,
            DeathNoteBookPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
