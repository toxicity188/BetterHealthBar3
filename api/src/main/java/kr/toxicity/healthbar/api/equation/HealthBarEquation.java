package kr.toxicity.healthbar.api.equation;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface HealthBarEquation {
    @NotNull Vector evaluate(double t);
}
