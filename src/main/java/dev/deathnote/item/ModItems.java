//ModItems.java
package dev.deathnote.item;

import dev.deathnote.DeathNote;
import dev.deathnote.item.custom.DeathNoteItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
//? if >=1.21.2 {
/*import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
*///?}
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {

    // Food items
    //? if >=1.21.2 {
    /*public static final Item SHINIGAMI_APPLE = registerItem("shinigami_apple",
            settings -> new Item(settings.food(ModFoodComponents.SHINIGAMI_APPLE, ModFoodComponents.SHINIGAMI_APPLE_CONSUMABLE)));
    public static final Item POTATO_CHIP = registerItem("potato_chip",
            settings -> new Item(settings.food(ModFoodComponents.POTATO_CHIP, ModFoodComponents.POTATO_CHIP_CONSUMABLE)));
    *///?} else {
    public static final Item SHINIGAMI_APPLE = registerItem("shinigami_apple",
            settings -> new Item(settings.food(ModFoodComponents.SHINIGAMI_APPLE)));
    public static final Item POTATO_CHIP = registerItem("potato_chip",
            settings -> new Item(settings.food(ModFoodComponents.POTATO_CHIP)));
    //?}

    // Custom items
    public static final Item DEATH_NOTE_ITEM = registerItem("death_note_item",
            settings -> new DeathNoteItem(settings.maxCount(1).rarity(Rarity.EPIC)
                    .component(DataComponentTypes.WRITABLE_BOOK_CONTENT, WritableBookContentComponent.DEFAULT)));

    private static Item registerItem(String name, Function<Item.Settings, Item> factory) {
        Identifier id = Identifier.tryParse(DeathNote.MOD_ID + ":" + name);
        Item.Settings settings = new Item.Settings();
        //? if >=1.21.2 {
        /*settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, id));
        *///?}
        return Registry.register(Registries.ITEM, id, factory.apply(settings));
    }

    public static void registerModItems() {
        DeathNote.LOGGER.info("Registering Mod Items for " + DeathNote.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(SHINIGAMI_APPLE);
            entries.add(POTATO_CHIP);
            entries.add(DEATH_NOTE_ITEM);
        });
    }
}