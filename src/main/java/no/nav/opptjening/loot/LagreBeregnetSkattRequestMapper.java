package no.nav.opptjening.loot;

import io.prometheus.client.Counter;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.List;

import static no.nav.opptjening.loot.PensjonsgivendeInntektMapper.mapToInntektSkatt;
import static no.nav.opptjening.loot.PensjonsgivendeInntektRecordMapper.mapToLagreBeregnetSkattRequest;

public class LagreBeregnetSkattRequestMapper {

    private static final Counter pensjonsgivendeInntekterRecieved = Counter.build()
            .name("pensjonsgivende_inntekter_recieved")
            .help("Antall pensjonsgivende inntekter mottatt").register();

    private static final Counter pensjonsgivendeInntekterProcessed = Counter.build()
            .name("pensjonsgivende_inntekter_processed")
            .help("Antall pensjonsgivende inntekter prosessert").register();

    public static List<LagreBeregnetSkattRequest> recordsToRequestList(ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntektRecords) {

        List<LagreBeregnetSkattRequest> lagreBeregnetSkattRequestList = new ArrayList<>();

        for(ConsumerRecord<String, PensjonsgivendeInntekt> record : pensjonsgivendeInntektRecords) {
            pensjonsgivendeInntekterRecieved.inc();
            InntektSkatt inntektSkatt = mapToInntektSkatt(record.value());
            LagreBeregnetSkattRequest request = mapToLagreBeregnetSkattRequest(record.key(), inntektSkatt);
            lagreBeregnetSkattRequestList.add(request);
        }

        pensjonsgivendeInntekterProcessed.inc(lagreBeregnetSkattRequestList.size());
        return lagreBeregnetSkattRequestList;
    }
}
