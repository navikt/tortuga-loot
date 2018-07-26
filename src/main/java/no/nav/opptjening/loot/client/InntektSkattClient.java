package no.nav.opptjening.loot.client;

import io.prometheus.client.Counter;
import no.nav.popp.tjenester.inntektskatt.v1.InntektSkattV1;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattSikkerhetsbegrensning;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattUgyldigInput;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;

public class InntektSkattClient {

    private static final Counter lagreBeregnetSkattRequestsSentCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent")
            .help("Antall beregnet skatt requester sendt til popp.").register();

    private static InntektSkattV1 port;

    public InntektSkattClient(InntektSkattV1 port) {
        this.port = port;
    }

    public void lagreBeregnetSkatt(LagreBeregnetSkattRequest lagreBeregnetSkattRequest)
            throws LagreBeregnetSkattSikkerhetsbegrensning,
                   LagreBeregnetSkattUgyldigInput {

        LagreBeregnetSkatt lagreBeregnetSkatt = new LagreBeregnetSkatt();
        lagreBeregnetSkatt.setRequest(lagreBeregnetSkattRequest);
        port.lagreBeregnetSkatt(lagreBeregnetSkatt);
        lagreBeregnetSkattRequestsSentCounter.inc();
    }
}
