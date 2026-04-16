#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
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
    vec4 texColor = texture(Sampler0, texCoord0);
    vec4 color = texColor * vertexColor * ColorModulator;

    //GenerateOtherMainMethod
    if (applyColor > 0 && texColor.a > 0) {
        color = vec4(texColor.rgb, isEncodeColor(texColor) ? 0.0 : 1.0) * vertexColor * ColorModulator;
    }
    //GenerateOtherMainMethod

    if (color.a < 0.1) {
        discard;
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
