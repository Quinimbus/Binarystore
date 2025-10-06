import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.binarystore.core.BinaryStoreContextImpl;
import cloud.quinimbus.binarystore.core.content.JavaContentTypeDetector;
import cloud.quinimbus.binarystore.core.content.NoopContentTypeDetector;
import cloud.quinimbus.binarystore.core.file.FileBasedBinaryStorageProvider;

module cloud.quinimbus.binarystore.core {
    uses cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
    uses cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
    uses cloud.quinimbus.config.api.ConfigContext;

    provides BinaryStoreContext with
            BinaryStoreContextImpl;
    provides BinaryStorageProvider with
            FileBasedBinaryStorageProvider;
    provides ContentTypeDetector with
            JavaContentTypeDetector,
            NoopContentTypeDetector;

    exports cloud.quinimbus.binarystore.core;
    exports cloud.quinimbus.binarystore.core.file;

    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.annotations;
    requires cloud.quinimbus.common.tools;
    requires cloud.quinimbus.config.api;
    requires cloud.quinimbus.tools;
    requires com.fasterxml.jackson.databind;
    requires throwing.interfaces;
    requires throwing.lambdas;
    requires throwing.streams;
    requires static lombok;
}
