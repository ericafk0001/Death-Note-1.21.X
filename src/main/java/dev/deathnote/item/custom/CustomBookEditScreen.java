package dev.deathnote.item.custom;

import dev.deathnote.network.DeathNoteEffectPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

//? if >=1.21.6 {
/*import net.minecraft.client.gl.RenderPipelines;
*///?} else if >=1.21.2 {
/*import net.minecraft.client.render.RenderLayer;
*///?}

@Environment(EnvType.CLIENT)
public class CustomBookEditScreen extends BookEditScreen {
    private static final Identifier BOOK_TEXTURE = Identifier.tryParse("deathnote:textures/gui/death_note_book.png");
    private static final int BOOK_SIZE = 192;
    private static final int BOOK_TOP = 2;

    // Mirrors the vanilla writable-book text layout: text starts at 35px, wraps
    // at 151px, and each line is 9px tall.
    private static final int RULE_LEFT = 35;
    private static final int RULE_RIGHT = 151;
    private static final int RULE_FIRST_Y = 41;
    private static final int RULE_SPACING = 9;
    private static final int RULE_COUNT = 14;
    private static final int RULE_COLOR = 0x66A88B4E;

    private final ItemStack book;
    private final WritableBookContentComponent initialContent;

    public CustomBookEditScreen(PlayerEntity player, ItemStack book, Hand hand) {
        //? if >=1.21.2 {
        /*super(player, book, hand, book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT));
        *///?} else {
        super(player, book, hand);
        //?}
        this.book = book;
        this.initialContent = book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        int left = (this.width - BOOK_SIZE) / 2;

        //? if >=1.21.6 {
        /*context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, left, BOOK_TOP,
                0.0F, 0.0F, BOOK_SIZE, BOOK_SIZE, 256, 256);
        *///?} else if >=1.21.2 {
        /*context.drawTexture(RenderLayer::getGuiTextured, BOOK_TEXTURE, left, BOOK_TOP,
                0.0F, 0.0F, BOOK_SIZE, BOOK_SIZE, 256, 256);
        *///?} else {
        context.drawTexture(BOOK_TEXTURE, left, BOOK_TOP, 0, 0, BOOK_SIZE, BOOK_SIZE);
        //?}

        for (int row = 0; row < RULE_COUNT; row++) {
            int y = RULE_FIRST_Y + row * RULE_SPACING;
            context.fill(left + RULE_LEFT, y, left + RULE_RIGHT, y + 1, RULE_COLOR);
        }
    }

    @Override
    public void removed() {
        super.removed();

        updateStackFromScreen();

        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> sendEffectIfChanged(client));
    }

    private void updateStackFromScreen() {
        List<String> pageStrings = extractPagesFromScreen();
        if (pageStrings.isEmpty()) {
            return;
        }

        java.util.List<RawFilteredPair<String>> pairs = new java.util.ArrayList<>();
        for (String s : pageStrings) {
            pairs.add(RawFilteredPair.of(s));
        }
        this.book.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, new WritableBookContentComponent(pairs));
    }

    private List<String> extractPagesFromScreen() {
        Class<?> clazz = BookEditScreen.class;
        while (clazz != null && clazz != Object.class) {
            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object val = field.get(this);
                    if (val instanceof List<?> list) {
                        List<String> strings = new java.util.ArrayList<>();
                        for (Object item : list) {
                            if (item instanceof String str) {
                                strings.add(str);
                            } else if (item != null) {
                                for (java.lang.reflect.Field f : item.getClass().getDeclaredFields()) {
                                    f.setAccessible(true);
                                    Object subVal = f.get(item);
                                    if (subVal instanceof String str) {
                                        strings.add(str);
                                        break;
                                    }
                                }
                            }
                        }
                        if (!strings.isEmpty()) {
                            return strings;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }
        return java.util.Collections.emptyList();
    }

    private void sendEffectIfChanged(MinecraftClient client) {
        WritableBookContentComponent content = book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (content == null || content.equals(initialContent)) {
            return;
        }

        List<RawFilteredPair<String>> pages = content.pages();
        if (pages.isEmpty()) {
            return;
        }

        String victimName = "";
        for (int i = pages.size() - 1; i >= 0; i--) {
            String pageText = pages.get(i).get(false).trim();
            if (!pageText.isEmpty()) {
                String[] words = pageText.split("\\s+");
                if (words.length > 0) {
                    victimName = words[words.length - 1];
                    if (!victimName.isEmpty()) {
                        break;
                    }
                }
            }
        }

        if (victimName.isEmpty() || victimName.length() > DeathNoteEffectPayload.MAX_NAME_LENGTH) {
            return;
        }
        if (client.getNetworkHandler() != null) {
            ClientPlayNetworking.send(new DeathNoteEffectPayload(victimName));
        }
    }
}
