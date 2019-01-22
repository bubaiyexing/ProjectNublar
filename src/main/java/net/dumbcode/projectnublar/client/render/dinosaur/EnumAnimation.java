package net.dumbcode.projectnublar.client.render.dinosaur;

import com.google.common.collect.Lists;
import net.dumbcode.dumblibrary.client.animation.objects.Animation;
import net.dumbcode.projectnublar.server.entity.ModelStage;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public enum EnumAnimation {
    IDLE(false, false),
    WALKING(false, false);

    private Animation<ModelStage> animation;

    EnumAnimation() {
        this(false, true);
    }

    EnumAnimation(boolean hold) {
        this(hold, true);
    }

    EnumAnimation(boolean hold, boolean useInertia) {
        this.animation = new Animation<>(hold, useInertia, this.name().toLowerCase());
    }

    public Animation<ModelStage> get() {
        return this.animation;
    }

    public static Collection<String> getNames() {
        List<String> list = Lists.newArrayList();
        for (EnumAnimation animation : values()) {
            list.add(animation.name().toLowerCase(Locale.ROOT));
        }
        return list;
    }

    public static EnumAnimation getAnimation(Animation<ModelStage> animation) {
        for (EnumAnimation animations : values()) {
            if (animation.equals(animations.animation)) {
                return animations;
            }
        }
        return EnumAnimation.IDLE;
    }

    public static Animation<ModelStage> fromName(String name) {
        EnumAnimation animation = IDLE;
        for (EnumAnimation animations : values()) {
            if (animations.name().equalsIgnoreCase(name)) {
                animation = animations;
            }
        }
        return animation.get();
    }

}
