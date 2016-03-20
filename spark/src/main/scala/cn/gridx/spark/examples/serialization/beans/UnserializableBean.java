package cn.gridx.spark.examples.serialization.beans;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

public class UnserializableBean {
    public ImmutableBytesWritable bytes;
    public String tag;

    public UnserializableBean() {
        bytes = new ImmutableBytesWritable();
        tag = "error";
    }

    public UnserializableBean(byte[] bytes, String tag) {
        this.bytes = new ImmutableBytesWritable(bytes);
        this.tag = tag;
    }
}
