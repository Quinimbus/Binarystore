package cloud.quinimbus.binarystore.cdi;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.config.api.ConfigNode;
import cloud.quinimbus.config.cdi.ConfigPath;
import java.util.ServiceLoader;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import name.falgout.jeffrey.throwing.stream.ThrowingStream;

@ApplicationScoped
public class BinaryStoreContextProducer {

    private final BinaryStoreContext binaryStoreContext;

    @Inject
    @ConfigPath(value = "binary.stores", optional = true)
    private ConfigNode configNode;

    public BinaryStoreContextProducer() throws BinaryStoreException {
        this.binaryStoreContext = ServiceLoader.load(BinaryStoreContext.class).findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find any BinaryStoreContext implementation"));
    }

    @PostConstruct
    public void init() throws BinaryStoreException {
        if (this.configNode != null) {
            ThrowingStream.of(this.configNode.stream(), BinaryStoreException.class)
                    .forEach(this::initConfiguredStore);
        }
    }

    @Produces
    public BinaryStoreContext getContext() {
        return this.binaryStoreContext;
    }

    private void initConfiguredStore(ConfigNode node) throws BinaryStoreException {
        var type = node.asString("type")
                .orElseThrow(() -> new BinaryStoreException(
                        "Cannot autoconfigure storage %s, type configuration is missing".formatted(node.name())));
        try {
            var provider = this.binaryStoreContext.getProvider(type)
                    .orElseThrow(() -> new BinaryStoreException(
                            "Cannot autoconfigure storage %s, no provider for type %s found"
                                    .formatted(node.name(), type)));
            var storage = provider.createStorage(node);
            this.binaryStoreContext.setStorage(node.name(), storage);
        } catch (BinaryStoreException ex) {
            throw new BinaryStoreException("Cannot autoconfigure storage %s".formatted(node.name()), ex);
        }
    }
}
