import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.core.BinaryStoreContextImpl;

module cloud.quinimbus.binarystore.core {
    
    provides BinaryStoreContext with BinaryStoreContextImpl;
    exports cloud.quinimbus.binarystore.core;
    exports cloud.quinimbus.binarystore.core.file;
    
    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.tools;
    requires com.fasterxml.jackson.databind;
    requires throwing.interfaces;
    requires throwing.lambdas;
    requires throwing.streams;
    requires lombok;
}
