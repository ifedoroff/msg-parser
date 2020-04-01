package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.msg_parser.property.KnownProperties;
import com.ifedorov.msg_parser.property.PropertiesStream;
import com.ifedorov.msg_parser.property.PropertyType;

import java.io.IOException;
import java.io.OutputStream;

public class Attachment extends MessageStorage {
    public Attachment(StorageDirectoryEntry directoryEntry) {
        super(directoryEntry);
    }

    @Override
    protected PropertiesStream createPropertiesStream(StreamDirectoryEntry stream) {
        return new PropertiesStream.SimplePropertiesStream(stream, 8);
    }

    public void writeTo(OutputStream os) {
        StreamDirectoryEntry contentStream = storage.<StreamDirectoryEntry>findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equalsIgnoreCase(Msg.VALUE_STREAM_PREFIX + KnownProperties.PidTagAttachDataBinary.toString()));
        contentStream.copyTo(os);
    }

    public byte[] content() {
        return (byte[]) PropertyType.forId(KnownProperties.PidTagAttachDataBinary.propertyType).resolveValue(this, KnownProperties.PidTagAttachDataBinary);
    }

}
