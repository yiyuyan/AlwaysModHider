package cn.ksmcbrigade.amh.mixin;

import cn.ksmcbrigade.amh.client.PreMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class InvokeEntrypointMixin {

    @Inject(method = "<init>",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/storage/LevelStorage;createSymlinkFinder(Ljava/nio/file/Path;)Lnet/minecraft/util/path/SymlinkFinder;"))
    public void init(RunArgs args, CallbackInfo ci){
        for(EntrypointContainer<ClientModInitializer> client: PreMod.hiddenClient){
            client.getEntrypoint().onInitializeClient();
        }
        for(EntrypointContainer<ModInitializer> main: PreMod.hiddenMain){
            main.getEntrypoint().onInitialize();
        }
    }
}
