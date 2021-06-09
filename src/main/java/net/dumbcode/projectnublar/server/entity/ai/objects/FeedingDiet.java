package net.dumbcode.projectnublar.server.entity.ai.objects;

import net.dumbcode.dumblibrary.DumbLibrary;
import net.dumbcode.dumblibrary.server.utils.ItemStackUtils;
import net.dumbcode.projectnublar.server.entity.ai.FeedingResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FeedingDiet {
    private final Map<BlockState, FeedingResult> blocks = new HashMap<>();
    private final Map<ItemStack, FeedingResult> items = new HashMap<>();
    private final Map<EntityType<?>, FeedingResult> entities = new HashMap<>();

    public Optional<FeedingResult> getResult(BlockState state) {
        return Optional.ofNullable(this.blocks.get(state));
    }

    public Optional<FeedingResult> getResult(ItemStack item) {
        for (Map.Entry<ItemStack, FeedingResult> entry : this.items.entrySet()) {
            if(entry.getKey().sameItem(item) && ItemStackUtils.compareControlledNbt(entry.getKey().getTag(), item.getTag())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Optional<FeedingResult> getResult(Entity entity) {
        EntityType<?> type = entity.getType();
        if(this.entities.containsKey(type)) {
            return Optional.ofNullable(this.entities.get(type));
        }
        return Optional.empty();
    }

    public FeedingDiet add(int food, int water, BlockState... states) {
        FeedingResult result = new FeedingResult(food, water);
        for (BlockState state : states) {
            this.blocks.put(state, result);
        }
        return this;
    }

    public FeedingDiet add(int food, int water, Block... blocks) {
        for (Block block : blocks) {
            this.add(food, water, block.getStateDefinition().getPossibleStates().toArray(new BlockState[0]));
        }
        return this;
    }

    public final FeedingDiet add(int food, int water, ItemStack... stackPredicates) {
        FeedingResult result = new FeedingResult(food, water);
        for (ItemStack stack : stackPredicates) {
            this.items.put(stack, result);
        }
        return this;
    }

    public FeedingDiet add(int food, int water, Item... items) {
        FeedingResult result = new FeedingResult(food, water);
        for (Item item : items) {
            this.items.put(new ItemStack(item), result);
        }
        return this;
    }

    public final FeedingDiet add(int food, int water, EntityType<?>... entities) {
        FeedingResult result = new FeedingResult(food, water);
        for (EntityType<?> entity : entities) {
            this.entities.put(entity, result);
        }
        return this;
    }

    public FeedingDiet copyInto(FeedingDiet diet) {
        diet.blocks.clear();
        diet.blocks.putAll(this.blocks);

        diet.items.clear();
        diet.items.putAll(this.items);

        diet.entities.clear();
        diet.entities.putAll(this.entities);

        return diet;
    }


    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        ListNBT blocks = new ListNBT();
        ListNBT items = new ListNBT();
        ListNBT entities = new ListNBT();

        this.blocks.forEach((state, result) -> {
            CompoundNBT compound = new CompoundNBT();
            compound.put("blockstate", NBTUtil.writeBlockState(state));
            compound.put("result", FeedingResult.writeToNBT(new CompoundNBT(), result));
            blocks.add(compound);
        });
        this.items.forEach((stack, result) -> {
            CompoundNBT compound = new CompoundNBT();
            compound.put("itemstack", stack.serializeNBT());
            compound.put("result", FeedingResult.writeToNBT(new CompoundNBT(), result));
            items.add(compound);
        });
        this.entities.forEach((type, result) -> {
            CompoundNBT compound = new CompoundNBT();
            compound.putString("entity_type", type.getRegistryName().toString());
            compound.put("result", FeedingResult.writeToNBT(new CompoundNBT(), result));
            entities.add(compound);
        });

        nbt.put("blocks", blocks);
        nbt.put("items", items);
        nbt.put("entities", entities);
        return nbt;
    }

    public void fromNBT(CompoundNBT nbt) {
        this.blocks.clear();
        this.items.clear();
        this.entities.clear();

        for (INBT base : nbt.getList("blocks", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT compound = (CompoundNBT) base;
            this.blocks.put(NBTUtil.readBlockState(compound.getCompound("blockstate")), FeedingResult.readFromNbt(compound.getCompound("result")));
        }
        for (INBT base : nbt.getList("items", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT compound = (CompoundNBT) base;
            this.items.put(ItemStack.of(compound.getCompound("itemstack")), FeedingResult.readFromNbt(compound.getCompound("result")));
        }
        for (INBT base : nbt.getList("entities", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT compound = (CompoundNBT) base;
            ResourceLocation resourceLocation = new ResourceLocation(compound.getString("entity_type"));
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(resourceLocation);
            if(type != null) {
                this.entities.put(type, FeedingResult.readFromNbt(compound.getCompound("result")));
            } else {
                DumbLibrary.getLogger().warn("Skipping type {} as it doesn't exist", resourceLocation);
            }
        }
    }
}
