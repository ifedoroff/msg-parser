package com.ifedorov.msg_parser.property;

import com.ifedorov.cfbf.Utils;
import com.ifedorov.msg_parser.nameid_mapping.NamedPropertyMappingStorage;

import java.util.Objects;

public class PropertyTag {

    public static final int MV_PROPERTY_TYPE_BASE = 0x1000;
    public final int propertyId;
    public final int propertyType;

    public PropertyTag(int propertyId, int propertyTypeId) {
        this.propertyId = propertyId;
        this.propertyType = propertyTypeId;
    }

    public boolean isNamed() {
        return (propertyId & NamedPropertyMappingStorage.NAMED_PROPERTY_ID_BASE) == NamedPropertyMappingStorage.NAMED_PROPERTY_ID_BASE;
    }

    public boolean isMultiValued() {
        return (propertyType & MV_PROPERTY_TYPE_BASE) == MV_PROPERTY_TYPE_BASE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyTag that = (PropertyTag) o;
        return propertyId == that.propertyId &&
                propertyType == that.propertyType;
    }

    @Override
    public String toString() {
        return Utils.toHex(Utils.toBytesLE(propertyId, 2)).toUpperCase() + Utils.toHex(Utils.toBytesLE(propertyType, 2)).toUpperCase();
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId, propertyType);
    }
}
