package com.ifedorov.msg_parser;

import com.ifedorov.cfbf.StorageDirectoryEntry;
import com.ifedorov.msg_parser.property.KnownProperties;
import com.ifedorov.msg_parser.property.PropertyType;

import java.util.stream.Stream;

import static com.ifedorov.msg_parser.Recipient.RECIPIENT_STORAGE_PREFIX;

public abstract class AbstractMessage extends MessageStorage{
    public AbstractMessage(StorageDirectoryEntry storage) {
        super(storage);
    }

    public Stream<Attachment> attachments() {
        return underlyingDirectoryEntry().storages().filter(storageDirectoryEntry -> storageDirectoryEntry.getDirectoryEntryName().startsWith(Msg.ATTACHMENT_STORAGE_PREFIX))
                .filter(storageDirectoryEntry -> storageDirectoryEntry.storages().noneMatch(substorage -> substorage.getDirectoryEntryName().equals(EmbeddedMessage.INTERNAL_STORAGE_NAME)))
                .map(Attachment::new);
    }

    public Stream<EmbeddedMessage> embeddedMessages() {
        return underlyingDirectoryEntry().storages().filter(storageDirectoryEntry -> storageDirectoryEntry.getDirectoryEntryName().startsWith(Msg.ATTACHMENT_STORAGE_PREFIX))
                .filter(storageDirectoryEntry -> storageDirectoryEntry.storages().anyMatch(substorage -> substorage.getDirectoryEntryName().equals(EmbeddedMessage.INTERNAL_STORAGE_NAME)))
                .map(EmbeddedMessage::new)
                .filter(embeddedMsg -> PropertyType.<PropertyType<Integer>>forId(KnownProperties.PidTagAttachMethod.propertyType).resolveValue(embeddedMsg, KnownProperties.PidTagAttachMethod) != EmbeddedMessage.ATTACH_METHOD_CUSTOM);
    }

    public Stream<Recipient> recipients() {
        return underlyingDirectoryEntry().storages().filter(storageDirectoryEntry -> storageDirectoryEntry.getDirectoryEntryName().startsWith(RECIPIENT_STORAGE_PREFIX))
                .map(Recipient::new);
    }
}
