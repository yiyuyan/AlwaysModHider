package cn.ksmcbrigade.amh.mixin;

import cn.ksmcbrigade.amh.HideGui;
import cn.ksmcbrigade.amh.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;

import static cn.ksmcbrigade.amh.client.PreMod.*;

@Mixin(TitleScreen.class)
public abstract class AddButtonMixin extends Screen {

    @Unique
    private boolean hided = false;

    protected AddButtonMixin(Text title) {
        super(title);
    }

    @Inject(method = "init",at = @At("HEAD"))
    public void laterHide(CallbackInfo ci){
        //later hide

        if(hided) return;

        try {
            JsonArray array = JsonParser.parseString(Files.readString(laterHide.toPath())).getAsJsonArray();
            for(JsonElement Key:array){
                String key = Key.getAsString();
                boolean h = Utils.uninstall(key.toLowerCase());
                if(h){
                    System.out.println("Later hided a mod: "+key.toLowerCase());
                }
                else{
                    System.out.println("Can't hide the mod: "+key.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        hided = true;
    }

    @Inject(method = "init",at = @At("TAIL"))
    public void init(CallbackInfo ci){
        if(this.client==null) this.client = MinecraftClient.getInstance();
        this.addDrawableChild(ButtonWidget.builder(Text.of("ModHider"), (button) -> this.client.setScreen(new HideGui(MinecraftClient.getInstance().currentScreen))).dimensions(this.width - 80 - 2, 0, 80, 20).build());
    }
}
