package cloud.quinimbus.binarystore.cdi;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.common.annotations.Id;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

@ApplicationScoped
public class BinaryStoreProducer {

    @Inject
    private BinaryStoreContext binaryStoreContext;

    @Produces
    @Dependent
    public BinaryStorage getConfigNode(InjectionPoint ip) throws BinaryStoreException {
        var idAnno = ip.getAnnotated().getAnnotation(Id.class);
        return this.binaryStoreContext
                .getStorage(idAnno.value())
                .orElseThrow(() -> new BinaryStoreException("Cannot find binary storage %s".formatted(idAnno.value())));
    }
}
