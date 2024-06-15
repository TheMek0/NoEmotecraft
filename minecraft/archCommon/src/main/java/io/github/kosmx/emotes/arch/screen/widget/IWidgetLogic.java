package io.github.kosmx.emotes.arch.screen.widget;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.NotNull;

public interface IWidgetLogic extends GuiEventListener, NarratableEntry {
    @Override
    default NarratableEntry.@NotNull NarrationPriority narrationPriority(){
        return NarratableEntry.NarrationPriority.NONE; //TODO narration
    }

    @Override
    default void updateNarration(@NotNull NarrationElementOutput narrationElementOutput){
        //TODO this too
    }
}
