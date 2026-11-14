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

        // The Done button closes the screen before vanilla writes the edited pages
        // back to the stack, so read them once the current client task has finished.
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> sendEffectIfChanged(client));
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

        String lastLine = pages.get(pages.size() - 1).get(false).trim();
        String[] words = lastLine.split("\\s+");
        String victimName = words[words.length - 1];
        if (victimName.isEmpty() || victimName.length() > DeathNoteEffectPayload.MAX_NAME_LENGTH) {
            return;
        }
        if (client.getNetworkHandler() != null) {
            ClientPlayNetworking.send(new DeathNoteEffectPayload(victimName));
        }
    }
}
