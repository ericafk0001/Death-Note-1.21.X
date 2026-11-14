package dev.deathnote.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class ClientDeathNoteItem {
    public static ItemStack useClient(PlayerEntity user, Hand hand) {
        ItemStack writableBook = user.getStackInHand(Hand.MAIN_HAND);
        MinecraftClient.getInstance().setScreen(new CustomBookEditScreen(user, writableBook, hand));
        return user.getStackInHand(hand);
    }
}