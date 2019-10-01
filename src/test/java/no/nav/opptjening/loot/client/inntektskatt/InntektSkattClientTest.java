package no.nav.opptjening.loot.client.inntektskatt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.opptjening.loot.sts.TokenClient;
import no.nav.opptjening.loot.sts.TokenImpl;

@ExtendWith(MockitoExtension.class)
class InntektSkattClientTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private InntektSkattProperties inntektSkattProperties;
    @Mock
    private TokenClient tokenClient;
    @InjectMocks
    private InntektSkattClient inntektSkattClient = new InntektSkattClient();

    private HttpResponse okResponse;
    private HttpResponse errorResponse;

    @BeforeEach
    void beforeEach() {
        okResponse = mock(HttpResponse.class);
        when(okResponse.statusCode()).thenReturn(200);
        errorResponse = mock(HttpResponse.class);
        when(inntektSkattProperties.getUrl()).thenReturn(URI.create("http://localhost:9080/popp-ws/api/inntekt/ske"));
        when(tokenClient.getAccessToken()).thenReturn(new TokenImpl("tokenValue", 3600L, "Bearer"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetryWhenErrorResponse() throws Exception {
        when(errorResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(), any()))
                .thenReturn(errorResponse)
                .thenReturn(okResponse);
        inntektSkattClient.lagreInntektPopp(createRequest("01029804032", "2019"));
        verify(tokenClient, times(2)).getAccessToken();
        verify(httpClient, times(2)).send(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetryOnNextRequestIfImmediateResendFails() throws Exception {
        when(errorResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(), any()))
                .thenReturn(errorResponse)
                .thenReturn(errorResponse)
                .thenReturn(okResponse);
        inntektSkattClient.lagreInntektPopp(createRequest("01029804032", "2019"));
        inntektSkattClient.lagreInntektPopp(createRequest("80403201029", "2019"));
        inntektSkattClient.lagreInntektPopp(createRequest("03201029804", "2019"));
        verify(tokenClient, times(5)).getAccessToken();
        verify(httpClient, times(5)).send(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSendContinouslyIfNoErrors() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(okResponse);
        inntektSkattClient.lagreInntektPopp(createRequest("01029804032", "2019"));
        inntektSkattClient.lagreInntektPopp(createRequest("80403201029", "2019"));
        inntektSkattClient.lagreInntektPopp(createRequest("03201029804", "2019"));
        verify(tokenClient, times(3)).getAccessToken();
        verify(httpClient, times(3)).send(any(), any());
    }

    private LagreBeregnetSkattRequest createRequest(String fnr, String year) {
        InntektSKD inntektSKD = new InntektSKD();
        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(1L);
        inntektSKD.setPersoninntektLoenn(2L);
        inntektSKD.setPersoninntektNaering(3L);
        inntektSKD.setPersoninntektBarePensjonsdel(4L);
        inntektSKD.setSvalbardLoennLoennstrekkordningen(5L);
        inntektSKD.setSvalbardPersoninntektNaering(6L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent(fnr);
        lagreBeregnetSkattRequest.setInntektsaar(year);
        lagreBeregnetSkattRequest.setInntektSKD(inntektSKD);
        return lagreBeregnetSkattRequest;
    }
}