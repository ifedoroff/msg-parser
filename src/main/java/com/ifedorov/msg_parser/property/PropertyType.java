package com.ifedorov.msg_parser.property;

import com.google.common.collect.Maps;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import com.ifedorov.msg_parser.Msg;
import com.ifedorov.msg_parser.MessageStorage;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class PropertyType<T> {

    public final int id;
    public final String name;

    public PropertyType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract T resolveValue(MessageStorage container, PropertyTag propertyTag);

    public static abstract class PropertyMultipleType<U> extends PropertyType<List<U>> {
        public PropertyMultipleType(int id, String name) {
            super(id, name);
        }
    }

    public static abstract class PropertyMultipleFixedLengthType<U> extends PropertyMultipleType<U> {

        private final int valueLength;

        public PropertyMultipleFixedLengthType(int id, String name, int valueLength) {
            super(id, name);
            this.valueLength = valueLength;
        }

        @Override
        public List<U> resolveValue(MessageStorage container, PropertyTag propertyTag) {
            try {
                StreamDirectoryEntry stream = container.findByName(Msg.VALUE_STREAM_PREFIX + propertyTag.toString());
                return IntStream.range(0, stream.getStreamSize() / valueLength).mapToObj(i -> resolveSingleValue(stream.read(i * valueLength, (i + 1) * valueLength))).collect(Collectors.toList());
            } catch (RuntimeException e) {
                throw e;
            }
        }

        protected abstract U resolveSingleValue(byte[] bytes);
    }

    public static class UnsupportedPropertyMultipleType<U> extends PropertyMultipleType<U> {

        public UnsupportedPropertyMultipleType(int id, String name) {
            super(id, name);
        }

        @Override
        public List<U> resolveValue(MessageStorage container, PropertyTag propertyTag) {
            throw new UnsupportedOperationException();
        }
    }

    public static abstract class PropertyMultipleVariableLengthType<U> extends PropertyMultipleType<U> {

        public PropertyMultipleVariableLengthType(int id, String name) {
            super(id, name);
        }

        @Override
        public List<U> resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return container.streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().startsWith(Msg.VALUE_STREAM_PREFIX + propertyTag.toString() + "-"))
                    .map(StreamDirectoryEntry::getStreamData).map(this::resolveSingleValue).collect(Collectors.toList());
        }

        protected abstract U resolveSingleValue(byte[] bytes);
    }

    public static PropertyType<Integer> PtypInteger16 = new PropertyType<Integer>(0x0002, "PtypInteger16") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toInt(
                    ArrayUtils.subarray(
                            container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data(), 0, 4
                    )
            );
        }
    };
    public static PropertyType<Integer> PtypInteger32 = new PropertyType<Integer>(0x0003, "PtypInteger32") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toInt(
                    ArrayUtils.subarray(
                            container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data(), 0, 4
                    )
            );
        }
    };
    public static PropertyType<Float> PtypFloating32 = new PropertyType<Float>(0x0004, "PtypFloating32") {
        @Override
        public Float resolveValue(MessageStorage container, PropertyTag propertyTag) {
            throw new UnsupportedOperationException();
        }
    };
    public static PropertyType<Double> PtypFloating64 = new PropertyType<Double>(0x0005, "PtypFloating64") {
        @Override
        public Double resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toDoubleLE(
                    ArrayUtils.subarray(
                            container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data(), 0, 8
                    )
            );
        }
    };
    public static PropertyType<Boolean> PtypBoolean = new PropertyType<Boolean>(0x000B, "PtypBoolean") {
        @Override
        public Boolean resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toBoolean(
                container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data()[0]
            );
        }
    };
    public static PropertyType<Integer> PtypCurrency = new PropertyType<Integer>(0x0006, "PtypCurrency") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            throw new UnsupportedOperationException();
        }
    };
    public static PropertyType<Integer> PtypFloatingTime = new PropertyType<Integer>(0x0007, "PtypFloatingTime") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            throw new UnsupportedOperationException();
        }
    };
    public static PropertyType<Date> PtypTime = new PropertyType<Date>(0x0040, "PtypTime") {
        @Override
        public Date resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toTime(container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data());
        }
    };
    public static PropertyType<Long> PtypInteger64 = new PropertyType<Long>(0x0014, "PtypInteger64") {
        @Override
        public Long resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toLongLE(container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data());
        }
    };
    public static PropertyType<Integer> PtypErrorCode = new PropertyType<Integer>(0x000A, "PtypErrorCode") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            return Utils.toInt(
                    ArrayUtils.subarray(
                            container.propertiesStream().properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(propertyTag)).findFirst().get().data(),
                            0,
                            4
                    )
            );
        }
    };
    public static PropertyType<String> PtypString = new PropertyType<String>(0x001F, "PtypString") {
        @Override
        public String resolveValue(MessageStorage container, PropertyTag propertyTag) {
            PropertiesStream.PropertyInfo property = container.propertiesStream().getProperty(propertyTag);
            int size = Utils.toInt(ArrayUtils.subarray(property.data(), 0,4)) - 2;
            StreamDirectoryEntry valueStream = container.streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().equalsIgnoreCase(Msg.VALUE_STREAM_PREFIX + propertyTag.toString())).findFirst().get();
            return Utils.toUTF8WithNoTrailingZeros(valueStream.read(0, size));
        }
    };
    public static PropertyType<byte[]> PtypBinary = new PropertyType<byte[]>(0x0102, "PtypBinary") {
        @Override
        public byte[] resolveValue(MessageStorage container, PropertyTag propertyTag) {
            PropertiesStream.PropertyInfo property = container.propertiesStream().getProperty(propertyTag);
            int size = Utils.toInt(ArrayUtils.subarray(property.data(), 0,4));
            StreamDirectoryEntry valueStream = container.streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().equalsIgnoreCase(Msg.VALUE_STREAM_PREFIX + propertyTag.toString())).findFirst().get();
            return valueStream.read(0, size);
        }
    };
    public static PropertyType<String> PtypString8 = new PropertyType<String>(0x001E, "PtypString8")  {
        @Override
        public String resolveValue(MessageStorage container, PropertyTag propertyTag) {
            PropertiesStream.PropertyInfo property = container.propertiesStream().getProperty(propertyTag);
            int size = Utils.toInt(ArrayUtils.subarray(property.data(), 0,4)) - 1;
            StreamDirectoryEntry valueStream = container.streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().equalsIgnoreCase(Msg.VALUE_STREAM_PREFIX + propertyTag.toString())).findFirst().get();
            return Utils.toUTF8WithNoTrailingZeros(valueStream.read(0, size));
        }
    };
    public static PropertyType<UUID> PtypGuid = new PropertyType<UUID>(0x0048, "PtypGuid") {
        @Override
        public UUID resolveValue(MessageStorage container, PropertyTag propertyTag) {
            String streamName = MessageStorage.VALUE_STREAM_PREFIX + propertyTag.toString();
            return Utils.uuidFromByteLE(container.streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().equalsIgnoreCase(streamName)).findFirst().get().read(0, 16));
        }
    };
    public static PropertyType<Integer> PtypObject = new PropertyType<Integer>(0x000D, "PtypObject") {
        @Override
        public Integer resolveValue(MessageStorage container, PropertyTag propertyTag) {
            throw new UnsupportedOperationException();
        }
    };
    public static PropertyMultipleFixedLengthType<Integer> PtypMultipleInteger16 = new PropertyMultipleFixedLengthType<Integer>(0x1002, "PtypMultipleInteger16", 2) {
        @Override
        protected Integer resolveSingleValue(byte[] bytes) {
            return Utils.toInt(bytes);
        }
    };
    public static PropertyMultipleFixedLengthType<Integer> PtypMultipleInteger32 = new PropertyMultipleFixedLengthType<Integer>(0x1003, "PtypMultipleInteger32", 4) {
        @Override
        protected Integer resolveSingleValue(byte[] bytes) {
            return Utils.toInt(bytes);
        }
    };
    public static PropertyMultipleType<Integer> PtypMultipleFloating32 = new UnsupportedPropertyMultipleType<Integer>(0x1004, "PtypMultipleFloating32");
    public static PropertyMultipleFixedLengthType<Double> PtypMultipleFloating64 = new PropertyMultipleFixedLengthType<Double>(0x1005, "PtypMultipleFloating64", 4) {
        @Override
        protected Double resolveSingleValue(byte[] bytes) {
            return Utils.toDoubleLE(bytes);
        }
    };
    public static PropertyMultipleFixedLengthType<Boolean> PtypMultipleBoolean = new PropertyMultipleFixedLengthType<Boolean>(0x100B, "PtypMultipleBoolean", 8) {
        @Override
        protected Boolean resolveSingleValue(byte[] bytes) {
            return Utils.toBoolean(bytes[0]);
        }
    };
    public static PropertyMultipleType<Integer> PtypMultipleCurrency = new UnsupportedPropertyMultipleType<Integer>(0x1006, "PtypMultipleCurrency");
    public static PropertyMultipleType<Integer> PtypMultipleFloatingTime = new UnsupportedPropertyMultipleType<Integer>(0x1007, "PtypMultipleFloatingTime");
    public static PropertyMultipleFixedLengthType<Date> PtypMultipleTime = new PropertyMultipleFixedLengthType<Date>(0x1040, "PtypMultipleTime", 8) {
        @Override
        protected Date resolveSingleValue(byte[] bytes) {
            return Utils.toTime(bytes);
        }
    };
    public static PropertyMultipleFixedLengthType<Long> PtypMultipleInteger64 = new PropertyMultipleFixedLengthType<Long>(0x1014, "PtypMultipleInteger64", 8) {

        @Override
        protected Long resolveSingleValue(byte[] bytes) {
            return Utils.toLongLE(bytes);
        }
    };
    public static PropertyMultipleVariableLengthType<String> PtypMultipleString = new PropertyMultipleVariableLengthType<String>(0x101F, "PtypMultipleString") {
        @Override
        protected String resolveSingleValue(byte[] bytes) {
            return Utils.toUTF8WithNoTrailingZeros(bytes);
        }
    };
    public static PropertyMultipleVariableLengthType<byte[]> PtypMultipleBinary = new PropertyMultipleVariableLengthType<byte[]>(0x1102, "PtypMultipleBinary") {
        @Override
        protected byte[] resolveSingleValue(byte[] bytes) {
            return bytes;
        }
    };
    public static PropertyMultipleVariableLengthType<String> PtypMultipleString8 = new PropertyMultipleVariableLengthType<String>(0x101E, "PtypMultipleString8") {
        @Override
        protected String resolveSingleValue(byte[] bytes) {
            return Utils.toUTF8WithNoTrailingZeros(bytes);
        }
    };
    public static PropertyMultipleFixedLengthType<UUID> PtypMultipleGuid = new PropertyMultipleFixedLengthType<UUID>(0x1048, "PtypMultipleGuid", 16) {
        @Override
        protected UUID resolveSingleValue(byte[] bytes) {
            return Utils.uuidFromByteLE(bytes);
        }
    };

    private static final Map<Integer, PropertyType> PROPERTY_TYPES = Maps.newHashMap();
    public static <U extends PropertyType> U forId(int id) {
        return (U)PROPERTY_TYPES.get(id);
    }
    static {
        PROPERTY_TYPES.put(0x0002, PtypInteger16);
        PROPERTY_TYPES.put(0x0003, PtypInteger32);
        PROPERTY_TYPES.put(0x0004, PtypFloating32);
        PROPERTY_TYPES.put(0x0005, PtypFloating64);
        PROPERTY_TYPES.put(0x000B, PtypBoolean);
        PROPERTY_TYPES.put(0x0006, PtypCurrency);
        PROPERTY_TYPES.put(0x0007, PtypFloatingTime);
        PROPERTY_TYPES.put(0x0040, PtypTime);
        PROPERTY_TYPES.put(0x0014, PtypInteger64);
        PROPERTY_TYPES.put(0x000A, PtypErrorCode);
        PROPERTY_TYPES.put(0x001F, PtypString);
        PROPERTY_TYPES.put(0x0102, PtypBinary);
        PROPERTY_TYPES.put(0x001E, PtypString8);
        PROPERTY_TYPES.put(0x0048, PtypGuid);
        PROPERTY_TYPES.put(0x000D, PtypObject);
        PROPERTY_TYPES.put(0x1002, PtypMultipleInteger16);
        PROPERTY_TYPES.put(0x1003, PtypMultipleInteger32);
        PROPERTY_TYPES.put(0x1004, PtypMultipleFloating32);
        PROPERTY_TYPES.put(0x1005, PtypMultipleFloating64);
        PROPERTY_TYPES.put(0x100B, PtypMultipleBoolean);
        PROPERTY_TYPES.put(0x1006, PtypMultipleCurrency);
        PROPERTY_TYPES.put(0x1007, PtypMultipleFloatingTime);
        PROPERTY_TYPES.put(0x1040, PtypMultipleTime);
        PROPERTY_TYPES.put(0x1014, PtypMultipleInteger64);
        PROPERTY_TYPES.put(0x101F, PtypMultipleString);
        PROPERTY_TYPES.put(0x1102, PtypMultipleBinary);
        PROPERTY_TYPES.put(0x101E, PtypMultipleString8);
        PROPERTY_TYPES.put(0x1048, PtypMultipleGuid);
    }


}
