package com.ifedorov.msg_parser.property;

import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import com.ifedorov.msg_parser.Msg;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class PropertiesStream {

    public static class SimplePropertiesStream extends PropertiesStream {
        public SimplePropertiesStream(StreamDirectoryEntry stream, int headerLength) {
            super(stream, headerLength);
        }
    }

    public static final String STREAM_NAME = "__properties_version1.0";
    protected final StreamDirectoryEntry stream;
    private int headerLength;

    public PropertiesStream(StreamDirectoryEntry stream, int headerLength) {
        this.stream = stream;
        this.headerLength = headerLength;
    }

    public Stream<Msg.PropertyStream.PropertyInfo> properties() {
        byte[] data = stream.read(headerLength, stream.getStreamSize());
        return IntStream.range(0, data.length / 16).mapToObj(pos -> new Msg.PropertyStream.PropertyInfo(ArrayUtils.subarray(data, pos * 16, (pos + 1) * 16)));
    }

    public PropertyInfo getProperty(PropertyTag tag) {
        return properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(tag)).findFirst().get();
    }

    public Optional<PropertyInfo> findProperty(PropertyTag tag) {
        return properties().filter(propertyInfo -> propertyInfo.propertyTag().equals(tag)).findFirst();
    }

    public class PropertyInfo {

        private byte[] bytes;

        public PropertyInfo(byte[] bytes) {
            this.bytes = bytes;
        }

        public int propertyId() {
            return Utils.toInt(ArrayUtils.subarray(bytes, 2, 4));
        }

        public int propertyType() {
            return Utils.toInt(ArrayUtils.subarray(bytes, 0, 2));
        }

        public PropertyTag propertyTag() {
            return new PropertyTag(propertyId(), propertyType());
        }

        public int flags() {
            return Utils.toInt(ArrayUtils.subarray(bytes, 4, 8));
        }

        public byte[] data() {
            return ArrayUtils.subarray(bytes, 8, 16);
        }
    }
}
