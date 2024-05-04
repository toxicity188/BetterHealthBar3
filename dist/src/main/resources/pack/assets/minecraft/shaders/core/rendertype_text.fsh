#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform mat4 ProjMat;

in vec3 Position;
in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in float applyColor;

out vec4 fragColor;

vec4 betterhealthbar_fog_distance(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    vec4 color = texColor * vertexColor * ColorModulator;
    if (applyColor > 0 && texColor.a > 0) {
        color = vec4(texColor.rgb, 1.0) * vertexColor * ColorModulator;
    }
    if (color.a < 0.1) {
        discard;
    }
    fragColor = betterhealthbar_fog_distance(color, vertexDistance, FogStart, FogEnd, FogColor);
}
