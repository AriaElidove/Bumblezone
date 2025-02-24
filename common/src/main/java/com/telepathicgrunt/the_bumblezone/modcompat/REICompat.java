package com.telepathicgrunt.the_bumblezone.modcompat;

import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.client.screens.CrystallineFlowerScreen;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.entities.datamanagers.queentrades.QueensTradeManager;
import com.telepathicgrunt.the_bumblezone.entities.datamanagers.queentrades.WeightedTradeResult;
import com.telepathicgrunt.the_bumblezone.items.recipes.PotionCandleRecipe;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.MainTradeRowInput;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.RandomizeTradeRowInput;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.rei.QueenRandomizerTradesREICategory;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.rei.QueenTradesREICategory;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.rei.REIQueenRandomizerTradesInfo;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.rei.REIQueenTradesInfo;
import com.telepathicgrunt.the_bumblezone.modinit.BzCreativeTabs;
import com.telepathicgrunt.the_bumblezone.modinit.BzFluids;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class REICompat implements REIClientPlugin {

    public static final CategoryIdentifier<REIQueenTradesInfo> QUEEN_TRADES = CategoryIdentifier.of(Bumblezone.MODID, "queen_trades");
    public static final CategoryIdentifier<REIQueenRandomizerTradesInfo> QUEEN_RANDOMIZE_TRADES = CategoryIdentifier.of(Bumblezone.MODID, "queen_color_randomizer_trades");

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        BzCreativeTabs.CUSTOM_CREATIVE_TAB_ITEMS.forEach(item -> addInfo(item.get()));
        addInfo(BzFluids.SUGAR_WATER_FLUID.get());
        addInfo(BzFluids.ROYAL_JELLY_FLUID.get());
        if (BzModCompatibilityConfigs.alternativeFluidToReplaceHoneyFluid.isEmpty()) {
            addInfo(BzFluids.HONEY_FLUID.get());
        }

        registry.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "potion_candle/from_super_candles"))
                .ifPresent(recipe -> registerExtraRecipes(recipe.value(), registry, true));

        registry.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "potion_candle/from_string_and_carvable_wax"))
                .ifPresent(recipe -> registerExtraRecipes(recipe.value(), registry, false));

        if (!QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerMainTrades.isEmpty()) {
            for (Pair<MainTradeRowInput, WeightedRandomList<WeightedTradeResult>> trade : QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerMainTrades) {
                for (WeightedTradeResult weightedTradeResult : trade.getSecond().unwrap()) {
                    List<ItemStack> rewardCollection = weightedTradeResult.getItems();
                    registry.add(new REIQueenTradesInfo(
                            trade.getFirst().tagKey().isPresent() ? EntryIngredients.ofItemTag(trade.getFirst().tagKey().get()) : EntryIngredients.of(trade.getFirst().item()),
                            trade.getFirst().tagKey().orElse(null),
                            EntryIngredients.ofItemStacks(rewardCollection),
                            weightedTradeResult.tagKey.orElse(null),
                            weightedTradeResult.xpReward,
                            weightedTradeResult.weight,
                            weightedTradeResult.getTotalWeight()
                    ), QUEEN_TRADES);
                }
            }
        }

        if (!QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerRandomizerTrades.isEmpty()) {
            for (RandomizeTradeRowInput tradeEntry : QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerRandomizerTrades) {
                List<ItemStack> randomizeStack = tradeEntry.getWantItems().stream().map(e -> e.value().getDefaultInstance()).toList();
                for (ItemStack input : randomizeStack) {
                    registry.add(new REIQueenRandomizerTradesInfo(
                            EntryIngredients.of(input),
                            EntryIngredients.ofItemStacks(randomizeStack),
                            tradeEntry.tagKey().orElse(null),
                            1,
                            randomizeStack.size()
                    ), QUEEN_RANDOMIZE_TRADES);
                }
            }
        }
    }

    private static void addInfo(Item item) {
        BuiltinClientPlugin.getInstance().registerInformation(
                EntryStacks.of(item),
                Component.translatable(BuiltInRegistries.ITEM.getKey(item).toString()),
                (text) -> {
                    text.add(Component.translatable(Bumblezone.MODID + "." + BuiltInRegistries.ITEM.getKey(item).getPath() + ".description"));
                    return text;
                });
    }

    private static void addInfo(Fluid fluid) {
        BuiltinClientPlugin.getInstance().registerInformation(
                EntryStacks.of(fluid, 1000),
                Component.translatable(BuiltInRegistries.FLUID.getKey(fluid).toString()),
                (text) -> {
                    text.add(Component.translatable(Bumblezone.MODID + "." + BuiltInRegistries.FLUID.getKey(fluid).getPath() + ".description"));
                    return text;
                });
    }

    private static void registerExtraRecipes(Recipe<?> baseRecipe, DisplayRegistry registry, boolean oneRecipeOnly) {
        if (baseRecipe instanceof PotionCandleRecipe potionCandleRecipe) {
            List<CraftingRecipe> extraRecipes = FakePotionCandleRecipeCreator.constructFakeRecipes(potionCandleRecipe, oneRecipeOnly);
            extraRecipes.forEach(registry::add);
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new QueenTradesREICategory());
        registry.add(new QueenRandomizerTradesREICategory());

        registry.addWorkstations(QUEEN_TRADES, EntryStacks.of(BzItems.BEE_QUEEN_SPAWN_EGG.get()));
        registry.addWorkstations(QUEEN_RANDOMIZE_TRADES, EntryStacks.of(BzItems.BEE_QUEEN_SPAWN_EGG.get()));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new OverlayDecider() {
            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                return true;
            }

            @Override
            public <R extends Screen> InteractionResult shouldScreenBeOverlaid(R screen) {
                return screen.getClass() == CrystallineFlowerScreen.class ?
                        InteractionResult.FAIL : InteractionResult.PASS;
            }
        });
    }
}