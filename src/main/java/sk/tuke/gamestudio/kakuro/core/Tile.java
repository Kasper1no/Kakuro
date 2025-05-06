package sk.tuke.gamestudio.kakuro.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueTile.class, name = "value"),
        @JsonSubTypes.Type(value = SumTile.class, name = "sum")
})
abstract class Tile { }
