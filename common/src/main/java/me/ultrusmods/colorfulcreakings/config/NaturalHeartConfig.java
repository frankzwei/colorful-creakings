package me.ultrusmods.colorfulcreakings.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ultrusmods.colorfulcreakings.Constants;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;

public final class NaturalHeartConfig {

    private static final Path file = Path.of("config", "colorfulcreakings-natural-hearts.json");
    private static final NaturalHeartConfig instance = load(file);

    private final EnumMap<CreakingColor, Integer> weights;
    private final int totalWeight;

    private NaturalHeartConfig(EnumMap<CreakingColor, Integer> weights) {
        this.weights = weights;

        long sum = 0;
        for (CreakingColor color : CreakingColor.values()) {
            sum += weights.get(color);
        }

        if (sum > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total natural heart color weights are too large");
        }

        this.totalWeight = (int) sum;
    }

    public static CreakingColor choose(RandomSource random) {
        if (instance.totalWeight == 0) {
            return CreakingColor.ORANGE;
        }

        int roll = random.nextInt(instance.totalWeight);
        for (CreakingColor color : CreakingColor.values()) {
            roll -= instance.weights.get(color);
            if (roll < 0) {
                return color;
            }
        }

        return CreakingColor.ORANGE;
    }

    private static EnumMap<CreakingColor, Integer> defaultWeights() {
        EnumMap<CreakingColor, Integer> result = new EnumMap<>(CreakingColor.class);
        for (CreakingColor color : CreakingColor.values()) {
            result.put(color, color == CreakingColor.ORANGE ? 40 : 4);
        }

        return result;
    }

    private static NaturalHeartConfig load(Path file) {
        EnumMap<CreakingColor, Integer> weights = defaultWeights();

        if (!Files.exists(file)) {
            Constants.LOG.info("No natural-heart config at {}; using built-in color weights (feature enabled)",
                file.toAbsolutePath());
            return new NaturalHeartConfig(weights);
        }

        try {
            if (!Files.isRegularFile(file)) {
                throw new IOException("Configuration path is not a regular file");
            }

            JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject overrides = root.getAsJsonObject("weights");
            if (overrides == null) {
                throw new IllegalArgumentException("Missing 'weights' object");
            }

            // Missing colors keep their built-in default weight.
            for (CreakingColor color : CreakingColor.values()) {
                String name = color.getSerializedName();
                if (!overrides.has(name)) {
                    continue;
                }

                JsonElement element = overrides.get(name);
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
                    throw new IllegalArgumentException("Weight for '" + name + "' must be a nonnegative integer");
                }

                int value = new BigDecimal(element.getAsString()).intValueExact();
                if (value < 0) {
                    throw new IllegalArgumentException("Negative weight for '" + name + "'");
                }

                weights.put(color, value);
            }

            NaturalHeartConfig config = new NaturalHeartConfig(weights);
            Constants.LOG.info("Loaded natural-heart color weights from {} (total weight {})", file.toAbsolutePath(), config.totalWeight);
            if (config.totalWeight == 0) {
                Constants.LOG.warn("All natural-heart color weights are zero; falling back to vanilla orange hearts");
            }

            return config;
        } catch (IOException | RuntimeException ex) {
            Constants.LOG.error("Could not read natural-heart config at {}; using built-in color weights", file.toAbsolutePath(), ex);
            return new NaturalHeartConfig(defaultWeights());
        }
    }
}