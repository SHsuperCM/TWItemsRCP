package shcm.shsupercm.mods.twitemsrcp;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ZipResourcePack;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class TwistedPackProvider implements net.minecraft.resource.ResourcePackProvider {
    public final String name = "\u00a7cTwisted Resources";

    private final Set<ZipResourcePack> open = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
        ResourcePackProfile container = ResourcePackProfile.of(name, false, () -> {
            ZipResourcePack zipResourcePack = new ZipResourcePack(TWItemsRCPClient.packZip) {
                @Override
                public String getName() {
                    return name;
                }
            };
            open.add(zipResourcePack);
            return zipResourcePack;
        }, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
        if(container != null)
            consumer.accept(container);
    }

    public void close() {
        open.removeIf(pack -> {
            pack.close();
            return true;
        });
    }
}
