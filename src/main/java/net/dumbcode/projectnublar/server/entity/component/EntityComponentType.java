package net.dumbcode.projectnublar.server.entity.component;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityComponentType<T extends EntityComponent> extends IForgeRegistryEntry<EntityComponentType<?>> {
    @Nonnull
    T construct();

    @Nonnull
    ResourceLocation getIdentifier();

    @Nonnull
    Class<? extends T> getType();

    @Override
    default Class<EntityComponentType<?>> getRegistryType() {
        return getWildcardType();
    }

    @Override
    default EntityComponentType<?> setRegistryName(ResourceLocation name) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    default ResourceLocation getRegistryName() {
        return this.getIdentifier();
    }

    @SuppressWarnings("unchecked")
    static Class<EntityComponentType<?>> getWildcardType() {
        return (Class<EntityComponentType<?>>) (Class<?>) EntityComponentType.class;
    }
}
