open module cloud.quinimbus.binarystore.api {
    exports cloud.quinimbus.binarystore.api;
    exports cloud.quinimbus.binarystore.api.storage;

    requires cloud.quinimbus.config.api;
    requires static lombok;
    requires throwing.streams;
}
