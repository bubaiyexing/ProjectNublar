package net.dumbcode.projectnublar.server.item;

import net.dumbcode.projectnublar.server.ProjectNublar;
import net.dumbcode.projectnublar.server.block.IItemBlock;
import net.dumbcode.projectnublar.server.dinosaur.Dinosaur;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.dumbcode.projectnublar.server.ProjectNublar.TAB;

@Mod.EventBusSubscriber(modid = ProjectNublar.MODID)
public final class ItemHandler {

    public static Item EMPTY_TEST_TUBE = new Item().setMaxStackSize(1);
    public static Item FILTER = new Item();
    public static Item AMBER = new Item();
    public static Item EMPTY_CHIP = new Item();
    public static Item EMPTY_SYRINGE = new Item();
    public static Item FILLED_SYRINGE = new Item();


    public static final Map<Dinosaur, ItemDinosaurMeat> RAW_MEAT_ITEMS = new HashMap<>();
    public static final Map<Dinosaur, ItemDinosaurMeat> COOKED_MEAT_ITEMS = new HashMap<>();
    public static final Map<Dinosaur, DinosaurSpawnEgg> SPAWN_EGG_ITEMS = new HashMap<>();
    public static final Map<Dinosaur, BasicDinosaurItem> TEST_TUBES_GENETIC_MATERIAL = new HashMap<>();
    public static final Map<Dinosaur, BasicDinosaurItem> STORAGE_CHIP = new HashMap<>();

    public static final Map<Dinosaur, Map<String, FossilItem>> FOSSIL_ITEMS = new HashMap<>();

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {

        event.getRegistry().registerAll(
                EMPTY_TEST_TUBE.setRegistryName("test_tube").setUnlocalizedName("test_tube").setCreativeTab(TAB),
                FILTER.setRegistryName("filter").setUnlocalizedName("filter").setCreativeTab(TAB),
                AMBER.setRegistryName("amber").setUnlocalizedName("amber").setCreativeTab(TAB),
                EMPTY_CHIP.setRegistryName("empty_chip").setUnlocalizedName("empty_chip").setCreativeTab(TAB).setMaxStackSize(1),
                EMPTY_SYRINGE.setRegistryName("empty_syringe").setUnlocalizedName("empty_syringe").setCreativeTab(TAB).setMaxStackSize(1),
                FILLED_SYRINGE.setRegistryName("filled_syringe").setUnlocalizedName("filled_syringe").setCreativeTab(TAB).setMaxStackSize(1)
        );

        Function<Item, Item> tab = item -> item.setCreativeTab(TAB);

        populateMap(event, RAW_MEAT_ITEMS, "%s_meat_dinosaur_raw", d -> new ItemDinosaurMeat(d, ItemDinosaurMeat.CookState.RAW), tab);
        populateMap(event, COOKED_MEAT_ITEMS, "%s_meat_dinosaur_cooked", d -> new ItemDinosaurMeat(d, ItemDinosaurMeat.CookState.COOKED), tab);
        populateMap(event, SPAWN_EGG_ITEMS, "%s_spawn_egg", DinosaurSpawnEgg::new, tab);
        populateMap(event, TEST_TUBES_GENETIC_MATERIAL, "%s_genetic_material_test_tube", BasicDinosaurItem::new, tab.andThen(i -> i.setMaxStackSize(1)));
        populateMap(event, STORAGE_CHIP, "%s_storage_chip", BasicDinosaurItem::new, tab);

        populateNestedMap(event, FOSSIL_ITEMS, dino -> dino.getSkeletalInformation().getIndividualBones(), FossilItem::new, "fossil_%s_%s");
        for (Block block : ForgeRegistries.BLOCKS) {
            if(block instanceof IItemBlock) {
                event.getRegistry().register(((IItemBlock)block).createItem()
                        .setRegistryName(block.getRegistryName())
                        .setUnlocalizedName(block.getUnlocalizedName().substring("tile.".length()))
                        .setCreativeTab(block.getCreativeTabToDisplayOn()));
            }
        }

        //TODO: iterate through all items, and check the cast
        registerOreNames(RAW_MEAT_ITEMS);
        registerOreNames(COOKED_MEAT_ITEMS);
        FOSSIL_ITEMS.values().forEach(ItemHandler::registerOreNames);
    }

    /**
     * Needs to be called **after** registering all the items
     * @param items
     */
    private static void registerOreNames(Map<?, ? extends Item> items) {
        items.values().stream()
                .filter(ItemWithOreName.class::isInstance)
                .map(ItemWithOreName.class::cast)
                .forEach(ItemWithOreName::registerOreNames);
    }

    private static <T extends Item> void populateMap(RegistryEvent.Register<Item> event, Map<Dinosaur, T> itemMap, String dinosaurRegname, Function<Dinosaur, T> supplier) {
        populateMap(event, itemMap, dinosaurRegname, supplier, null);
    }

    private static <T extends Item> void populateMap(RegistryEvent.Register<Item> event, Map<Dinosaur, T> itemMap, String dinosaurRegname, Function<Dinosaur, T> supplier, @Nullable Function<Item, Item> initializer) {
        for (Dinosaur dinosaur : ProjectNublar.DINOSAUR_REGISTRY) {

            if(dinosaur != Dinosaur.MISSING) {
                T item = supplier.apply(dinosaur);
                String name = String.format(dinosaurRegname, dinosaur.getFormattedName());
                item.setRegistryName(new ResourceLocation(ProjectNublar.MODID, name));
                item.setUnlocalizedName(name);
                if(initializer != null) {
                    item = runInitilizer(item, initializer);
                }
                itemMap.put(dinosaur, item);
                event.getRegistry().register(item);
            }
        }
    }

    private static <T extends Item, S> void populateNestedMap(RegistryEvent.Register<Item> event, Map<Dinosaur, Map<S, T>> itemMap, Function<Dinosaur, Collection<S>> getterFunction, BiFunction<Dinosaur, S, T> creationFunc, String dinosaurRegname) {
        populateNestedMap(event, itemMap, getterFunction, Object::toString, creationFunc, dinosaurRegname, null);
    }

    private static <T extends Item, S> void populateNestedMap(RegistryEvent.Register<Item> event, Map<Dinosaur, Map<S, T>> itemMap, Function<Dinosaur, Collection<S>> getterFunction, Function<S, String> toStringFunction, BiFunction<Dinosaur, S, T> creationFunc, String dinosaurRegname, @Nullable Function<Item, Item> initializer) {
        for (Dinosaur dinosaur : ProjectNublar.DINOSAUR_REGISTRY) {
            if(dinosaur != Dinosaur.MISSING) {
                for (S s : getterFunction.apply(dinosaur)) {
                    T item = creationFunc.apply(dinosaur, s);
                    String name = String.format(dinosaurRegname, dinosaur.getFormattedName(), toStringFunction.apply(s));
                    item.setRegistryName(new ResourceLocation(ProjectNublar.MODID, name));
                    item.setUnlocalizedName(name);
                    if(initializer != null) {
                        item = runInitilizer(item, initializer);
                    }
                    itemMap.computeIfAbsent(dinosaur, d -> new HashMap<>()).put(s, item);
                    event.getRegistry().register(item);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> T runInitilizer(T item, Function<Item, Item> initializer) {
        Item initializedItem = initializer.apply(item);
        if(!item.getClass().isInstance(initializedItem)) {
            throw new RuntimeException("Initialized class did not give same as (of subclass of) initial class. Initial: " + item.getClass() + " Initialized: " + initializedItem.getClass());
        }
        return (T) initializedItem;
    }
}
