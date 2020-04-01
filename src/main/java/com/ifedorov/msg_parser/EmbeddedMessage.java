package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.msg_parser.property.PropertiesStream;

public class EmbeddedMessage extends MessageStorage {

    public final static String INTERNAL_STORAGE_NAME = "__substg1.0_3701000D";
    public final static int ATTACH_METHOD_CUSTOM = 0x0006;

    public EmbeddedMessage(StorageDirectoryEntry directoryEntry) {
        super(directoryEntry);
    }

    @Override
    protected PropertiesStream createPropertiesStream(StreamDirectoryEntry stream) {
        return new PropertiesStream.SimplePropertiesStream(stream, 24);
    }

    public InternalStorage internalStorage() {
        return new InternalStorage(storage.findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equals(INTERNAL_STORAGE_NAME)));
    }

    public static class InternalStorage extends AbstractMessage {

        public InternalStorage(StorageDirectoryEntry directoryEntry) {
            super(directoryEntry);
        }

        @Override
        protected PropertiesStream createPropertiesStream(StreamDirectoryEntry stream) {
            return new PropertiesStream.SimplePropertiesStream(stream, 24);
        }
    }
}
