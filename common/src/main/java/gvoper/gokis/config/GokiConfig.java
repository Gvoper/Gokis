package gvoper.gokis.config;

public interface GokiConfig {
    default void validatePostLoad() throws ConfigException {}
}
