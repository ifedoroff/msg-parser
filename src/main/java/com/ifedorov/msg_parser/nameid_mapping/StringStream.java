package com.ifedorov.msg_parser.nameid_mapping;

import com.ifedorov.cfbf.StreamDirectoryEntry;
import com.ifedorov.cfbf.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;

public class StringStream {
    public static final String STREAM_NAME = "__substg1.0_00040102";
    private StreamDirectoryEntry _this;

    public StringStream(StreamDirectoryEntry _this) {
        this._this = _this;
    }

    public String getPropertyNameAt(int startingOffset) {
        byte[] streamData = _this.getStreamData();
        int length = Utils.toInt(ArrayUtils.subarray(streamData, startingOffset, startingOffset + 4));
        byte[] bytes = ArrayUtils.subarray(streamData, startingOffset + 4, startingOffset + 4 + length);
        return new String(bytes, StandardCharsets.UTF_16LE);
    }
}
