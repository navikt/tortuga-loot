package no.nav.opptjening.loot.client.inntektskatt;

import java.net.http.HttpResponse;

public class CouldNotReachPoppException extends RuntimeException {

    CouldNotReachPoppException(HttpResponse response, LagreBeregnetSkattRequest request) {
        super("Request to POPP failed with status: " + response.statusCode() + ", message:" + response.body() + ", for person:" + request.getPersonIdent()
                + " , year: " + request.getInntektsaar() + ". Crashing intentionally to try again on same user");
    }

    CouldNotReachPoppException(String message, Throwable e) {
        super(message, e);
    }
}
