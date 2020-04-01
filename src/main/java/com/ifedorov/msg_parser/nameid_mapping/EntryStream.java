package com.ifedorov.msg_parser.nameid_mapping;

import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.UUID;
import java.util.function.Consumer;

public class EntryStream {

    public static final String STREAM_NAME = "__substg1.0_00030102";
    private StreamDirectoryEntry stream;

    public EntryStream(StreamDirectoryEntry _this) {
        this.stream = _this;
    }

    public Entry getEntry(int index) {
        return new Entry(stream.read(index * 8, (index + 1) * 8));
    }

    public void eachEntry(Consumer<Entry> consumer) {
        for (int i = 0; i < stream.getStreamSize() / 8; i++) {
            consumer.accept(getEntry(i));
        }
    }

    public static class Entry {
        private final byte[] bytes;
        private final long idOrOffset;
        private final IndexAndKindInformation indexAndKindInformation;

        public Entry(byte[] bytes) {
            this.bytes = bytes;
            idOrOffset = Utils.toLongLE(ArrayUtils.subarray(bytes, 0, 4));
            indexAndKindInformation = new IndexAndKindInformation(ArrayUtils.subarray(bytes, 4, 8));
        }

        public String getPropertyName(StringStream stringStream) {
            return stringStream.getPropertyNameAt((int) idOrOffset);
        }

        public long getPropertyNameIdentifierOrOffsetOrChecksum() {
            return idOrOffset;
        }

        public UUID getGuid(GUIDStream guidStream) {
            return guidStream.getGuidAt(indexAndKindInformation.getGuidIndex());
        }

        public int getPropertyIndex() {
            return indexAndKindInformation.getPropertyIndex();
        }

        public boolean isString() {
            return indexAndKindInformation.getPropertyKind() == IndexAndKindInformation.PropertyKind.STRING;
        }

        public boolean isNumeric() {
            return indexAndKindInformation.getPropertyKind() == IndexAndKindInformation.PropertyKind.NUMERIC;
        }
    }
}
