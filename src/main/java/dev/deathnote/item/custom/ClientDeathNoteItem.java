package dev.deathnote.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class ClientDeathNoteItem {
    public static void useClient(PlayerEntity user, Hand hand) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new CustomBookEditScreen(user, user.getStackInHand(hand), hand));
    }
}
