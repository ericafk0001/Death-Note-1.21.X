package dev.deathnote.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
//? if >=1.21.2 {
/*import net.minecraft.util.ActionResult;
*///?} else {
import net.minecraft.util.TypedActionResult;
//?}
import net.minecraft.world.World;

public class DeathNoteItem extends Item {
    public DeathNoteItem(Settings settings) {
        super(settings);
    }

    //? if >=1.21.2 {
    /*@Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            ClientDeathNoteItem.useClient(user, hand);
        }
        return ActionResult.SUCCESS;
    }
    *///?} else {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            ClientDeathNoteItem.useClient(user, hand);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    //?}
}
