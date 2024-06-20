import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.binarystore.core.BinaryStoreContextImpl;
import cloud.quinimbus.binarystore.core.file.FileBasedBinaryStorageProvider;

module cloud.quinimbus.binarystore.core {
    
    provides BinaryStoreContext with BinaryStoreContextImpl;
    provides BinaryStorageProvider with FileBasedBinaryStorageProvider;
    exports cloud.quinimbus.binarystore.core;
    exports cloud.quinimbus.binarystore.core.file;
    
    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.annotations;
    requires cloud.quinimbus.config.api;
    requires cloud.quinimbus.tools;
    requires com.fasterxml.jackson.databind;
    requires throwing.interfaces;
    requires throwing.lambdas;
    requires throwing.streams;
    requires static lombok;
}
