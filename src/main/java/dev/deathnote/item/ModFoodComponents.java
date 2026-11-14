package dev.deathnote.item;

import net.minecraft.component.type.FoodComponent;
//? if >=1.21.2 {
/*import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
*///?}
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    //? if >=1.21.2 {
    /*public static final FoodComponent SHINIGAMI_APPLE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.2f).alwaysEdible()
            .build();

    public static final ConsumableComponent SHINIGAMI_APPLE_CONSUMABLE = ConsumableComponent.builder()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 * 20), 0.3f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 10 * 20), 0.6f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.HUNGER, 15 * 20, 5), 0.9f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 15 * 20), 0.9f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20), 1f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120 * 20, 5), 0.7f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20, 2), 0.6f))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 5 * 20, 1), 0.6f))
            .build();

    public static final FoodComponent POTATO_CHIP = new FoodComponent.Builder().nutrition(4).saturationModifier(0.1f)
            .build();

    public static final ConsumableComponent POTATO_CHIP_CONSUMABLE = ConsumableComponent.builder()
            .consumeSeconds(0.8f)
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 5 * 20), 1f))
            .build();
    *///?} else {
    public static final FoodComponent SHINIGAMI_APPLE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.2f).alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 * 20), 0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 10 * 20), 0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 15 * 20, 5), 0.9f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 15 * 20), 0.9f)
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20), 1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120 * 20, 5), 0.7f)
            .statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20, 2), 0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 5 * 20, 1), 0.6f)
            .build();

    public static final FoodComponent POTATO_CHIP = new FoodComponent.Builder().nutrition(4).saturationModifier(0.1f)
            .snack()
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 5 * 20), 1f)
            .build();
    //?}
}