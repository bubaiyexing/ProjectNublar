package net.dumbcode.projectnublar.server.entity;

import net.dumbcode.dumblibrary.server.ecs.component.EntityComponentType;
import net.dumbcode.dumblibrary.server.ecs.component.EntityComponentTypes;
import net.dumbcode.dumblibrary.server.ecs.component.RegisterStoragesEvent;
import net.dumbcode.dumblibrary.server.ecs.component.impl.CloseProximityAngryComponent;
import net.dumbcode.projectnublar.server.ProjectNublar;
import net.dumbcode.projectnublar.server.entity.component.impl.MultipartEntityComponent;
import net.dumbcode.projectnublar.server.entity.storage.CloseProximityBlacklistStorage;
import net.dumbcode.projectnublar.server.entity.storage.DinosaurMultipartStorage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectNublar.MODID)
public class EntityStorageOverrides {

    public static EntityComponentType.StorageOverride<MultipartEntityComponent, DinosaurMultipartStorage> DINOSAUR_MULTIPART;
    public static EntityComponentType.StorageOverride<CloseProximityAngryComponent, CloseProximityBlacklistStorage> CLOSE_PROXIMITY_BLACKLIST;

    @SubscribeEvent
    public static void onRegisterStorages(RegisterStoragesEvent event) {
        DINOSAUR_MULTIPART = event.register(ComponentHandler.MULTIPART.get(), "dinosaur_multipart", DinosaurMultipartStorage::new);
        CLOSE_PROXIMITY_BLACKLIST = event.register(EntityComponentTypes.CLOSE_PROXIMITY_ANGRY.get(), "close_proximity_blacklist", CloseProximityBlacklistStorage::new);
    }
}
