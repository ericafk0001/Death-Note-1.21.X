package dev.deathnote.client;

import dev.deathnote.item.custom.CustomBookEditScreen;
import dev.deathnote.network.DeathNoteVictimPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DeathNoteClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DeathNoteVictimPayload.ID,
                (payload, context) -> DeathNoteHud.show());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            DeathNoteHud.tick();
            CustomBookEditScreen.tickPendingSign(client);
        });
        HudRenderCallback.EVENT.register((context, tickCounter) -> DeathNoteHud.render(context));
    }
}
