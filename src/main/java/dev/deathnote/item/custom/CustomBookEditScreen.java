// CustomBookEditScreen.java
package dev.deathnote.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

//? if >=1.21.2 {
/*import net.minecraft.client.gl.RenderPipelines;
*///?}

@Environment(EnvType.CLIENT)
public class CustomBookEditScreen extends BookEditScreen {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomBookEditScreen.class);
    private static final Field pagesField;

    private static final Identifier BOOK_TEXTURE = Identifier.tryParse("deathnote:textures/gui/death_note_book.png");
    private static final int BOOK_SIZE = 192;
    private static final int BOOK_TOP = 2;

    // These match the vanilla writable-book text layout on every supported version:
    // text starts at x = left + 35/36 and y = 32, wraps at 114px, with 9px line spacing.
    private static final int RULE_LEFT = 35;
    private static final int RULE_RIGHT = 151;
    private static final int RULE_FIRST_Y = 41;
    private static final int RULE_SPACING = 9;
    private static final int RULE_COUNT = 14;
    private static final int RULE_COLOR = 0x66A88B4E;

    static {
        Field tempField = null;
        try {
            for (Field field : BookEditScreen.class.getDeclaredFields()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    tempField = field;
                    tempField.setAccessible(true);
                    break;
                }
            }
            if (tempField == null) {
                throw new NoSuchFieldException("No field of type List found in BookEditScreen");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pagesField = tempField;
    }

    public CustomBookEditScreen(PlayerEntity player, ItemStack book, Hand hand) {
        //? if >=1.21.2 {
        /*super(player, book, hand, book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT));
        *///?} else {
        super(player, book, hand);
        //?}
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        int left = (this.width - BOOK_SIZE) / 2;

        //? if >=1.21.2 {
        /*context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, left, BOOK_TOP,
                0.0F, 0.0F, BOOK_SIZE, BOOK_SIZE, 256, 256);
        *///?} else {
        context.drawTexture(BOOK_TEXTURE, left, BOOK_TOP, 0, 0, BOOK_SIZE, BOOK_SIZE);
        //?}

        for (int row = 0; row < RULE_COUNT; row++) {
            int y = RULE_FIRST_Y + row * RULE_SPACING;
            context.fill(left + RULE_LEFT, y, left + RULE_RIGHT, y + 1, RULE_COLOR);
        }
    }

    public record DeathNoteEffectPayload(String victimName) implements CustomPayload {
        public static final CustomPayload.Id<DeathNoteEffectPayload> ID = new CustomPayload.Id<>(
                Identifier.tryParse("deathnote:death_note_item"));
        public static final PacketCodec<RegistryByteBuf, DeathNoteEffectPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, DeathNoteEffectPayload::victimName, DeathNoteEffectPayload::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }

        public static void send(String victimName) {
            ClientPlayNetworking.send(new DeathNoteEffectPayload(victimName));
        }
    }

    @Override
    public void removed() {
        super.removed();

        try {
            @SuppressWarnings("unchecked")
            List<String> bookText = (List<String>) pagesField.get(this);
            if (!bookText.isEmpty()) {
                String lastLine = bookText.get(bookText.size() - 1).trim();
                String[] names = lastLine.split("\\s+");
                String victimName = names[names.length - 1];
                if (!victimName.isEmpty()) {
                    DeathNoteEffectPayload.send(victimName);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process Death Note effect", e);
        }
    }
}