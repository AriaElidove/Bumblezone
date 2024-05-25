package com.telepathicgrunt.the_bumblezone.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.items.BuzzingBriefcase;
import com.telepathicgrunt.the_bumblezone.menus.BuzzingBriefcaseMenu;
import com.telepathicgrunt.the_bumblezone.mixin.client.EntityRenderersAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.entities.BeeEntityInvoker;
import com.telepathicgrunt.the_bumblezone.mixin.entities.EntityAccessor;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.ModCompat;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BuzzingBriefcaseScreen extends AbstractContainerScreen<BuzzingBriefcaseMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/background.png");
    private static final ResourceLocation BEE_SLOT_BACKGROUND = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_slots.png");
    private static final ResourceLocation GENERAL_ICONS = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/general_icons.png");


    private static final ResourceLocation BEE_VANILLA_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_vanilla.png");
    private static final ResourceLocation BEE_BASE_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_base_layer.png");
    private static final ResourceLocation BEE_PRIMARY_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_primary_layer.png");
    private static final ResourceLocation BEE_SECONDARY_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_secondary_layer.png");
    private static final ResourceLocation BEE_STINGER_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_stinger.png");
    private static final ResourceLocation BEE_POLLEN_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_pollen.png");
    private static final ResourceLocation BEE_BABY_BASE_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_baby_base_layer.png");
    private static final ResourceLocation BEE_BABY_PRIMARY_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_baby_primary_layer.png");
    private static final ResourceLocation BEE_BABY_SECONDARY_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_baby_secondary_layer.png");
    private static final ResourceLocation BEE_BABY_STINGER_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_baby_stinger.png");
    private static final ResourceLocation BEE_BABY_POLLEN_ICON = new ResourceLocation(Bumblezone.MODID, "textures/gui/buzzing_briefcase/bee_icon_baby_pollen.png");

    private static final int NORMAL_PRIMARY_COLOR = 0xE59900;
    private static final int NORMAL_SECONDARY_COLOR = 0x231100;
    private static final int MISSING_PRIMARY_COLOR = 0x000000;
    private static final int MISSING_SECONDARY_COLOR = 0xF200FF;
    private static final int[] IGNORE_COLORS = {
            0x7CC9D1,
            0x1E1E28,
            0x302B37,
            0xF7FDFD,
            0xF1F2E0,
            0x5F3225
    };
    private static final float SCALE = 1.25f;
    private static final int MENU_HEIGHT = (int) (174 * SCALE);
    private static final int MENU_WIDTH = (int) (306 * SCALE);
    private static final int MAX_ROW_LENGTH = 7;

    private record BeeState(Bee beeEntity, int primaryColor, int secondaryColor){}
    private final List<BeeState> BEE_INVENTORY = new ArrayList<>();
    private final Inventory inventory;
    private CompoundTag cachedBriefcaseTag;

    public BuzzingBriefcaseScreen(BuzzingBriefcaseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 126;
        this.titleLabelX = 75;
        this.titleLabelY = -38;
        this.inventory = playerInventory;

    }

    @Override
    protected void init() {
        this.leftPos = (getTrueWidth() - this.imageWidth) / 2;
        this.topPos = (getTrueHeight() - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ItemStack briefcaseStack = menu.getItems().get(0);
        if (!briefcaseStack.isEmpty() && !briefcaseStack.getComponents().get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).getUnsafe().equals(this.cachedBriefcaseTag)) {
            this.cachedBriefcaseTag = briefcaseStack.getComponents().get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).getUnsafe();

            List<Entity> beesStored = BuzzingBriefcase.getBeesStored(inventory.player.level(), briefcaseStack, false);
            if (isDiffFoundInBeeList(beesStored)) {

                BEE_INVENTORY.clear();
                for (Entity entity : beesStored) {

                    if (entity instanceof Bee bee) {
                        bee.stopBeingAngry();
                        boolean pollinated = bee.hasNectar();
                        ((BeeEntityInvoker)bee).callSetHasNectar(false);
                        try {
                            addBeeWithColor(bee);
                        }
                        catch (Exception e) {
                            BEE_INVENTORY.add(new BeeState(bee, MISSING_PRIMARY_COLOR, MISSING_SECONDARY_COLOR));
                            Bumblezone.LOGGER.warn("Bumblezone Buzzing Briefcase Clientside: Error trying to dynamically get color for following bee -");
                            CompoundTag tag = new CompoundTag();
                            bee.saveWithoutId(tag);
                            Bumblezone.LOGGER.warn("Bee: {}", tag);
                        }
                        ((BeeEntityInvoker)bee).callSetHasNectar(pollinated);
                    }
                }
            }
        }

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        RenderSystem.enableDepthTest();

        drawBeeSlots(guiGraphics, leftPos, topPos, mouseX, mouseY);
    }

    private boolean isDiffFoundInBeeList(List<Entity> beesStored) {
        if (BEE_INVENTORY.size() != beesStored.size()) {
            return true;
        }
        else {
            for (int i = 0; i < beesStored.size(); i++) {
                Entity beeFound = beesStored.get(i);
                Bee beeSaved = BEE_INVENTORY.get(i).beeEntity();

                if (beeFound instanceof Bee bee) {
                    if (bee.hasNectar() != beeSaved.hasNectar()) {
                        return true;
                    }
                    else if (bee.getHealth() != beeSaved.getHealth()) {
                        return true;
                    }
                    else if (bee.isBaby() != beeSaved.isBaby()) {
                        return true;
                    }
                    else if (bee.hasStung() != beeSaved.hasStung()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addBeeWithColor(Bee bee) throws IOException {
        int primaryColor = NORMAL_PRIMARY_COLOR;
        int secondaryColor = NORMAL_SECONDARY_COLOR;

        if (bee.getType() == EntityType.BEE) {
            BEE_INVENTORY.add(new BeeState(bee, primaryColor, secondaryColor));
            return;
        }

        for (ModCompat compat : ModChecker.BEE_COLOR_COMPATS) {
            Pair<Integer, Integer> moddedBeeColors = compat.getModdedBeePrimaryAndSecondaryColors(bee);
            if (moddedBeeColors != null) {
                BEE_INVENTORY.add(new BeeState(bee, moddedBeeColors.getFirst(), moddedBeeColors.getSecond()));
                return;
            }
        }

        EntityRendererProvider<Bee> rendererProvider = (EntityRendererProvider<Bee>) EntityRenderersAccessor.getPROVIDERS().get(bee.getType());
        if (rendererProvider != null) {
            EntityRenderer<Bee> entityRenderer = rendererProvider.create(new EntityRendererProvider.Context(
                    minecraft.getEntityRenderDispatcher(),
                    minecraft.getItemRenderer(),
                    minecraft.getBlockRenderer(),
                    new ItemInHandRenderer(minecraft, minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer()),
                    minecraft.getResourceManager(),
                    minecraft.getEntityModels(),
                    this.font));
            ResourceLocation textureLocation = entityRenderer.getTextureLocation(bee);

            int[] pixels = LegacyStuffWrapper.getPixels(minecraft.getResourceManager(), textureLocation);
            if (pixels == null || pixels.length == 0) {
                throw new RuntimeException("No pixels found for bee texture.");
            }

            List<Integer> colors = new ObjectArrayList<>();
            Map<Integer, Integer> deniedColors = new HashMap<>();
            for (int pixel : pixels) {
                if (GeneralUtils.getAlpha(pixel) > 0.15) {
                    boolean isColorSimilarToDisallowedColors = false;
                    for (int disallowedColor : IGNORE_COLORS) {
                        if (GeneralUtils.isSimilarInColor(pixel, disallowedColor, 1)) {
                            isColorSimilarToDisallowedColors = true;
                            break;
                        }
                    }

                    if (!isColorSimilarToDisallowedColors) {
                        colors.add(pixel);
                    }
                    else {
                        deniedColors.put(pixel, deniedColors.getOrDefault(pixel, 0) + 1);
                    }
                }
            }

            for (Map.Entry<Integer, Integer> entry : deniedColors.entrySet()) {
                if (entry.getValue() >= 200) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        colors.add(entry.getKey());
                    }
                }
            }
            deniedColors.clear();

            List<Integer> originalColors = new ObjectArrayList<>();
            List<Integer> averagedColors = new ObjectArrayList<>();
            List<Integer> combinedColorCount = new ObjectArrayList<>();

            for (int i = colors.size() - 1; i >= 0; i--) {
                int color = colors.remove(i);
                if (averagedColors.isEmpty()) {
                    originalColors.add(color);
                    averagedColors.add(color);
                    combinedColorCount.add(1);
                }
                else {
                    boolean combined = false;
                    for (int k = averagedColors.size() - 1; k >= 0; k--) {
                        int originalColor = originalColors.get(k);
                        int averagedColor = averagedColors.get(k);
                        if (GeneralUtils.isSimilarInVisualColor(color, originalColor, 35, 80)) {
                            averagedColors.set(k, GeneralUtils.colorToInt(
                                ((GeneralUtils.getRed(color) + GeneralUtils.getRed(averagedColor)) / 2),
                                ((GeneralUtils.getGreen(color) + GeneralUtils.getGreen(averagedColor)) / 2),
                                ((GeneralUtils.getBlue(color) + GeneralUtils.getBlue(averagedColor)) / 2)
                            ));
                            combinedColorCount.set(k, combinedColorCount.get(k) + 1);
                            combined = true;
                        }
                    }

                    if (!combined) {
                        originalColors.add(color);
                        averagedColors.add(color);
                        combinedColorCount.add(1);
                    }
                }
            }

            int largestColorIndex1 = 0;
            int largestColorIndex2 = 0;
            for (int i = 0; i < combinedColorCount.size(); i++) {
                if (combinedColorCount.get(i) >= combinedColorCount.get(largestColorIndex1)) {
                    if (combinedColorCount.get(largestColorIndex1) >= combinedColorCount.get(largestColorIndex2)) {
                        largestColorIndex2 = largestColorIndex1;
                    }

                    largestColorIndex1 = i;
                }
                else if (largestColorIndex2 == largestColorIndex1 || (largestColorIndex1 != i && combinedColorCount.get(i) >= combinedColorCount.get(largestColorIndex2))) {
                    largestColorIndex2 = i;
                }
            }

            primaryColor = averagedColors.get(largestColorIndex1);
            secondaryColor = averagedColors.get(largestColorIndex2);
        }

        BEE_INVENTORY.add(new BeeState(bee, primaryColor, secondaryColor));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialtick, int x, int y) {
        int startX = (getTrueWidth() - MENU_WIDTH) / 2;
        int startY = (getTrueHeight() - MENU_HEIGHT) / 2;
        RenderSystem.enableDepthTest();
        guiGraphics.blit(
                CONTAINER_BACKGROUND,
                startX,
                startY,
                0,
                0,
                MENU_WIDTH,
                MENU_HEIGHT * 2,
                MENU_WIDTH,
                MENU_HEIGHT * 2
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
        guiGraphics.drawString(this.font, this.title, 74, -38, 0xFFEFAF, true);
    }

    protected void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int rowIndex = 0;
        int columnIndex = 0;
        for (BeeState beeState : BEE_INVENTORY) {
            int xOffset = getXOffset(rowIndex);
            int yOffset = getYOffset(columnIndex);
            int mainX = leftPos + xOffset;
            int mainY = topPos + yOffset;

            rowIndex++;
            if (rowIndex == MAX_ROW_LENGTH) {
                rowIndex = 0;
                columnIndex++;
            }

            if (mouseX - mainX >= 0.0D &&
                    mouseX - mainX < 22.0D &&
                    mouseY - mainY >= 0.0D &&
                    mouseY - mainY < 22.0D)
            {
                Entity beeEntity = beeState.beeEntity();
                Component beeNormalAndCustomName = beeEntity.getName();
                Component beeNoneCustomName = beeEntity.getName();
                if (beeNoneCustomName != null && beeNoneCustomName.equals(beeEntity.getCustomName())) {
                    beeNoneCustomName = ((EntityAccessor)beeEntity).callGetTypeName();
                }

                boolean isNameAndTypeEqual =
                        beeNoneCustomName != null &&
                        beeNoneCustomName.equals(beeNormalAndCustomName);

                List<Component> toolTipComponents =  new ArrayList<>();

                if (isNameAndTypeEqual) {
                    toolTipComponents.add(Component.translatable("item.the_bumblezone.buzzing_briefcase_bee_type", beeNormalAndCustomName).withStyle(ChatFormatting.YELLOW));
                }
                else {
                    toolTipComponents.add(Component.translatable("item.the_bumblezone.buzzing_briefcase_bee_name", beeNormalAndCustomName));
                    toolTipComponents.add(Component.translatable("item.the_bumblezone.buzzing_briefcase_bee_type", beeNoneCustomName).withStyle(ChatFormatting.YELLOW));
                }

                if (GeneralUtilsClient.isAdvancedToolTipActive()) {
                    toolTipComponents.add(Component.translatable("item.the_bumblezone.buzzing_briefcase_bee_registry_name", BuiltInRegistries.ENTITY_TYPE.getKey(beeState.beeEntity().getType())).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                }

                guiGraphics.renderTooltip(
                    this.font,
                    toolTipComponents,
                    Optional.empty(),
                    mouseX,
                    mouseY);
            }
            else if (mouseX - (mainX + 22) >= 0.0D &&
                    mouseX - (mainX + 22) < 11.0D &&
                    mouseY - mainY >= 0.0D &&
                    mouseY - mainY < 11.0D)
            {
                Entity beeEntity = beeState.beeEntity();
                Component beeNormalAndCustomName = beeEntity.getName();

                guiGraphics.renderTooltip(
                    this.font,
                    List.of(
                        Component.translatable("item.the_bumblezone.buzzing_briefcase_release", beeNormalAndCustomName)
                    ),
                    Optional.empty(),
                    mouseX,
                    mouseY);
            }
            else if (mouseX - mainX >= 0.0D &&
                    mouseX - mainX < 11.0D &&
                    mouseY - (mainY + 22) >= 0.0D &&
                    mouseY - (mainY + 22) < 11.0D)
            {
                if (beeState.beeEntity().getHealth() < beeState.beeEntity().getMaxHealth()) {
                    boolean hasHoneyBottleItem = inventory.contains(Items.HONEY_BOTTLE.getDefaultInstance());
                    if (hasHoneyBottleItem) {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_health_1"),
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_health_2")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                    else {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_health_missing_item")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                }
            }
            else if (mouseX - (mainX + 11) >= 0.0D &&
                    mouseX - (mainX + 11) < 11.0D &&
                    mouseY - (mainY + 22) >= 0.0D &&
                    mouseY - (mainY + 22) < 11.0D)
            {
                if (beeState.beeEntity().hasStung()) {
                    boolean hasBeeStingerItem = inventory.contains(BzItems.BEE_STINGER.get().getDefaultInstance());
                    if (hasBeeStingerItem) {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_stinger_1"),
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_stinger_2")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                    else {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_stinger_missing_item")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                }
            }
            else if (mouseX - mainX >= 0.0D &&
                    mouseX - mainX < 11.0D &&
                    mouseY - (mainY + 33) >= 0.0D &&
                    mouseY - (mainY + 33) < 11.0D)
            {
                if (beeState.beeEntity().isBaby()) {
                    boolean hasHoneyBottleItem = inventory.contains(Items.HONEY_BOTTLE.getDefaultInstance());
                    if (hasHoneyBottleItem) {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_grow_up_1"),
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_grow_up_2")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                    else {
                        guiGraphics.renderTooltip(
                            this.font,
                            List.of(
                                Component.translatable("item.the_bumblezone.buzzing_briefcase_grow_up_missing_item")
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY);
                    }
                }
            }
            else if (mouseX - (mainX + 11) >= 0.0D &&
                    mouseX - (mainX + 11) < 11.0D &&
                    mouseY - (mainY + 33) >= 0.0D &&
                    mouseY - (mainY + 33) < 11.0D)
            {
                if (beeState.beeEntity().getType().is(BzTags.BUZZING_BRIEFCASE_CAN_POLLINATE)) {
                    if (!beeState.beeEntity().hasNectar()) {
                        boolean hasPollenPuffItem = inventory.contains(BzItems.POLLEN_PUFF.get().getDefaultInstance());
                        if (hasPollenPuffItem) {
                            guiGraphics.renderTooltip(
                                this.font,
                                List.of(
                                    Component.translatable("item.the_bumblezone.buzzing_briefcase_pollen_1"),
                                    Component.translatable("item.the_bumblezone.buzzing_briefcase_pollen_2")
                                ),
                                Optional.empty(),
                                mouseX,
                                mouseY);
                        }
                        else {
                            guiGraphics.renderTooltip(
                                this.font,
                                List.of(
                                    Component.translatable("item.the_bumblezone.buzzing_briefcase_pollen_missing_item")
                                ),
                                Optional.empty(),
                                mouseX,
                                mouseY);
                        }
                    }
                }
            }
        }
    }

    private void drawBeeSlots(GuiGraphics guiGraphics, int startX, int startY, int mouseX, int mouseY) {
        boolean hasStingerItem = false;
        boolean hasHoneyBottleItem = false;
        boolean hasPollenPuffItem = false;
        if (!BEE_INVENTORY.isEmpty()) {
            hasStingerItem = inventory.contains(BzItems.BEE_STINGER.get().getDefaultInstance());
            hasHoneyBottleItem = inventory.contains(Items.HONEY_BOTTLE.getDefaultInstance());
            hasPollenPuffItem = inventory.contains(BzItems.POLLEN_PUFF.get().getDefaultInstance());
        }

        int rowIndex = 0;
        int columnIndex = 0;
        for (int beeIndex = 0; beeIndex < BuzzingBriefcase.MAX_NUMBER_OF_BEES; beeIndex++) {
            int xOffset = getXOffset(rowIndex);
            int yOffset = getYOffset(columnIndex);
            int mainX = startX + xOffset;
            int mainY = startY + yOffset;

            rowIndex++;
            if (rowIndex == MAX_ROW_LENGTH) {
                rowIndex = 0;
                columnIndex++;
            }

            BeeState beeState = null;
            if (beeIndex < BEE_INVENTORY.size()) {
                beeState = BEE_INVENTORY.get(beeIndex);
            }

            //mainSlot
            RenderSystem.enableDepthTest();
            if (beeState == null) {
                guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX, mainY, 22, 0, 22, 22, 64, 64);
                guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX, mainY + 22, 22, 22, 11, 11, 64, 64);
                guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX + 11, mainY + 22, 22, 22, 11, 11, 64, 64);
                guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX, mainY + 33, 22, 22, 11, 11, 64, 64);
                guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX + 11, mainY + 33, 22, 22, 11, 11, 64, 64);
                continue;
            }

            drawBeeSlot(guiGraphics, mainX, mainY, beeState);
            renderHealthButton(guiGraphics, mouseX, mouseY, hasHoneyBottleItem, mainX, mainY, beeState);
            renderStingerButton(guiGraphics, mouseX, mouseY, hasStingerItem, mainX, mainY, beeState);
            renderGrowUpButton(guiGraphics, mouseX, mouseY, hasHoneyBottleItem, mainX, mainY, beeState);
            renderPollenButton(guiGraphics, mouseX, mouseY, hasPollenPuffItem, mainX, mainY, beeState);
            renderReleaseButton(guiGraphics, mouseX, mouseY, mainX, mainY);
            renderButtonTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    private static void drawBeeSlot(GuiGraphics guiGraphics, int mainX, int mainY, BeeState beeState) {
        guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX, mainY, 0, 0, 22, 22, 64, 64);

        boolean isBaby = beeState.beeEntity().isBaby();
        if (beeState.beeEntity().getType() == EntityType.BEE) {
            int beeIconXOffset = 0;
            int beeIconYOffset = 0;
            if (isBaby) {
                beeIconYOffset += 16;
            }
            if (beeState.beeEntity().hasStung()) {
                beeIconXOffset += 16;
            }
            if (beeState.beeEntity().hasNectar()) {
                beeIconXOffset += 32;
            }

            guiGraphics.blit(BEE_VANILLA_ICON, mainX + 3, mainY + 3, beeIconXOffset, beeIconYOffset, 16, 16, 64, 64);
        }
        else {
            guiGraphics.blit(isBaby ? BEE_BABY_BASE_ICON : BEE_BASE_ICON, mainX + 3, mainY + 3, 0, 0, 16, 16, 16, 16);
            guiGraphics.setColor(GeneralUtils.getRed(beeState.primaryColor()) / 255f, GeneralUtils.getGreen(beeState.primaryColor()) / 255f, GeneralUtils.getBlue(beeState.primaryColor()) / 255f, 1F);
            guiGraphics.blit(isBaby ? BEE_BABY_PRIMARY_ICON : BEE_PRIMARY_ICON, mainX + 3, mainY + 3, 0, 0, 16, 16, 16, 16);
            guiGraphics.setColor(GeneralUtils.getRed(beeState.secondaryColor()) / 255f, GeneralUtils.getGreen(beeState.secondaryColor()) / 255f, GeneralUtils.getBlue(beeState.secondaryColor()) / 255f, 1F);
            guiGraphics.blit(isBaby ? BEE_BABY_SECONDARY_ICON : BEE_SECONDARY_ICON, mainX + 3, mainY + 3, 0, 0, 16, 16, 16, 16);
            guiGraphics.setColor(1F, 1F, 1F, 1F);
            if (!beeState.beeEntity().hasStung()) {
                guiGraphics.blit(isBaby ? BEE_BABY_STINGER_ICON : BEE_STINGER_ICON, mainX + 3, mainY + 3, 0, 0, 16, 16, 16, 16);
            }
            if (beeState.beeEntity().hasNectar()) {
                guiGraphics.blit(isBaby ? BEE_BABY_POLLEN_ICON : BEE_POLLEN_ICON, mainX + 3, mainY + 3, 0, 0, 16, 16, 16, 16);
            }
        }

        float healthPercentage = Math.min(1, beeState.beeEntity().getHealth() / beeState.beeEntity().getMaxHealth());
        int barColor = Mth.hsvToRgb(healthPercentage / 3.0f, 1.0f, 1.0f);
        int barWidth = (int) (Math.max(1, 16 * healthPercentage));
        guiGraphics.fill(RenderType.guiOverlay(), mainX + 3, mainY + 19, mainX + 3 + barWidth, mainY + 18, barColor | 0xFF000000);

    }

    private static void renderReleaseButton(GuiGraphics guiGraphics, int mouseX, int mouseY, int mainX, int mainY) {
        //hover release button
        if (mouseX - (mainX + 22) >= 0.0D &&
            mouseX - (mainX + 22) < 11.0D &&
            mouseY - mainY >= 0.0D &&
            mouseY - mainY < 11.0D)
        {
            guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX + 22, mainY, 33, 33, 11, 11, 64, 64);
        }
        //release button
        else {
            guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX + 22, mainY, 0, 33, 11, 11, 64, 64);
        }
    }

    private static void renderHealthButton(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hasHoneyBottle, int mainX, int mainY, BeeState beeState) {
        //has health button
        if (beeState.beeEntity().getHealth() == beeState.beeEntity().getMaxHealth()) {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 22, 11, 11, 11, 11, 64, 64);
        }
        //no inventory stinger button
        else if (!hasHoneyBottle) {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 22, 22, 11, 11, 11, 64, 64);
        }
        //hover/click health button
        else if (mouseX - mainX >= 0.0D &&
                mouseX - mainX < 11.0D &&
                mouseY - (mainY + 22) >= 0.0D &&
                mouseY - (mainY + 22) < 11.0D)
        {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 22, 33, 11, 11, 11, 64, 64);
        }
        //needs health button
        else {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 22, 0, 11, 11, 11, 64, 64);
        }
    }

    private static void renderStingerButton(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hasStingerItem, int mainX, int mainY, BeeState beeState) {
        //has stinger button
        if (!beeState.beeEntity().hasStung()) {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 22, 11, 0, 11, 11, 64, 64);
        }
        //no inventory stinger button
        else if (!hasStingerItem) {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 22, 22, 0, 11, 11, 64, 64);
        }
        //hover stinger button
        else if (mouseX - (mainX + 11) >= 0.0D &&
                mouseX - (mainX + 11) < 11.0D &&
                mouseY - (mainY + 22) >= 0.0D &&
                mouseY - (mainY + 22) < 11.0D)
        {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 22, 33, 0, 11, 11, 64, 64);
        }
        //normal stinger button
        else {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 22, 0, 0, 11, 11, 64, 64);
        }
    }

    private static void renderGrowUpButton(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hasHoneyBottle, int mainX, int mainY, BeeState beeState) {
        //cannot grow up button
        if (!beeState.beeEntity().isBaby()) {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 33, 11, 33, 11, 11, 64, 64);
        }
        //no inventory honey bottle button
        else if (!hasHoneyBottle) {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 33, 22, 33, 11, 11, 64, 64);
        }
        //hover grow up button
        else if (mouseX - mainX >= 0.0D &&
                mouseX - mainX < 11.0D &&
                mouseY - (mainY + 33) >= 0.0D &&
                mouseY - (mainY + 33) < 11.0D)
        {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 33, 33, 33, 11, 11, 64, 64);
        }
        //normal grow up button
        else {
            guiGraphics.blit(GENERAL_ICONS, mainX, mainY + 33, 0, 33, 11, 11, 64, 64);
        }
    }

    private static void renderPollenButton(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hasPollenPuff, int mainX, int mainY, BeeState beeState) {
        // Cannot pollinate
        if (!beeState.beeEntity().getType().is(BzTags.BUZZING_BRIEFCASE_CAN_POLLINATE)) {
            guiGraphics.blit(BEE_SLOT_BACKGROUND, mainX + 11, mainY + 33, 22, 22, 11, 11, 64, 64);
        }
        //cannot pollinate button
        else if (beeState.beeEntity().hasNectar()) {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 33, 11, 22, 11, 11, 64, 64);
        }
        //no inventory pollen puff button
        else if (!hasPollenPuff) {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 33, 22, 22, 11, 11, 64, 64);
        }
        //hover pollinate button
        else if (mouseX - (mainX + 11) >= 0.0D &&
                mouseX - (mainX + 11) < 11.0D &&
                mouseY - (mainY + 33) >= 0.0D &&
                mouseY - (mainY + 33) < 11.0D)
        {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 33, 33, 22, 11, 11, 64, 64);
        }
        //normal pollinate button
        else {
            guiGraphics.blit(GENERAL_ICONS, mainX + 11, mainY + 33, 0, 22, 11, 11, 64, 64);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int rowIndex = 0;
        int columnIndex = 0;
        for (int beeIndex = 0; beeIndex < BuzzingBriefcase.MAX_NUMBER_OF_BEES; beeIndex++) {
            if (beeIndex >= BEE_INVENTORY.size()) {
                break;
            }
            Bee bee = BEE_INVENTORY.get(beeIndex).beeEntity();

            int xOffset = getXOffset(rowIndex);
            int yOffset = getYOffset(columnIndex);
            int mainX = leftPos + xOffset;
            int mainY = topPos + yOffset;

            rowIndex++;
            if (rowIndex == MAX_ROW_LENGTH) {
                rowIndex = 0;
                columnIndex++;
            }

            if (mouseX - (mainX + 22) >= 0.0D &&
                mouseX - (mainX + 22) < 11.0D &&
                mouseY - mainY >= 0.0D &&
                mouseY - mainY < 11.0D)
            {
                sendButtonPressToMenu((beeIndex * BuzzingBriefcaseMenu.NUMBER_OF_BUTTONS) + BuzzingBriefcaseMenu.RELEASE_ID);
            }
            else if (mouseX - mainX >= 0.0D &&
                    mouseX - mainX < 11.0D &&
                    mouseY - (mainY + 22) >= 0.0D &&
                    mouseY - (mainY + 22) < 11.0D)
            {
                if (bee.getHealth() < bee.getMaxHealth()) {
                    sendButtonPressToMenu((beeIndex * BuzzingBriefcaseMenu.NUMBER_OF_BUTTONS) + BuzzingBriefcaseMenu.HEALTH_ID);

                    int honeyBottleSlotIndex = inventory.findSlotMatchingItem(Items.HONEY_BOTTLE.getDefaultInstance());
                    if (honeyBottleSlotIndex != -1) {
                        ItemStack playerHoneyBottleStack = inventory.getItem(honeyBottleSlotIndex);
                        if (!playerHoneyBottleStack.isEmpty()) {
                            if (!menu.player.getAbilities().instabuild) {
                                playerHoneyBottleStack.shrink(1);
                                if (playerHoneyBottleStack.isEmpty()) {
                                    inventory.add(honeyBottleSlotIndex, Items.GLASS_BOTTLE.getDefaultInstance());
                                } else {
                                    inventory.add(Items.GLASS_BOTTLE.getDefaultInstance());
                                }
                            }
                        }
                    }
                }
            }
            else if (mouseX - (mainX + 11) >= 0.0D &&
                    mouseX - (mainX + 11) < 11.0D &&
                    mouseY - (mainY + 22) >= 0.0D &&
                    mouseY - (mainY + 22) < 11.0D)
            {
                if (bee.hasStung()) {
                    sendButtonPressToMenu((beeIndex * BuzzingBriefcaseMenu.NUMBER_OF_BUTTONS) + BuzzingBriefcaseMenu.STINGER_ID);

                    int stingerSlotIndex = inventory.findSlotMatchingItem(BzItems.BEE_STINGER.get().getDefaultInstance());
                    if (stingerSlotIndex != -1) {
                        ItemStack playerStingerStack = inventory.getItem(stingerSlotIndex);
                        if (!playerStingerStack.isEmpty()) {
                            ((BeeEntityInvoker) bee).callSetHasStung(false);

                            if (!menu.player.getAbilities().instabuild) {
                                playerStingerStack.shrink(1);
                            }
                        }
                    }
                }
            }
            else if (mouseX - mainX >= 0.0D &&
                    mouseX - mainX < 11.0D &&
                    mouseY - (mainY + 33) >= 0.0D &&
                    mouseY - (mainY + 33) < 11.0D)
            {
                if (bee.isBaby()) {
                    sendButtonPressToMenu((beeIndex * BuzzingBriefcaseMenu.NUMBER_OF_BUTTONS) + BuzzingBriefcaseMenu.GROW_UP_ID);

                    int honeyBottleSlotIndex = inventory.findSlotMatchingItem(Items.HONEY_BOTTLE.getDefaultInstance());
                    if (honeyBottleSlotIndex != -1) {
                        ItemStack playerHoneyBottleStack = inventory.getItem(honeyBottleSlotIndex);
                        if (!playerHoneyBottleStack.isEmpty()) {
                            if (!menu.player.getAbilities().instabuild) {
                                playerHoneyBottleStack.shrink(1);
                                if (playerHoneyBottleStack.isEmpty()) {
                                    inventory.add(honeyBottleSlotIndex, Items.GLASS_BOTTLE.getDefaultInstance());
                                } else {
                                    inventory.add(Items.GLASS_BOTTLE.getDefaultInstance());
                                }
                            }
                        }
                    }
                }
            }
            else if (mouseX - (mainX + 11) >= 0.0D &&
                    mouseX - (mainX + 11) < 11.0D &&
                    mouseY - (mainY + 33) >= 0.0D &&
                    mouseY - (mainY + 33) < 11.0D)
            {
                if (!bee.hasNectar() && bee.getType().is(BzTags.BUZZING_BRIEFCASE_CAN_POLLINATE)) {
                    sendButtonPressToMenu((beeIndex * BuzzingBriefcaseMenu.NUMBER_OF_BUTTONS) + BuzzingBriefcaseMenu.POLLEN_ID);

                    int pollenSlotIndex = inventory.findSlotMatchingItem(BzItems.POLLEN_PUFF.get().getDefaultInstance());
                    if (pollenSlotIndex != -1) {
                        ItemStack playerPollenStack = inventory.getItem(pollenSlotIndex);
                        if (!playerPollenStack.isEmpty()) {
                            if (!menu.player.getAbilities().instabuild) {
                                playerPollenStack.shrink(1);
                            }
                        }
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return false;
    }

    private void sendButtonPressToMenu(Integer sectionId) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, sectionId);
    }

    private int getTrueWidth() {
        return width;
    }

    private int getTrueHeight() {
        return height - 29;
    }

    private static int getXOffset(int rowIndex) {
        return -11 + (rowIndex * 38);
    }

    private static int getYOffset(int columnIndex) {
        return 46 + (columnIndex * 54);
    }
}