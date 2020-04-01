package com.ifedorov.msg_parser.property;

public class PropertyNameLID {

    public final PropertySet propertySet;
    public final long propertyLID;
    public final int propertyType;

    PropertyNameLID(PropertySet propertySet, long propertyLID, int propertyTypeId) {

        this.propertySet = propertySet;
        this.propertyLID = propertyLID;
        this.propertyType = propertyTypeId;
    }
}
