package cn.ksmcbrigade.amh;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;

public class HideGui extends GameOptionsScreen {

    private final Screen last;

    public HideGui(Screen last) {
        super(last, MinecraftClient.getInstance().options,Text.of("All mods"));
        this.last = last;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer,Text.of("Click to hide the mod."),this.width / 2, 20+this.textRenderer.fontHeight, 16777215);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void addOptions() {
        if (this.body != null) {
            this.body.addAll(Utils.getMods(this.last));
        }
    }
}
