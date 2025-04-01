package net.iamaprogrammer.config.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.iamaprogrammer.WaxItemFrames;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtil {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

    public static <C extends Config> boolean save(C config) {
        Path path = Path.of(ConfigUtil.CONFIG_PATH.toString(), config.fileName()+".json");
        if (!Files.exists(path)) {
            try {
                createFileWithDirectories(path, config);
                return true;
            } catch (IOException e) {
                WaxItemFrames.LOGGER.error("Could not create config.");
            }
        } else {
            try {
                createFile(path, config);
                return true;
            } catch (IOException e) {
                WaxItemFrames.LOGGER.error("Could not create config.");
            }
        }
        return false;
    }
    public static <C extends Config> C load(Path configPath, Class<C> config) {
        try {
            return GSON.fromJson(new FileReader(configPath.toString()), config);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static <C extends Config> void createFileWithDirectories(Path path, C config) throws IOException {
        Files.createDirectories(path.getParent());
        createFile(path, config);
    }
    public static <C extends Config> void createFile(Path path, C config) throws IOException {
        String data = GSON.toJson(config != null ? config : "");
        FileWriter fooWriter = new FileWriter(path.toFile(), false); // true to append
        fooWriter.write(data);
        fooWriter.close();
    }

    public static <C extends Config> Path findOrCreateConfig(C defaultConfig) {
        Path path = Path.of(ConfigUtil.CONFIG_PATH.toString(), defaultConfig.fileName()+".json");
        if (!Files.exists(path)) {
            try {
                ConfigUtil.createFileWithDirectories(path, defaultConfig);
            } catch (IOException e) {
                WaxItemFrames.LOGGER.error("Could not create config.");
            }
        }
        return path;
    }
}
