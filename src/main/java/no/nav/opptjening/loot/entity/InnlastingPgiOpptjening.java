package no.nav.opptjening.loot.entity;

import java.util.Date;

public class InnlastingPgiOpptjening {
    private final long id;

    private final String filId;

    private final String recart;

    private final String rappart;

    private final String inntaar;

    private final String kommnr;

    private final String rappdato;

    private final String skjonnsligning;

    private final String pgiLonn;

    private final String pgiNaring;

    private final String avvistKode;

    private final String pgiJsf;

    private final String status;

    private final String filInfoId;

    private final String rapportertFnr;

    private final String kontekst;

    private final String avvistInntektId;

    private final Long personId;

    private final String endretAv;

    private final String opprettetAv;

    private final String failedInStep;

    private final Date datoEndret;

    private final String feilmelding;

    private final Date datoOpprettet;

    public InnlastingPgiOpptjening(long id, String filId, String recart, String rappart, String inntaar,
                                   String kommnr, String rappdato, String skjonnsligning, String pgiLonn,
                                   String pgiNaring, String avvistKode, String pgiJsf, String status, String filInfoId,
                                   String rapportertFnr, String kontekst, String avvistInntektId, Long personId,
                                   String endretAv, String opprettetAv, String failedInStep, Date datoEndret,
                                   String feilmelding, Date datoOpprettet) {
        this.id = id;
        this.filId = filId;
        this.recart = recart;
        this.rappart = rappart;
        this.inntaar = inntaar;
        this.kommnr = kommnr;
        this.rappdato = rappdato;
        this.skjonnsligning = skjonnsligning;
        this.pgiLonn = pgiLonn;
        this.pgiNaring = pgiNaring;
        this.avvistKode = avvistKode;
        this.pgiJsf = pgiJsf;
        this.status = status;
        this.filInfoId = filInfoId;
        this.rapportertFnr = rapportertFnr;
        this.kontekst = kontekst;
        this.avvistInntektId = avvistInntektId;
        this.personId = personId;
        this.endretAv = endretAv;
        this.opprettetAv = opprettetAv;
        this.failedInStep = failedInStep;
        this.datoEndret = datoEndret;
        this.feilmelding = feilmelding;
        this.datoOpprettet = datoOpprettet;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    public String getFilId() {
        return filId;
    }

    public String getRecart() {
        return recart;
    }

    public String getRappart() {
        return rappart;
    }

    public String getInntaar() {
        return inntaar;
    }

    public String getKommnr() {
        return kommnr;
    }

    public String getRappdato() {
        return rappdato;
    }

    public String getSkjonnsligning() {
        return skjonnsligning;
    }

    public String getPgiLonn() {
        return pgiLonn;
    }

    public String getPgiNaring() {
        return pgiNaring;
    }

    public String getAvvistKode() {
        return avvistKode;
    }

    public String getPgiJsf() {
        return pgiJsf;
    }

    public String getStatus() {
        return status;
    }

    public String getFilInfoId() {
        return filInfoId;
    }

    public String getRapportertFnr() {
        return rapportertFnr;
    }

    public String getKontekst() {
        return kontekst;
    }

    public String getAvvistInntektId() {
        return avvistInntektId;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public String getFailedInStep() {
        return failedInStep;
    }

    public Date getDatoEndret() {
        return datoEndret;
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public Date getDatoOpprettet() {
        return datoOpprettet;
    }

    public static final class Builder {
        private long id;
        private String filId;
        private String recart;
        private String rappart;
        private String inntaar;
        private String kommnr;
        private String rappdato;
        private String skjonnsligning;
        private String pgiLonn;
        private String pgiNaring;
        private String avvistKode;
        private String pgiJsf;
        private String status;
        private String filInfoId;
        private String rapportertFnr;
        private String kontekst;
        private String avvistInntektId;
        private Long personId;
        private String endretAv;
        private String opprettetAv;
        private String failedInStep;
        private Date datoEndret;
        private String feilmelding;
        private Date datoOpprettet;

        private Builder() {
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withFilId(String filId) {
            this.filId = filId;
            return this;
        }

        public Builder withRecart(String recart) {
            this.recart = recart;
            return this;
        }

        public Builder withRappart(String rappart) {
            this.rappart = rappart;
            return this;
        }

        public Builder withInntaar(String inntaar) {
            this.inntaar = inntaar;
            return this;
        }

        public Builder withKommnr(String kommnr) {
            this.kommnr = kommnr;
            return this;
        }

        public Builder withRappdato(String rappdato) {
            this.rappdato = rappdato;
            return this;
        }

        public Builder withSkjonnsligning(String skjonnsligning) {
            this.skjonnsligning = skjonnsligning;
            return this;
        }

        public Builder withPgiLonn(String pgiLonn) {
            this.pgiLonn = pgiLonn;
            return this;
        }

        public Builder withPgiNaring(String pgiNaring) {
            this.pgiNaring = pgiNaring;
            return this;
        }

        public Builder withAvvistKode(String avvistKode) {
            this.avvistKode = avvistKode;
            return this;
        }

        public Builder withPgiJsf(String pgiJsf) {
            this.pgiJsf = pgiJsf;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withFilInfoId(String filInfoId) {
            this.filInfoId = filInfoId;
            return this;
        }

        public Builder withRapportertFnr(String rapportertFnr) {
            this.rapportertFnr = rapportertFnr;
            return this;
        }

        public Builder withKontekst(String kontekst) {
            this.kontekst = kontekst;
            return this;
        }

        public Builder withAvvistInntektId(String avvistInntektId) {
            this.avvistInntektId = avvistInntektId;
            return this;
        }

        public Builder withPersonId(Long personId) {
            this.personId = personId;
            return this;
        }

        public Builder withEndretAv(String endretAv) {
            this.endretAv = endretAv;
            return this;
        }

        public Builder withOpprettetAv(String opprettetAv) {
            this.opprettetAv = opprettetAv;
            return this;
        }

        public Builder withFailedInStep(String failedInStep) {
            this.failedInStep = failedInStep;
            return this;
        }

        public Builder withDatoEndret(Date datoEndret) {
            this.datoEndret = datoEndret;
            return this;
        }

        public Builder withFeilmelding(String feilmelding) {
            this.feilmelding = feilmelding;
            return this;
        }

        public Builder withDatoOpprettet(Date datoOpprettet) {
            this.datoOpprettet = datoOpprettet;
            return this;
        }

        public InnlastingPgiOpptjening build() {
            return new InnlastingPgiOpptjening(id, filId, recart, rappart, inntaar, kommnr, rappdato, skjonnsligning,
                    pgiLonn, pgiNaring, avvistKode, pgiJsf, status, filInfoId, rapportertFnr, kontekst, avvistInntektId,
                    personId, endretAv, opprettetAv, failedInStep, datoEndret, feilmelding, datoOpprettet);
        }
    }
}
