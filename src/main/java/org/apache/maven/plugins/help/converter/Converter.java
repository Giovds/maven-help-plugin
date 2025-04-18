package org.apache.maven.plugins.help.converter;

/**
 * A converter interface for transforming text.
 */
public interface Converter {
    String convert(String text);
}
