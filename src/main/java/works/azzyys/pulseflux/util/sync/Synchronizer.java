package works.azzyys.pulseflux.util.sync;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.ApiStatus;
import works.azzyys.pulseflux.PulseFlux;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Synchronization manager, capable of automatically managing a lot of types, mainly primitives<p>
 * This class does <i>not</i> automatically synchronize most higher classes<p>
 * Only Enums, Strings, UUIDs, BlockPos, Identifiers, and other Synchronizers are supported.
 * <p><p>
 * THIS IS MEANT ONLY FOR S2C SYNCHRONIZATION. NOT THE OTHER WAY AROUND.
 */
public abstract class Synchronizer<C> {

    private final C target;
    private final Class<C> synchronizedClass;
    private final Map<String, FieldSignature> syncedFields = new HashMap<>();
    private final Set<String> enumerators = new HashSet<>();
    private final List<Synced> syncData = new ArrayList<>();
    private final String name;

    public Synchronizer(String name, C target, Class<C> synchronizedClass) {
        this.name = name;
        this.target = target;
        this.synchronizedClass = synchronizedClass;
        configure();
    }

    public void configure() {
        var fields = FieldUtils.getAllFieldsList(synchronizedClass)
                .stream()
                .filter(field -> field.canAccess(target))
                .filter(field -> field.getAnnotation(Synced.class) != null)
                .toList();

        fields.forEach(field -> {
            var type = field.getType();
            if (!validate(type))
                throw new AnnotationFormatError("[ILLEGAL ANNOTATION] | ONLY PRIMITIVES, SELECT CLASSES AND SYNCHRONIZERS CAN BE ANNOTATED AS SYNCED | [INVALIED FIELD] - " + field.getType() + " | [DECLARING CLASS - " + field.getDeclaringClass() + " | [SYNCHRONIZED OBJECT] + " + target.getClass());

            if (type.isEnum()) {
                try {
                    enumerators.add(((Enum<?>) field.get(target)).getDeclaringClass().getCanonicalName());
                } catch (IllegalAccessException e) {
                    PulseFlux.LOG.error("WHAT. HOW.", e);
                }
            }

            var syncInfo = field.getAnnotation(Synced.class);
            syncedFields.put(syncInfo.value(), new FieldSignature(field, Type.of(type)));
            syncData.add(syncInfo);
        });
    }

    public boolean validate(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.isEnum() ||
                clazz == String.class ||
                clazz.isAssignableFrom(BlockPos.class) ||
                clazz == UUID.class ||
                clazz == Identifier.class ||
                clazz.isAssignableFrom(Synchronizer.class);
    }

    @ApiStatus.OverrideOnly
    abstract void write(NbtCompound tag);

    @ApiStatus.OverrideOnly
    abstract void read(NbtCompound tag);


    @ApiStatus.NonExtendable
    public final void writeInternal(NbtCompound tag) {
        if (target == null)
            return;

        for (Synced syncInfo : syncData) {
            var name = syncInfo.value();
            var signature = syncedFields.get(name);
            var field = signature.field;

            tag.putString(name + ".syncName", this.name);
            tag.putString(name + ".syncSanity", synchronizedClass.getCanonicalName());

            try {
                signature.type.write(this.name + "." + name, field, tag, target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to synchronize field [" + field.getName() + "] in [" + target.getClass().getName() + "] while running, unable to access - unable to access?! whar????");
            }
        }
        write(tag);
    }

    @ApiStatus.NonExtendable
    public final void readInternal(NbtCompound tag) {
        if (target == null)
            return;

        var syncName = tag.getString(name + ".syncName");
        var syncSanity = tag.getString(name + "syncSanity");
        if (!syncName.equals(name) || !syncSanity.equals(synchronizedClass.getCanonicalName())) {
            return;
        }

        for (Synced syncInfo : syncData) {
            var name = syncInfo.value();
            var signature = syncedFields.get(name);
            var field = signature.field;


        }

        read(tag);
    }

    public record FieldSignature(Field field, Type type) {}

    private enum Type implements Processor {
        BOOLEAN {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putBoolean(name, field.getBoolean(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setBoolean(synchronizer.target, tag.getBoolean(name));
            }
        },
        BYTE {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putByte(name, field.getByte(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setByte(synchronizer.target, tag.getByte(name));
            }
        },
        SHORT {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putShort(name, field.getShort(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setShort(synchronizer.target, tag.getShort(name));
            }
        },
        INT {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putInt(name, field.getInt(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setInt(synchronizer.target, tag.getInt(name));
            }
        },
        LONG {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putLong(name, field.getLong(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setLong(synchronizer.target, tag.getLong(name));
            }
        },
        FLOAT {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putFloat(name, field.getFloat(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setFloat(synchronizer.target, tag.getFloat(name));
            }
        },
        DOUBLE {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putDouble(name, field.getDouble(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name))
                    field.setDouble(synchronizer.target, tag.getDouble(name));
            }
        },
        CHAR {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putString(name, String.valueOf(field.getChar(target)));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name)) {
                    var value = tag.getString(name);
                    if (value.length() == 1)
                        field.setChar(synchronizer.target, value.charAt(0));
                }
            }
        },
        STRING {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putString(name, (String) field.get(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType() == String.class)
                    field.set(synchronizer.target, tag.getString(name));
            }
        },
        IDENTIFIER {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putString(name, field.get(target).toString());
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType() == Identifier.class)
                    field.set(synchronizer.target, Identifier.tryParse(tag.getString(name)));
            }
        },
        BLOCK {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                var pos = (BlockPos) field.get(target);
                tag.putLong(name, pos.asLong());
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType().isAssignableFrom(BlockPos.class))
                    field.set(synchronizer.target, BlockPos.fromLong(tag.getLong(name)));
            }
        },
        UUID {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putUuid(name, (UUID) field.get(target));
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType() == UUID.class)
                    field.set(synchronizer.target, tag.getUuid(name));
            }
        },
        ENUM {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                tag.putString(name + "EnumClass", field.getType().getDeclaringClass().getCanonicalName());
                tag.putInt(name, ((Enum<?>) field.get(target)).ordinal());
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType().isAssignableFrom(Enum.class)) {
                    var enumClass = tag.getString(name + "EnumClass");

                    if (!synchronizer.enumerators.contains(enumClass))
                        return;

                    try {
                        var enumerator = Class.forName(enumClass);
                        var enumValue = enumerator.getEnumConstants()[tag.getInt(name)];
                        field.set(synchronizer.target, enumValue);
                    } catch (ClassNotFoundException e) {
                        PulseFlux.LOG.fatal("Enumerator class not found while Synchronizing. Potentially suspicious payload - things are dire indeed.", e);
                    }
                }
            }
        },
        RECURSIVE {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                var synchronizer = (Synchronizer<?>) field.get(target);
                var innerTag = new NbtCompound();
                synchronizer.writeInternal(innerTag);
                tag.put(synchronizer.name, innerTag);
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                if (tag.contains(name) && field.getType().isAssignableFrom(Synchronizer.class))
                    ((Synchronizer<?>)field.get(synchronizer.target)).readInternal(tag.getCompound(name));
            }
        },
        NOOP {
            @Override
            public void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException {
                //do nothing
            }

            @Override
            public void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException {
                //do nothing
            }
        };

        static Type of(Class<?> type) {

            if (type.isEnum())
                return ENUM;

            if (type == UUID.class)
                return UUID;

            if (type == Identifier.class)
                return IDENTIFIER;

            if (type == BlockPos.class)
                return BLOCK;

            if (type == Synchronizer.class)
                return RECURSIVE;

            if (type == String.class)
                return STRING;

            if (!type.isPrimitive())
                return NOOP;

            if (type == Boolean.class)
                return BOOLEAN;

            if (type == Byte.class)
                return BYTE;

            if (type == Short.class)
                return SHORT;

            if (type == Integer.class)
                return INT;

            if (type == Long.class)
                return LONG;

            if (type == Float.class)
                return FLOAT;

            if (type == Double.class)
                return DOUBLE;

            if (type == Character.class)
                return CHAR;

            return NOOP;
        }
    }

    private interface Processor {
        void write(String name, Field field, NbtCompound tag, Object target) throws IllegalAccessException;

        void read(String name, Field field, NbtCompound tag, Synchronizer<?> synchronizer) throws IllegalAccessException;
    }
}
