package net.dumbcode.projectnublar.server.item;

import net.dumbcode.dumblibrary.server.utils.MathUtils;
import net.dumbcode.projectnublar.server.ProjectNublar;
import net.dumbcode.projectnublar.server.item.data.DriveUtils;
import net.dumbcode.projectnublar.server.utils.MachineUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSyringe extends Item implements DriveUtils.DriveInformation {

    private final Type type;

    public ItemSyringe(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if(!this.type.filled) {
            ItemStack out;
            if((entity instanceof ChickenEntity || entity instanceof ParrotEntity) && ((AnimalEntity) entity).isInLove()) {
                out = new ItemStack(ItemHandler.EMBRYO_FILLED_SYRINGE.get());
                out.getOrCreateTagElement(ProjectNublar.MODID).putString("ContainedType", entity.getType().getDescriptionId());
            } else {
                out = new ItemStack(ItemHandler.DNA_FILLED_SYRINGE.get());
                CompoundNBT nbt = out.getOrCreateTagElement(ProjectNublar.MODID);
                nbt.putString("ContainedType", entity.getType().getDescriptionId());
                nbt.putInt("ContainedSize", MathUtils.getWeightedResult(10, 5));
            }
            stack.shrink(1);
            if(stack.isEmpty()) {
                player.setItemInHand(Hand.MAIN_HAND, out);
            } else {
                MachineUtils.giveToInventory(player, out);
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }


    @Override
    public int getSize(ItemStack stack) {
        return stack.getOrCreateTagElement(ProjectNublar.MODID).getInt("ContainedSize");
    }

    @Override
    public String getKey(ItemStack stack) {
        return stack.getOrCreateTagElement(ProjectNublar.MODID).getString("ContainedType");
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getKey(stack);
    }

    @Override
    public String getDriveTranslationKey(ItemStack stack) {
        return this.getDescriptionId(stack) + ".name";
    }

    @Override
    public boolean hasInformation(ItemStack stack) {
        return this.type == Type.FILLED_DNA && stack.getOrCreateTagElement(ProjectNublar.MODID).contains("ContainedType", Constants.NBT.TAG_STRING);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(this.type.filled) {
            tooltip.add(new TranslationTextComponent(this.getDescriptionId(stack)));
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }


    @Override
    public ItemStack getOutItem(ItemStack stack) {
        return new ItemStack(ItemHandler.EMPTY_SYRINGE.get());
    }

    public enum Type {
        EMPTY(false), FILLED_DNA(true), FILLED_EMBRYO(true);

        private final boolean filled;

        Type(boolean filled) {
            this.filled = filled;
        }
    }
}
