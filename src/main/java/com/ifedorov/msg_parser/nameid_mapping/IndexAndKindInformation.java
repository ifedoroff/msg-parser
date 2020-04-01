package com.ifedorov.msg_parser.nameid_mapping;

import com.ifedorov.cfbf.Utils;
import org.apache.commons.lang3.ArrayUtils;

class IndexAndKindInformation {

    public enum PropertyKind {
        NUMERIC, STRING
    }

    private final int propertyIndex;
    private byte[] bytes;
    private final int guidIndex;
    private final PropertyKind propertyKind;

    IndexAndKindInformation(byte[] bytes) {
        propertyIndex = Utils.toInt(ArrayUtils.subarray(bytes, 2, 4));
        this.bytes = bytes;
        byte[] twoTrailingBytesArray = ArrayUtils.subarray(bytes, 0, 2);
        int twoTrailingBytes = Utils.toInt(twoTrailingBytesArray);
        guidIndex = twoTrailingBytes >> 1;
        if((twoTrailingBytes & 1) == 1) {
            propertyKind = PropertyKind.STRING;
        } else {
            propertyKind = PropertyKind.NUMERIC;
        }
    }

    int getPropertyIndex() {
        return propertyIndex;
    }

    int getGuidIndex() {
        return guidIndex;
    }

    PropertyKind getPropertyKind() {
        return propertyKind;
    }
}
