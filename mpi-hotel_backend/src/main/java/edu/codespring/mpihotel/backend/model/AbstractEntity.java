package edu.codespring.mpihotel.backend.model;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractEntity {
    private String uuid;

    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(getUuid(), ((AbstractEntity) o).getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
