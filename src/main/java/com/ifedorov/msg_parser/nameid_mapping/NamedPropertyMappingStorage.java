package com.ifedorov.msg_parser.nameid_mapping;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.primitives.UnsignedInteger;
import com.ifedorov.cfbf.DirectoryEntry;
import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import com.ifedorov.msg_parser.CRC;
import com.ifedorov.msg_parser.property.PropertyNameLID;
import com.ifedorov.msg_parser.property.PropertyNameString;
import com.ifedorov.msg_parser.property.PropertySet;
import com.ifedorov.msg_parser.property.PropertyTag;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.CRC32;

public class NamedPropertyMappingStorage {

    public static final String STORAGE_NAME = "__nameid_version1.0";
    public static final int NAMED_PROPERTY_ID_BASE = 0x8000; //32768
    private StorageDirectoryEntry storage;


    public NamedPropertyMappingStorage(StorageDirectoryEntry storage) {
        this.storage = storage;
    }

    public String getPropertyNameByPropertyTag(PropertyTag propertyTag) {
        EntryStream.Entry entry = entryStream().getEntry(propertyTag.propertyId - NAMED_PROPERTY_ID_BASE);
        if(entry.isNumeric()) {
            return String.valueOf(entry.getPropertyNameIdentifierOrOffsetOrChecksum());
        } else {
            return entry.getPropertyName(stringStream());
        }
    }

    public PropertyTag getPropertyTagByPropertyName(PropertyNameString propertyNameString) {
        long crcOrPropertyIdentifier = CRC.crc32(propertyNameString.propertyName.getBytes(StandardCharsets.UTF_16LE));
        PropertyTag propertyTag = getPropertyTagForNamedProperty(crcOrPropertyIdentifier, propertyNameString.propertySet.id, propertyNameString.propertyType);
        if(propertyTag == null) {
            throw new IllegalArgumentException("Unable to find property: " + propertyNameString.propertyName);
        } else {
            return propertyTag;
        }
    }

    public PropertyTag getPropertyTagByPropertyNameLID(PropertyNameLID propertyNameLID) {
        PropertyTag propertyTag = getPropertyTagForNamedProperty(propertyNameLID.propertyLID, propertyNameLID.propertySet.id, propertyNameLID.propertyType);
        if(propertyTag == null) {
            throw new IllegalArgumentException("Unable to find property: " + propertyNameLID.propertyLID);
        } else {
            return propertyTag;
        }
    }

    private PropertyTag getPropertyTagForNamedProperty(long crcOrPropertyIdentifier, UUID propertySetGuid, int propertyType) {
        int guidIndex = guidStream().getIndexFor(propertySetGuid);
        long streamId = 0x1000l + (crcOrPropertyIdentifier ^ ((guidIndex << 1) | 1l)) % 0x1Fl;
        long hexIdentifier = streamId << 16l | 0x00000102l;
        String streamName = "__substg1.0_" + Utils.toHex(Utils.toBytesLE(hexIdentifier, 4));
        StreamDirectoryEntry namesStream = storage.findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equalsIgnoreCase(streamName));
        Optional<EntryStream.Entry> idToNameMappingEntry = IntStream.range(0, namesStream.getStreamSize() / 8).mapToObj(i -> new EntryStream.Entry(namesStream.read(i * 8,(i + 1) * 8)))
                .filter(e -> e.getPropertyNameIdentifierOrOffsetOrChecksum() == crcOrPropertyIdentifier).findFirst();
        if(idToNameMappingEntry.isPresent()) {
            EntryStream.Entry entry = idToNameMappingEntry.get();
            return new PropertyTag(0x8000 + entry.getPropertyIndex(), propertyType);
        } else {
            return null;
        }
    }

    private GUIDStream guidStream() {
        return new GUIDStream(storage.findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equals(GUIDStream.STREAM_NAME)));
    }

    private EntryStream entryStream() {
        return new EntryStream(storage.findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equals(EntryStream.STREAM_NAME)));
    }

    private StringStream stringStream() {
        return new StringStream(storage.findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equals(StringStream.STREAM_NAME)));
    }
}
