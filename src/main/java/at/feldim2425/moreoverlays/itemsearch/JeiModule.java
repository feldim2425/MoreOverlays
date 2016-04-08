package at.feldim2425.moreoverlays.itemsearch;

import mezz.jei.ItemFilter;
import mezz.jei.api.*;
import mezz.jei.input.IKeyable;

import javax.annotation.Nonnull;

@JEIPlugin
public class JeiModule implements IModPlugin {

    public static ItemFilter filter;
    public static IKeyable keyableOverlay;

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {

    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        filter = new ItemFilter(registry.getItemRegistry());
    }

    @Override
    public void onRecipeRegistryAvailable(@Nonnull IRecipeRegistry recipeRegistry) {

    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        if(jeiRuntime.getItemListOverlay() instanceof IKeyable){
            keyableOverlay = (IKeyable)jeiRuntime.getItemListOverlay();
        }
    }
}
