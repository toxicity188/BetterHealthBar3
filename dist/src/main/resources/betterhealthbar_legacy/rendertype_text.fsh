#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform mat4 ProjMat;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
out vec4 fragColor;

//GenerateOtherDefinedMethod
in float applyColor;

bool isEncodeColor(vec4 color) {
    return color.x == 1.0 / 255.0 && color.y == 2.0 / 255.0 && color.z == 3.0 / 255.0;
}

vec4 betterhealthbar_fog_distance(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
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
    fragColor = betterhealthbar_fog_distance(color, vertexDistance, FogStart, FogEnd, FogColor);
}
