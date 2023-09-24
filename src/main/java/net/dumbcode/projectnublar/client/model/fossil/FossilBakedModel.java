package net.dumbcode.projectnublar.client.model.fossil;

import com.google.common.collect.ImmutableList;
import net.dumbcode.projectnublar.client.model.ModelUtils;
import net.dumbcode.projectnublar.server.block.entity.FossilBlockEntity;
import net.dumbcode.projectnublar.server.fossil.FossilHandler;
import net.dumbcode.projectnublar.server.fossil.StoneTypeHandler;
import net.dumbcode.projectnublar.server.fossil.base.Fossil;
import net.dumbcode.projectnublar.server.fossil.base.StoneType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.inventory.container.PlayerContainer.BLOCK_ATLAS;

public class FossilBakedModel implements IDynamicBakedModel {

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        BlockState baseState = getOrDefault(extraData, FossilBlockEntity.STONE_TYPE, StoneTypeHandler.GRANITE.get()).baseState.get();
        IBakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.stateToModelLocation(baseState));

        Fossil fossil = getOrDefault(extraData, FossilBlockEntity.FOSSIL, FossilHandler.AMMONITE.get());
        TextureAtlasSprite overlay = Minecraft.getInstance().getModelManager().getAtlas(BLOCK_ATLAS).getSprite(fossil.texture);

        List<BakedQuad> quads = baseModel.getQuads(state, side, rand, extraData);

        List<BakedQuad> out =  new ArrayList<>(quads);

        for (BakedQuad quad : quads) {
            out.add(ModelUtils.retextureQuad(quad, overlay, 0.001F));
        }

        return ImmutableList.copyOf(out);
    }

    public static <T> T getOrDefault(IModelData data, ModelProperty<T> property, T fallback) {
        if (data.hasProperty(property)) {
            return data.getData(property);
        }
        return fallback;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        BlockState baseState = getOrDefault(data, FossilBlockEntity.STONE_TYPE, StoneTypeHandler.GRANITE.get()).baseState.get();
        IBakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.stateToModelLocation(baseState));
        return baseModel.getParticleTexture(data);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
