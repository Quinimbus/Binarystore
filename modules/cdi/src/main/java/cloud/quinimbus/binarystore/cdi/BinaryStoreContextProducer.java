package cloud.quinimbus.binarystore.cdi;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import java.util.ServiceLoader;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class BinaryStoreContextProducer {

    private final BinaryStoreContext binaryStoreContext;

    public BinaryStoreContextProducer() {
        this.binaryStoreContext = ServiceLoader.load(BinaryStoreContext.class).findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find any BinaryStoreContext implementation"));
    }
    
    @Produces
    public BinaryStoreContext getContext() {
        return this.binaryStoreContext;
    }
}
