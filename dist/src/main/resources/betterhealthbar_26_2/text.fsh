#version 330

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#moj_import <minecraft:fog.glsl>
#endif

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
in float sphericalVertexDistance;
in float cylindricalVertexDistance;
#endif

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

//GenerateOtherDefinedMethod
in float applyColor;

bool isEncodeColor(vec4 color) {
    return color.x == 1.0 / 255.0 && color.y == 2.0 / 255.0 && color.z == 3.0 / 255.0;
}
//GenerateOtherDefinedMethod

void main() {
#ifdef IS_GRAYSCALE
    vec4 texColor = texture(Sampler0, texCoord0).rrrr;
#else
    vec4 texColor = texture(Sampler0, texCoord0);
#endif

#ifdef IS_SEE_THROUGH
    vec4 color = texColor * vertexColor;
#else
    vec4 color = texColor * vertexColor * ColorModulator;
#endif

    //GenerateOtherMainMethod
    if (applyColor > 0 && texColor.a > 0) {
        color = vec4(texColor.rgb, isEncodeColor(texColor) ? 0.0 : 1.0) * vertexColor * ColorModulator;
    }
    //GenerateOtherMainMethod

    if (color.a < 0.1) {
        discard;
    }

#ifdef IS_SEE_THROUGH
    fragColor = color * ColorModulator;
#elif defined(IS_GUI)
    fragColor = color;
#else
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
#endif
}
