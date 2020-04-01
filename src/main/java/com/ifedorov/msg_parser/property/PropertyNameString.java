package com.ifedorov.msg_parser.property;

public class PropertyNameString {

    public final PropertySet propertySet;
    public final String propertyName;
    public final int propertyType;

    PropertyNameString(PropertySet propertySet, String propertyName, int propertyTypeId) {

        this.propertySet = propertySet;
        this.propertyName = propertyName;
        this.propertyType = propertyTypeId;
    }
}
