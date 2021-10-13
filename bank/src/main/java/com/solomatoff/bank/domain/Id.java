package com.solomatoff.bank.domain;

public abstract class Id {

    protected int id;

    public Id() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Id id1 = (Id) o;

        return id == id1.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
