package dev.evo.elasticsearch.collapse;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.search.SearchExtBuilder;

import java.io.IOException;
import java.util.Objects;

public class CollapseSearchExtBuilder extends SearchExtBuilder {
    public static final String NAME = "collapse";

    private static final ParseField GROUP_FIELD_NAME = new ParseField("field");

    // Window size on which we will operate to group and collapse documents
    private static final ParseField WINDOW_SIZE_FIELD_NAME = new ParseField("window_size");
    private static final int DEFAULT_WINDOW_SIZE = 10_000;

    // Number of documents that will be returned from a shard
    private static final ParseField SHARD_SIZE_FIELD_NAME = new ParseField("shard_size");
    private static final int DEFAULT_SHARD_SIZE = 1_000;

    private static final ConstructingObjectParser<CollapseSearchExtBuilder, Void> PARSER =
        new ConstructingObjectParser<>(
            NAME,
            args -> new CollapseSearchExtBuilder((String) args[0])
        );
    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), GROUP_FIELD_NAME);
        PARSER.declareInt(CollapseSearchExtBuilder::windowSize, WINDOW_SIZE_FIELD_NAME);
        PARSER.declareInt(CollapseSearchExtBuilder::shardSize, SHARD_SIZE_FIELD_NAME);
    }

    private final String groupField;
    private int windowSize = DEFAULT_WINDOW_SIZE;
    private int shardSize = DEFAULT_SHARD_SIZE;

    public CollapseSearchExtBuilder(String groupField) {
        this.groupField = groupField;
    }

    public CollapseSearchExtBuilder(StreamInput in) throws IOException {
        groupField = in.readString();
        windowSize = in.readInt();
        shardSize = in.readInt();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(groupField);
        out.writeInt(windowSize);
        out.writeInt(shardSize);
    }

    public static CollapseSearchExtBuilder fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    public String groupField() {
        return groupField;
    }

    public void windowSize(int size) {
        this.windowSize = size;
    }

    public int windowSize() {
        return windowSize;
    }

    public void shardSize(int shardSize) {
        this.shardSize = shardSize;
    }

    public int shardSize() {
        return shardSize;
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.field(GROUP_FIELD_NAME.getPreferredName(), groupField);
        builder.field(WINDOW_SIZE_FIELD_NAME.getPreferredName(), windowSize);
        builder.field(SHARD_SIZE_FIELD_NAME.getPreferredName(), shardSize);
        builder.endObject();
        return builder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupField, windowSize, shardSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CollapseSearchExtBuilder)) {
            return false;
        }
        var other = (CollapseSearchExtBuilder) obj;
        return other.groupField.equals(groupField) &&
            other.windowSize == windowSize &&
            other.shardSize == shardSize;
    }
}
