package kr.toxicity.healthbar.api.configuration;

public record CoreShadersOption(
        boolean renderTypeVertex,
        boolean renderTypeFragment,
        boolean renderTypeJson
) {
    public static final CoreShadersOption DEFAULT = new CoreShadersOption(true, true, true);
}
