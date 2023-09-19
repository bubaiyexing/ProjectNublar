package net.dumbcode.projectnublar.server.fossil.blockitem;

import net.dumbcode.projectnublar.server.fossil.base.Fossil;
import net.dumbcode.projectnublar.server.fossil.base.StoneType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class FossilBlock extends Block {
    public Fossil fossil;
    public StoneType stone;
    public FossilBlock(Properties properties, Fossil fossil, StoneType stone) {
        super(properties);
        this.fossil = fossil;
        this.stone = stone;
    }

    @Override
    public IFormattableTextComponent getName() {
        return new TranslationTextComponent("projectnublar.fossil", WordUtils.capitalizeFully(stone.name), WordUtils.capitalizeFully(fossil.name));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        return super.getDrops(pState, pBuilder);
    }

    public Fossil getFossil() {
        return fossil;
    }

    public void setFossil(Fossil fossil) {
        this.fossil = fossil;
    }

    public StoneType getStone() {
        return stone;
    }

    public void setStone(StoneType stone) {
        this.stone = stone;
    }
}
