package shcm.shsupercm.mods.twitemsrcp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TWItemsRCPClient implements ClientModInitializer {
    public static final File dir = new File("twitemsrcp"), cache = new File(dir, "cache"), packZip = new File(TWItemsRCPClient.dir, "pack.zip");
    public static TwistedPackProvider provider = new TwistedPackProvider();

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(new Identifier("twitems", "rcp"), (packetContext, packetByteBuf) -> {
            synchronized (TWItemsRCPClient.this) {
                try {
                    ByteBuffer byteBuffer = packetByteBuf.nioBuffer();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String[] received = new String(bytes).split(";");
                    try {
                        List<String> cacheLines = Files.readAllLines(cache.toPath());
                        if (!received[0].equals(cacheLines.get(0))) { // version mismatch
                            download(received[0], received[1]);
                            cache.delete();
                            Files.write(cache.toPath(), Collections.singletonList(received[0]));
                        }
                    } catch (Exception e) { // no cached version
                        dir.mkdirs();
                        download(received[0], received[1]);
                        cache.delete();
                        Files.write(cache.toPath(), Collections.singletonList(received[0]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void download(String version, String url) {
        provider.close();
        packZip.delete();

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("Downloading Twisted Resourcepack V" + version + "..."));
        try {
            FileUtils.copyURLToFile(new URL(url), packZip);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("Downloaded successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean reloadAssets = false;
        for (ResourcePackProfile enabledProfile : MinecraftClient.getInstance().getResourcePackManager().getEnabledProfiles()) {
            if (enabledProfile.getName().equals(provider.name)) {
                reloadAssets = true;
                break;
            }
        }
        if (reloadAssets) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("Reloading assets.."));
            MinecraftClient.getInstance().reloadResourcesConcurrently();
        }
    }
}
