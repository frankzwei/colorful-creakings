package me.ultrusmods.colorfulcreakings.attachment;

import com.mojang.serialization.Codec;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import java.util.Objects;
import java.util.function.Supplier;

public final class CommonAttachment<T> implements AttachmentDataHelper<T> {

    private final Identifier id;
    private final boolean copyOnDeath;
    private final Codec<T> codec;
    private final Supplier<T> defaultValue;

    // Set by each loader
    private AttachmentDataHelper<T> helper = null;

    public CommonAttachment(Identifier id, boolean copyOnDeath, Codec<T> codec, Supplier<T> defaultValue) {
        this.id = id;
        this.copyOnDeath = copyOnDeath;
        this.codec = codec;
        this.defaultValue = defaultValue;
    }


    public void setHelper(AttachmentDataHelper<T> helper) {
        this.helper = helper;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static <T> CommonAttachmentBuilder<T> builder(Identifier id) {
        return new CommonAttachmentBuilder<>(id);
    }

    public Identifier id() {
        return id;
    }

    public boolean copyOnDeath() {
        return copyOnDeath;
    }

    public Codec<T> codec() {
        return codec;
    }

    public Supplier<T> defaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || !(obj instanceof CommonAttachment<?> that)) {
            return false;
        }

        return Objects.equals(this.id, that.id) &&
                this.copyOnDeath == that.copyOnDeath &&
                Objects.equals(this.codec, that.codec) &&
                Objects.equals(this.defaultValue, that.defaultValue);
    }

    @Override
    public String toString() {
        return "CommonAttachment[" +
                "id=" + id + ", " +
                "copyOnDeath=" + copyOnDeath + ", " +
                "codec=" + codec + ", " +
                "defaultValue=" + defaultValue + ']';
    }

    @Override
    public T getAttachment(LivingEntity entity) {
        return helper.getAttachment(entity);
    }

    @Override
    public void setAttachment(LivingEntity entity, T value) {
        helper.setAttachment(entity, value);
    }

    @Override
    public boolean hasAttachment(LivingEntity entity) {
        return helper.hasAttachment(entity);
    }

    @Override
    public void removeAttachment(LivingEntity entity) {
        helper.removeAttachment(entity);
    }

    public static class CommonAttachmentBuilder<T> {

        private final Identifier id;
        private boolean copyOnDeath = false;
        private Codec<T> codec = null;
        private Supplier<T> defaultValue = null;

        public CommonAttachmentBuilder(Identifier id) {
            this.id = id;
        }

        public CommonAttachmentBuilder<T> copyOnDeath(boolean copyOnDeath) {
            this.copyOnDeath = copyOnDeath;
            return this;
        }

        public CommonAttachmentBuilder<T> copyOnDeath() {
            return copyOnDeath(true);
        }

        public CommonAttachmentBuilder<T> codec(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public CommonAttachmentBuilder<T> defaultValue(Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public CommonAttachment<T> build() {
            return new CommonAttachment<>(id, copyOnDeath, codec, defaultValue);
        }
    }
}