package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.*;
import com.ifedorov.msg_parser.nameid_mapping.NamedPropertyMappingStorage;
import com.ifedorov.msg_parser.property.KnownProperties;
import com.ifedorov.msg_parser.property.PropertiesStream;
import com.ifedorov.msg_parser.property.PropertyType;
import org.apache.commons.lang3.ArrayUtils;

import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.ifedorov.msg_parser.Recipient.RECIPIENT_STORAGE_PREFIX;

public class Msg extends AbstractMessage {

    public static final String ATTACHMENT_STORAGE_PREFIX = "__attach_version1.0";

    private CompoundFile compoundFile;

    public Msg(CompoundFile compoundFile) {
        super(compoundFile.getRootStorage());
        this.compoundFile = compoundFile;
    }

    @Override
    protected PropertiesStream createPropertiesStream(StreamDirectoryEntry stream) {
        return new PropertyStream(stream);
    }

    public NamedPropertyMappingStorage namedPropertyMappingStorage() {
        return new NamedPropertyMappingStorage(underlyingDirectoryEntry().findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equalsIgnoreCase(NamedPropertyMappingStorage.STORAGE_NAME)));
    }

    public void writeTo(EmbeddedMessage embeddedMessage, OutputStream outputStream) {
        StorageDirectoryEntry nameidmapping = underlyingDirectoryEntry().findChild((directoryEntry -> NamedPropertyMappingStorage.STORAGE_NAME.equalsIgnoreCase(directoryEntry.getDirectoryEntryName())));
        CompoundFile copy = new CompoundFile();
        StorageDirectoryEntry nameidmappingCopy = copy.getRootStorage().addStorage(NamedPropertyMappingStorage.STORAGE_NAME);
        nameidmapping.eachChild(new CopyConsumer(nameidmappingCopy));
        embeddedMessage.internalStorage().underlyingDirectoryEntry().eachChild(new CopyConsumer(copy.getRootStorage()));
        StreamDirectoryEntry propertiesStream = copy.getRootStorage().findChild(directoryEntry -> directoryEntry.getDirectoryEntryName().equals(PropertiesStream.STREAM_NAME));
        byte[] streamData = propertiesStream.getStreamData();
        streamData = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.subarray(streamData, 0, 24), new byte[8]), ArrayUtils.subarray(streamData, 24, streamData.length));
        propertiesStream.setStreamData(streamData);
        copy.saveTo(outputStream);
    }

    private static class CopyConsumer implements Consumer<DirectoryEntry>{

        private StorageDirectoryEntry parent;

        private CopyConsumer(StorageDirectoryEntry parent) {
            this.parent = parent;
        }

        @Override
        public void accept(DirectoryEntry directoryEntry) {
            DirectoryEntry copy = null;
            if(directoryEntry instanceof StorageDirectoryEntry) {
                copy = parent.addStorage(directoryEntry.getDirectoryEntryName());
                ((StorageDirectoryEntry) directoryEntry).eachChild(new CopyConsumer((StorageDirectoryEntry) copy));
            } else {
                copy = parent.addStream(directoryEntry.getDirectoryEntryName(), ((StreamDirectoryEntry)directoryEntry).getStreamData());
            }
            copy.setCLSID(directoryEntry.getCLSID());
            copy.setStateBits(directoryEntry.getStateBits());
            copy.setCreationTime(directoryEntry.getCreationTime());
            copy.setModifiedTime(directoryEntry.getModifiedTime());
        }
    }

    public static class PropertyStream extends PropertiesStream {
        public static final int HEADER_LENGTH = 32;
        private final Header header;

        public PropertyStream(StreamDirectoryEntry stream) {
            super(stream, HEADER_LENGTH);
            header = new Header();
        }

        private class Header {

            private final byte[] data;

            private Header() {
                data = stream.read(0, 32);
            }


            private int nextRecipientId() {
                return Utils.toInt(ArrayUtils.subarray(data, 8, 12));
            }

            private int nextAttachmentId() {
                return Utils.toInt(ArrayUtils.subarray(data, 12, 16));
            }

            private int recipientCount() {
                return Utils.toInt(ArrayUtils.subarray(data, 16, 20));
            }

            private int attachmentCount() {
                return Utils.toInt(ArrayUtils.subarray(data, 20, 24));
            }
        }

    }
}
