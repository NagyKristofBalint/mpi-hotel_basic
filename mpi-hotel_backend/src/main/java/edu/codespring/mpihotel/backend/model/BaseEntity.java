package edu.codespring.mpihotel.backend.model;

public abstract class BaseEntity extends AbstractEntity {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
