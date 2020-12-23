package shcm.shsupercm.mods.twitemsrcp.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo info) {
        if (message instanceof TranslatableText && ((TranslatableText) message).getKey().contains("Twisted Vanilla V"))
            info.cancel();
    }
}