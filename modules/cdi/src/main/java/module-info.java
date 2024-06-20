import cloud.quinimbus.binarystore.api.BinaryStoreContext;

module cloud.quinimbus.binarystore.cdi {
    exports cloud.quinimbus.binarystore.cdi;

    uses BinaryStoreContext;

    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.annotations;
    requires cloud.quinimbus.config.api;
    requires cloud.quinimbus.config.cdi;
    requires jakarta.enterprise.cdi.api;
    requires java.annotation;
    requires jakarta.inject.api;
    requires throwing.interfaces;
    requires throwing.streams;
}
