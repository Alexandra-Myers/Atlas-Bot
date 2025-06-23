package net.atlas.atlasbot;

import java.util.Objects;

public class ExclusionTarget {
    public ExclusionType type;
    public String id;

    public ExclusionTarget(ExclusionType type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExclusionTarget that)) return false;
        return type == that.type && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    public enum ExclusionType {
        USER,
        ROLE,
        ROLE_GROUP
    }
}
