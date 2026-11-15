package dev.deathnote.item.custom;

import dev.deathnote.network.DeathNoteBookPayload;
import dev.deathnote.network.DeathNoteEffectPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
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

    // 1.21.6 signs on its own screen, so the name waits until that screen closes.
    private static String pendingVictim;

    private final Hand hand;
    private boolean cancelled;

    public CustomBookEditScreen(PlayerEntity player, ItemStack book, Hand hand) {
        //? if >=1.21.2 {
        /*super(player, book, hand, book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT));
        *///?} else {
        super(player, book, hand);
        //?}
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        // Signing would turn the book into a vanilla written book, so the button
        // keeps its label but closes the screen the same way Done does.
        for (Element child : List.copyOf(this.children())) {
            if (child instanceof ButtonWidget button && isSignButton(button)) {
                this.remove(child);
                this.addDrawableChild(ButtonWidget.builder(button.getMessage(),
                                pressed -> MinecraftClient.getInstance().setScreen(null))
                        .dimensions(button.getX(), button.getY(), button.getWidth(), button.getHeight())
                        .build());
                return;
            }
        }
    }

    private static boolean isSignButton(ButtonWidget button) {
        return button.getMessage().getString().equals(I18n.translate("book.signButton"));
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
    public void close() {
        this.cancelled = true;
        super.close();
    }

    @Override
    public void removed() {
        super.removed();
        if (this.cancelled) {
            pendingVictim = null;
            return;
        }

        // The screen keeps the live page list; the item only catches up on Done
        // or Sign. The next tick can see which screen replaced this one.
        pendingVictim = lastWord(this.pages);

        // Vanilla only writes the pages when it finalizes, and its book update is
        // ignored for custom items before 1.21.2, so the mod saves both copies.
        List<String> pages = savedPages(this.pages);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.getStackInHand(this.hand).set(DataComponentTypes.WRITABLE_BOOK_CONTENT,
                    new WritableBookContentComponent(pages.stream().map(RawFilteredPair::of).toList()));
        }
        if (client.getNetworkHandler() != null) {
            ClientPlayNetworking.send(new DeathNoteBookPayload(pages, this.hand.ordinal()));
        }
    }

    public static void tickPendingSign(MinecraftClient client) {
        if (pendingVictim == null) {
            return;
        }

        String victimName = pendingVictim;
        pendingVictim = null;
        if (client.currentScreen == null) {
            sendEffect(client, victimName);
        }
    }

    private static String lastWord(List<String> pages) {
        for (int i = pages.size() - 1; i >= 0; i--) {
            String text = pages.get(i).trim();
            if (text.isEmpty()) {
                continue;
            }
            String[] words = text.split("\\s+");
            String name = words[words.length - 1];
            return name.length() > DeathNoteEffectPayload.MAX_NAME_LENGTH ? "" : name;
        }
        return "";
    }

    private static List<String> savedPages(List<String> pages) {
        int end = pages.size();
        while (end > 0 && pages.get(end - 1).isBlank()) {
            end--;
        }
        return List.copyOf(pages.subList(0, end));
    }

    private static void sendEffect(MinecraftClient client, String victimName) {
        if (victimName.isEmpty() || client.getNetworkHandler() == null) {
            return;
        }
        ClientPlayNetworking.send(new DeathNoteEffectPayload(victimName));
    }
}