package kr.toxicity.healthbar.api.configuration;

public record CoreShadersOption(
        boolean renderTypeVertex,
        boolean renderTypeFragment
) {
    public static final CoreShadersOption DEFAULT = new CoreShadersOption(true, true);
}
