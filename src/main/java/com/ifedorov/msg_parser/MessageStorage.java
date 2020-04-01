package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.msg_parser.property.PropertiesStream;

import java.util.stream.Stream;

public abstract class MessageStorage {

    public static final String VALUE_STREAM_PREFIX = "__substg1.0_";
    protected final StorageDirectoryEntry storage;

    public MessageStorage(StorageDirectoryEntry storage) {
        this.storage = storage;
    }

    public PropertiesStream propertiesStream() {
        return createPropertiesStream(storage.streams().filter(streamDirectoryEntry -> PropertiesStream.STREAM_NAME.equals(streamDirectoryEntry.getDirectoryEntryName())).findFirst().get());
    }

    public Stream<StreamDirectoryEntry> streams() {
        return storage.streams();
    }

    public StreamDirectoryEntry findByName(String name) {
        return streams().filter(streamDirectoryEntry -> streamDirectoryEntry.getDirectoryEntryName().equalsIgnoreCase(name)).findFirst().get();
    }

    protected abstract PropertiesStream createPropertiesStream(StreamDirectoryEntry stream);

    public StorageDirectoryEntry underlyingDirectoryEntry() {
        return storage;
    }

}
