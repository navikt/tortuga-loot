package no.nav.opptjening.loot.sts;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class STSClientConfig {

    private static final String STS_CLIENT_AUTHENTICATION_POLICY = "classpath:untPolicy.xml";
    private static final String STS_SAML_POLICY = "classpath:samlPolicy.xml";

    public static <T> T konfigurerKlientTilAaSendeStsUtstedtSaml(T port, STSProperties stsProperties) {
        Client client = ClientProxy.getClient(port);

        STSClient stsClient = konfigurerStsClient(client.getBus(), stsProperties);

        client.getRequestContext().put(SecurityConstants.STS_CLIENT, stsClient);
        client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);

        konigurerEndpointPolicyReference(client, STS_SAML_POLICY);

        return port;
    }

    private static void konigurerEndpointPolicyReference(Client client, String stsSamlPolicy) {
        Bus bus = client.getBus();

        PolicyBuilder policyBuilder = bus.getExtension(PolicyBuilder.class);
        ReferenceResolver referenceResolver = new RemoteReferenceResolver("", policyBuilder);
        Policy policy = referenceResolver.resolveReference(stsSamlPolicy);

        PolicyEngine policyEngine = bus.getExtension(PolicyEngine.class);
        SoapMessage message = new SoapMessage(Soap12.getInstance());
        Endpoint endpoint = client.getEndpoint();
        EndpointInfo endpointInfo = endpoint.getEndpointInfo();
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));

    }

    private static STSClient konfigurerStsClient(Bus bus, STSProperties stsProperties) {
        STSClient stsClient = new STSClient(bus){
            @Override
            protected boolean useSecondaryParameters() {
                return false;
            }
        };
        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(stsProperties.getStsUrl());
        stsClient.setFeatures(new ArrayList<Feature>(Arrays.asList(new LoggingFeature())));

        HashMap<String, Object> properties = new HashMap<>();
        properties.put(SecurityConstants.USERNAME, stsProperties.getUsername());
        properties.put(SecurityConstants.PASSWORD, stsProperties.getPassword());

        stsClient.setProperties(properties);

        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);

        return stsClient;
    }
}
