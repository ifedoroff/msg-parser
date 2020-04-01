package samples;

import com.ifedorov.cfbf.CompoundFile;
import com.ifedorov.cfbf.DataView;
import com.ifedorov.msg_parser.EmbeddedMessage;
import com.ifedorov.msg_parser.Msg;
import com.ifedorov.msg_parser.property.KnownProperties;
import com.ifedorov.msg_parser.property.PropertyType;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtractEmbeddedMessagesAndAttachments {

    @Test
    void testAttachments() throws IOException {
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("Top level email.msg")) {
            CompoundFile compoundFile = new CompoundFile(DataView.from(is));
            Msg msg = new Msg(compoundFile);
            msg.attachments()
                    .forEach(attachment -> {
                        try {
                            String subject = "";
                            if(attachment.propertiesStream().findProperty(KnownProperties.PidTagAttachFilename).isPresent()) {
                                subject = (String) PropertyType.forId(KnownProperties.PidTagAttachFilename.propertyType).resolveValue(attachment, KnownProperties.PidTagAttachFilename);
                            } else {
                                subject = "Untitled";
                            }

                            try (OutputStream os = new FileOutputStream("build/" + subject.replaceAll("[\\/:]", "_"))) {
                                attachment.writeTo(os);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (RuntimeException e) {
                            throw e;
                        }
                    });
        }
    }

    @Test
    void testExtractEmbeddedMessages() throws IOException {
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("Top level email.msg")) {
            CompoundFile compoundFile = new CompoundFile(DataView.from(is));
            Msg msg = new Msg(compoundFile);
            msg.embeddedMessages()
                    .forEach(embeddedMessage -> {
                        EmbeddedMessage.InternalStorage internalStorage = embeddedMessage.internalStorage();
                        try {
                            String subject = "";
                            if(internalStorage.propertiesStream().findProperty(KnownProperties.PidTagSubject).isPresent()) {
                                subject = (String) PropertyType.forId(KnownProperties.PidTagSubject.propertyType).resolveValue(internalStorage, KnownProperties.PidTagSubject);
                            } else if(embeddedMessage.propertiesStream().findProperty(KnownProperties.PidTagAttachFilename).isPresent()) {
                                subject = (String) PropertyType.forId(KnownProperties.PidTagAttachFilename.propertyType).resolveValue(embeddedMessage, KnownProperties.PidTagAttachFilename);
                            } else {
                                subject = "Untitled";
                            }

                            try (OutputStream os = new FileOutputStream("build/" + subject.replaceAll("[\\/:]", "_") + ".msg")) {
                                msg.writeTo(embeddedMessage, os);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (RuntimeException e) {
                            throw e;
                        }
                    });
        }
    }
}
