package net.iamaprogrammer.config.core;

import com.google.gson.JsonSyntaxException;
import net.iamaprogrammer.WaxItemFrames;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class ConfigRegistry<C extends Config> {
    private final Class<C> config;
    private final C defaultConfig;

    public ConfigRegistry(C defaultConfig, Class<C> config) {
        this.config = config;
        this.defaultConfig = defaultConfig;
    }
    public C register() {
        Path configLocation = ConfigUtil.findOrCreateConfig(this.defaultConfig);
        try {
            return ConfigUtil.GSON.fromJson(new FileReader(configLocation.toString()), this.config);
        } catch (FileNotFoundException e) {
            WaxItemFrames.LOGGER.error("Config File Not Found. Falling back to default config.");
            return this.defaultConfig;
        } catch (JsonSyntaxException e) {
            WaxItemFrames.LOGGER.error("Invalid Json Syntax. Falling back to default config.");
            return this.defaultConfig;
        }
    }
}
