package net.dumbcode.projectnublar.server.fossil.blockitem;

import net.dumbcode.projectnublar.server.fossil.base.Fossil;
import net.dumbcode.projectnublar.server.fossil.base.StoneType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.text.WordUtils;

//TODO: tint the item model fossil overlay
public class FossilItem extends Item {
    Fossil fossil;
    public FossilItem(Properties properties, Fossil fossil) {
        super(properties);
        this.fossil = fossil;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        return new TranslationTextComponent("projectnublar.fossil", "", WordUtils.capitalizeFully(fossil.name));
    }

    public Fossil getFossil() {
        return fossil;
    }

    public void setFossil(Fossil fossil) {
        this.fossil = fossil;
    }
}
