package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.msg_parser.property.PropertiesStream;

public class Recipient extends MessageStorage {

    public static final String RECIPIENT_STORAGE_PREFIX = "__recip_version1.0";

    public Recipient(StorageDirectoryEntry directoryEntry) {
        super(directoryEntry);
    }

    @Override
    protected PropertiesStream createPropertiesStream(StreamDirectoryEntry stream) {
        return new PropertiesStream.SimplePropertiesStream(stream, 8);
    }
}
