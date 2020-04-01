package com.ifedorov.msg_parser.property;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class PropertySet {

    public static PropertySet PUBLIC_STRINGS = new PropertySet("PS_PUBLIC_STRINGS", UUID.fromString("00020329-0000-0000-C000-000000000046"));
    public static PropertySet COMMON = new PropertySet("PSETID_Common", UUID.fromString("00062008-0000-0000-C000-000000000046"));
    public static PropertySet ADDRESS = new PropertySet("PSETID_Address", UUID.fromString("00062004-0000-0000-C000-000000000046"));
    public static PropertySet HEADERS = new PropertySet("PS_INTERNET_HEADERS", UUID.fromString("00020386-0000-0000-C000-000000000046"));
    public static PropertySet APPOINTMENT = new PropertySet("PSETID_Appointment", UUID.fromString("00062002-0000-0000-C000-000000000046"));
    public static PropertySet MEETING = new PropertySet("PSETID_Meeting", UUID.fromString("6ED8DA90-450B-101B-98DA-00AA003F1305"));
    public static PropertySet LOG = new PropertySet("PSETID_Log", UUID.fromString("0006200A-0000-0000-C000-000000000046"));
    public static PropertySet MESSAGING = new PropertySet("PSETID_Messaging", UUID.fromString("41F28F13-83F4-4114-A584-EEDB5A6B0BFF"));
    public static PropertySet NOTE = new PropertySet("PSETID_Note", UUID.fromString("0006200E-0000-0000-C000-000000000046"));
    public static PropertySet POST_RSS = new PropertySet("PSETID_PostRss", UUID.fromString("00062041-0000-0000-C000-000000000046"));
    public static PropertySet TASK = new PropertySet("PSETID_Task", UUID.fromString("00062003-0000-0000-C000-000000000046"));
    public static PropertySet UNIFIED_MESSAGING = new PropertySet("PSETID_UnifiedMessaging", UUID.fromString("4442858E-A9E3-4E80-B900-317A210CC15B"));
    public static PropertySet PS_MAPI = new PropertySet("PS_MAPI", UUID.fromString("00020328-0000-0000-C000-000000000046"));
    public static PropertySet AIR_SYNC = new PropertySet("PSETID_AirSync", UUID.fromString("71035549-0739-4DCB-9163-00F0580DBBDF"));
    public static PropertySet SHARING = new PropertySet("PSETID_Sharing", UUID.fromString("00062040-0000-0000-C000-000000000046"));
    public static PropertySet XML_EXTR_ENTITIES = new PropertySet("PSETID_XmlExtractedEntities", UUID.fromString("23239608-685D-4732-9C55-4C95CB4E8E33"));
    public static PropertySet ATTACHMENT = new PropertySet("PSETID_Attachment", UUID.fromString("96357F7F-59E1-47D0-99A7-46515C183B54"));
    public static PropertySet CALENDAR_ASSISTANT = new PropertySet("PSETID_Attachment", UUID.fromString("11000E07-B51B-40D6-AF21-CAA85EDAB1D0"));

    private final static Map<UUID, PropertySet> PROPERTY_SETS = Maps.newHashMap();
    static {
        PROPERTY_SETS.put(UUID.fromString("00020329-0000-0000-C000-000000000046"), PUBLIC_STRINGS);
        PROPERTY_SETS.put(UUID.fromString("00062008-0000-0000-C000-000000000046"), COMMON);
        PROPERTY_SETS.put(UUID.fromString("00062004-0000-0000-C000-000000000046"), ADDRESS);
        PROPERTY_SETS.put(UUID.fromString("00020386-0000-0000-C000-000000000046"), HEADERS);
        PROPERTY_SETS.put(UUID.fromString("00062002-0000-0000-C000-000000000046"), APPOINTMENT);
        PROPERTY_SETS.put(UUID.fromString("6ED8DA90-450B-101B-98DA-00AA003F1305"), MEETING);
        PROPERTY_SETS.put(UUID.fromString("0006200A-0000-0000-C000-000000000046"), LOG);
        PROPERTY_SETS.put(UUID.fromString("41F28F13-83F4-4114-A584-EEDB5A6B0BFF"), MESSAGING);
        PROPERTY_SETS.put(UUID.fromString("0006200E-0000-0000-C000-000000000046"), NOTE);
        PROPERTY_SETS.put(UUID.fromString("00062041-0000-0000-C000-000000000046"), POST_RSS);
        PROPERTY_SETS.put(UUID.fromString("00062003-0000-0000-C000-000000000046"), TASK);
        PROPERTY_SETS.put(UUID.fromString("4442858E-A9E3-4E80-B900-317A210CC15B"), UNIFIED_MESSAGING);
        PROPERTY_SETS.put(UUID.fromString("00020328-0000-0000-C000-000000000046"), PS_MAPI);
        PROPERTY_SETS.put(UUID.fromString("71035549-0739-4DCB-9163-00F0580DBBDF"), AIR_SYNC);
        PROPERTY_SETS.put(UUID.fromString("00062040-0000-0000-C000-000000000046"), SHARING);
        PROPERTY_SETS.put(UUID.fromString("23239608-685D-4732-9C55-4C95CB4E8E33"), XML_EXTR_ENTITIES);
        PROPERTY_SETS.put(UUID.fromString("96357F7F-59E1-47D0-99A7-46515C183B54"), ATTACHMENT);
        PROPERTY_SETS.put(UUID.fromString("11000E07-B51B-40D6-AF21-CAA85EDAB1D0"), CALENDAR_ASSISTANT);
    }

    public static PropertySet forUUID(UUID uuid) {
        return PROPERTY_SETS.get(uuid);
    }

    public final UUID id;
    public final String name;

    public PropertySet(String name, UUID id) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " " + id;
    }
}
