import cloud.quinimbus.binarystore.persistence.EmbeddableBinaryContextHandler;
import cloud.quinimbus.persistence.api.records.RecordPropertyContextHandler;

open module cloud.quinimbus.binarystore.persistence {
    uses cloud.quinimbus.binarystore.api.BinaryStoreContext;
    uses cloud.quinimbus.config.api.ConfigContext;
    uses cloud.quinimbus.persistence.api.PersistenceContext;

    provides RecordPropertyContextHandler with
            EmbeddableBinaryContextHandler;

    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.annotations;
    requires cloud.quinimbus.common.tools;
    requires cloud.quinimbus.config.api;
    requires cloud.quinimbus.persistence.api;
    requires com.fasterxml.jackson.annotation;
    requires io.soabase.recordbuilder.core;
    requires java.compiler;
    requires org.junit.jupiter.api;
}
