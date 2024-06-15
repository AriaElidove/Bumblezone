package com.telepathicgrunt.the_bumblezone.client.rendering.purplespike;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.PurpleSpikeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PurpleSpikeRenderer<M extends EntityModel<PurpleSpikeEntity>>
        extends EntityRenderer<PurpleSpikeEntity>
        implements RenderLayerParent<PurpleSpikeEntity, M>
{
    private static final ResourceLocation SKIN = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/entity/purple_spike.png");
    protected final PurpleSpikeModel<PurpleSpikeEntity> model;
    protected final List<RenderLayer<PurpleSpikeEntity, M>> layers = Lists.newArrayList();

    public PurpleSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PurpleSpikeModel<>(context.bakeLayer(PurpleSpikeModel.LAYER_LOCATION));
    }

    protected final boolean addLayer(RenderLayer<PurpleSpikeEntity, M> renderLayer) {
        return this.layers.add(renderLayer);
    }

    @Override
    public M getModel() {
        return (M) this.model;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void render(PurpleSpikeEntity ringEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        float m = Mth.lerp(g, ringEntity.xRotO, ringEntity.getXRot());

        float offSet = 1.0f + Math.min(ringEntity.spikeChargeClientTimeTracker / 40f, 0.5f);

        poseStack.scale(1.0f, 1.0f, 1.0f);
        poseStack.translate(0.0f, offSet, 0.0f);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0f - ringEntity.getYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(180.0f - ringEntity.getXRot()));
        this.model.prepareMobModel(ringEntity, 0, 0, g);
        ((EntityModel)this.model).setupAnim(ringEntity, 0, 0, 0, 0, m);
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = this.isBodyVisible(ringEntity);
        boolean bl2 = !bl && !ringEntity.isInvisibleTo(minecraft.player);
        boolean bl3 = minecraft.shouldEntityAppearGlowing(ringEntity);
        RenderType renderType = this.getRenderType(ringEntity, bl, bl2, bl3);
        if (renderType != null) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            ((Model)this.model).renderToBuffer(poseStack, vertexConsumer, i, 0, FastColor.ARGB32.colorFromFloat(1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f));
        }
        if (!ringEntity.isSpectator()) {
            for (RenderLayer<PurpleSpikeEntity, M> renderLayer : this.layers) {
                renderLayer.render(poseStack, multiBufferSource, i, ringEntity, 0, 0, g, 0, 0, m);
            }
        }
        poseStack.popPose();
    }

    @Nullable
    protected RenderType getRenderType(PurpleSpikeEntity ringEntity, boolean bl, boolean bl2, boolean bl3) {
        ResourceLocation resourceLocation = this.getTextureLocation(ringEntity);
        if (bl2) {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        }
        if (bl) {
            return this.model.renderType(resourceLocation);
        }
        if (bl3) {
            return RenderType.outline(resourceLocation);
        }
        return null;
    }

    protected boolean isBodyVisible(PurpleSpikeEntity ringEntity) {
        return !ringEntity.isInvisible();
    }

    @Override
    public ResourceLocation getTextureLocation(PurpleSpikeEntity ringEntity) {
        return SKIN;
    }
}