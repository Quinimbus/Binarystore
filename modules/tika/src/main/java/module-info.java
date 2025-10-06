import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.binarystore.tika.TikaContentTypeDetector;

module cloud.quinimbus.binarystore.tika {
    provides ContentTypeDetector with
            TikaContentTypeDetector;

    requires cloud.quinimbus.binarystore.api;
    requires cloud.quinimbus.common.annotations;
    requires org.apache.tika.core;
}
