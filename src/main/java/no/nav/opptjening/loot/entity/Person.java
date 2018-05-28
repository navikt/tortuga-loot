package no.nav.opptjening.loot.entity;

import java.util.Date;

public class Person {

    private final long id;

    private final String fnr;

    private final Date datoOpprettet;

    private final String opprettetAv;

    private final Date datoEndret;

    private final String endretAv;

    private final long versjon;

    public Person(long id, String fnr, Date datoOpprettet, String opprettetAv, Date datoEndret, String endretAv, long versjon) {
        this.id = id;
        this.fnr = fnr;
        this.datoOpprettet = datoOpprettet;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret;
        this.endretAv = endretAv;
        this.versjon = versjon;
    }

    public long getId() {
        return id;
    }

    public String getFnr() {
        return fnr;
    }

    public Date getDatoOpprettet() {
        return datoOpprettet;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public Date getDatoEndret() {
        return datoEndret;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public long getVersjon() {
        return versjon;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long id;
        private String fnr;
        private Date datoOpprettet;
        private String opprettetAv;
        private Date datoEndret;
        private String endretAv;
        private long versjon;

        private Builder() {
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withFnr(String fnr) {
            this.fnr = fnr;
            return this;
        }

        public Builder withDatoOpprettet(Date datoOpprettet) {
            this.datoOpprettet = datoOpprettet;
            return this;
        }

        public Builder withOpprettetAv(String opprettetAv) {
            this.opprettetAv = opprettetAv;
            return this;
        }

        public Builder withDatoEndret(Date datoEndret) {
            this.datoEndret = datoEndret;
            return this;
        }

        public Builder withEndretAv(String endretAv) {
            this.endretAv = endretAv;
            return this;
        }

        public Builder withVersjon(long versjon) {
            this.versjon = versjon;
            return this;
        }

        public Person build() {
            return new Person(id, fnr, datoOpprettet, opprettetAv, datoEndret, endretAv, versjon);
        }
    }
}
