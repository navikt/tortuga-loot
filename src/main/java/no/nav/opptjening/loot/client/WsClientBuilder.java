package no.nav.opptjening.loot.client;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.util.function.Supplier;

public class WsClientBuilder {

    private final Supplier<JaxWsProxyFactoryBean> factorySupplier;

    public WsClientBuilder(Supplier<JaxWsProxyFactoryBean> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @SuppressWarnings("unchecked")
    public <T> T createPort(String serviceUrl, Class<T> serviceClass) {
        JaxWsProxyFactoryBean factory = factorySupplier.get();

        factory.setAddress(serviceUrl);
        factory.setServiceClass(serviceClass);
        factory.getFeatures().add(new LoggingFeature());
        factory.getInInterceptors().add(new PoppFaultLoggingInterceptor());

        return (T)factory.create();
    }
}
