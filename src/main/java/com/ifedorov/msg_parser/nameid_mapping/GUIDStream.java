package com.ifedorov.msg_parser.nameid_mapping;

import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import com.ifedorov.msg_parser.property.PropertySet;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GUIDStream {

    public static final String STREAM_NAME = "__substg1.0_00020102";
    public static final int MAGICAL_SHIFT_INSIDE_STREAM = 3;
    public static final int GUID_LENGTH = 16;

    private StreamDirectoryEntry stream;

    public GUIDStream(StreamDirectoryEntry stream) {
        this.stream = stream;
    }

    public UUID getGuidAt(int index) {
        if(index == 1) {
            return PropertySet.PS_MAPI.id;
        } else if(index == 2) {
            return PropertySet.PUBLIC_STRINGS.id;
        } else {
            return Utils.uuidFromByteLE(getGuidBytes(index));
        }
    }

    private byte[] getGuidBytes(int index) {
        if(index == 1 || index == 2) {
            throw new IllegalArgumentException("Not implemented property type: PS_MAPI or PS_PUBLIC_STRINGS (" + index + ")");
        }
        int position = index - MAGICAL_SHIFT_INSIDE_STREAM;
        return stream.read(position * GUID_LENGTH, (position + 1) * GUID_LENGTH);
    }

    public Stream<UUID> guids() {
        return IntStream.range(3, 3+stream.getStreamSize() / 16).mapToObj(i->{
            byte[] littleEndianBytes = getGuidBytes(i);
            return Utils.uuidFromByteLE(littleEndianBytes);
        });
    }

    public int getIndexFor(UUID uid) {
        return IntStream.range(3, 3+stream.getStreamSize() / 16).filter(i->{
            byte[] littleEndianBytes = getGuidBytes(i);
            return uid.equals(Utils.uuidFromByteLE(littleEndianBytes));
        }).findFirst().getAsInt();
    }
}
