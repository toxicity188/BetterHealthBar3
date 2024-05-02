#version 150

#moj_import <fog.glsl>

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

#define HEIGHT 8192.0 / 40.0

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    vec4 color = texColor * vertexColor * ColorModulator;
    if (applyColor > 0) {
        if (texColor.rgb == vec3(0)) discard;
        color = vec4(texColor.rgb, 1.0) * vertexColor * ColorModulator;
    }
    if (color.a < 0.1) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
