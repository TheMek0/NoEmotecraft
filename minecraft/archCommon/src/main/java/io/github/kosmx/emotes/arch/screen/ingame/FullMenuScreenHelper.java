package io.github.kosmx.emotes.arch.screen.ingame;

import io.github.kosmx.emotes.arch.gui.widgets.AbstractEmoteListWidget;
import io.github.kosmx.emotes.arch.screen.EmoteMenu;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.arch.screen.EmoteConfigScreen;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Stuff to override/implement
 * init
 * isPauseScreen
 * render
 */
public class FullMenuScreenHelper extends EmoteConfigScreen {

    private EditBox searchBox;
    private EmoteListFS emoteList;

    protected FullMenuScreenHelper(Screen screen) {
        super(Component.translatable("emotecraft.emotelist"), screen);
    }

    private EmoteConfigScreen newEmoteMenu() {
        return new EmoteMenu(this);
    }

    @Override
    public void init() {
        int x = (int) Math.min(getWidth() * 0.8, getHeight() - 60);
        int x1 = (getWidth() - x) / 2;
        this.searchBox = new EditBox(Minecraft.getInstance().font, x1, 12, x, 20, Component.translatable("emotecraft.search"));
        this.searchBox.setResponder((string) -> emoteList.filter(string::toLowerCase));

        this.emoteList = newEmoteList(x, getHeight(), getWidth());
        this.emoteList.emotesSetLeftPos(x1);
        emoteList.setEmotes(EmoteHolder.list, false);

        addToChildren(searchBox);
        addToChildren(emoteList);
        setInitialFocus(this.searchBox);

        int x3 = getWidth() - 120;
        int y1 = getHeight() - 30;
        Component msg1 = CommonComponents.GUI_CANCEL;
        addRenderableWidget(Button.builder(msg1, (button1 -> getMinecraft().setScreen(null))).pos(x3, y1).size(96, 20).build());

        int x2 = getWidth() - 120;
        int y = getHeight() - 60;
        Component msg = Component.translatable("emotecraft.config");
        addRenderableWidget(Button.builder(msg, (button -> getMinecraft().setScreen(newEmoteMenu()))).pos(x2, y).size(96, 20).build());
    }

    private EmoteListFS newEmoteList(int boxSize, int height, int width) {
        return new EmoteListFS(getMinecraft(), 44, boxSize, height - 44 - 10, 36, this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.emoteList.renderThis(matrices, mouseX, mouseY, delta);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
    }

    public static class EmoteListFS extends AbstractEmoteListWidget<EmoteListFS.EmoteListEntryImpl> {

        public EmoteListFS(Minecraft minecraftClient, int y, int width, int height, int itemHeight, Screen screen) {
            super(minecraftClient, y, width, height, itemHeight, screen);
        }

        @Override
        protected EmoteListEntryImpl newEmoteEntry(Minecraft client, EmoteHolder emoteHolder) {
            return new EmoteListEntryImpl(client, emoteHolder);
        }

        @Override
        public void renderThis(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
            super.render(matrices, mouseX, mouseY, delta);

            int scrollbarX = this.getX() + this.getWidth() - 6;
            int scrollbarHeight = this.getMaxScroll() > 0 ? (int) ((float) this.getHeight() * this.getHeight() / this.getMaxScroll()) : this.getHeight();
            int scrollbarY = (int) (this.getScrollAmount() * (this.getHeight() - scrollbarHeight) / this.getMaxScroll()) + this.getY();

            matrices.fill(scrollbarX, scrollbarY, scrollbarX + 6, scrollbarY + scrollbarHeight, 0xFF000000);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int scrollbarXStart = this.getX() + this.getWidth() - 6;
            int scrollbarXEnd = this.getX() + this.getWidth();

            if (mouseX >= scrollbarXStart && mouseX <= scrollbarXEnd) {
                int scrollbarHeight = this.getMaxScroll() > 0 ? (int) ((float) this.getHeight() * this.getHeight() / this.getMaxScroll()) : this.getHeight();
                double scrollbarTop = this.getScrollAmount() * (this.getHeight() - scrollbarHeight) / this.getMaxScroll() + this.getY();

                if (mouseY >= scrollbarTop && mouseY <= scrollbarTop + scrollbarHeight) {
                    return true;
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        public static class EmoteListEntryImpl extends AbstractEmoteListWidget.AbstractEmoteEntry<EmoteListEntryImpl> {

            public EmoteListEntryImpl(Minecraft client, EmoteHolder emote) {
                super(client, emote);
            }

            @Override
            protected void onPressed() {
                ClientEmotePlay.clientStartLocalEmote(this.getEmote());
                Minecraft.getInstance().setScreen(null);
            }
        }
    }
}
