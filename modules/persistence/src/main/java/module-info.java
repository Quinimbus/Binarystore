module cloud.quinimbus.binarystore.persistence {
    uses cloud.quinimbus.binarystore.api.BinaryStoreContext;
    uses cloud.quinimbus.config.api.ConfigContext;

    exports cloud.quinimbus.binarystore.persistence;

    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.tools;
    requires cloud.quinimbus.config.api;
    requires cloud.quinimbus.persistence.api;
    requires com.fasterxml.jackson.annotation;
    requires io.soabase.recordbuilder.core;
    requires java.compiler;
}
