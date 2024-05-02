#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform vec2 ScreenSize;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out float applyColor;

#define HEIGHT 8192.0 / 40.0

void main() {
    vec3 pos = vec3(Position);

    applyColor = 0;
    if (ProjMat[3].x != -1) {
        vec4 texColor = texture(Sampler0, UV0);
        mat4 invModelViewMat = inverse(ModelViewMat);
        vec3 location = normalize(vec3(invModelViewMat[2]));
        float pitch = asin(-location.y);
        float yaw = atan(location.x, -location.z);

        float x = abs(pos.x) + abs(ModelViewMat[3].x);
        float y = abs(pos.y) + abs(ModelViewMat[3].y);
        float z = abs(pos.z) + abs(ModelViewMat[3].z);

        float length1 = -cos(pitch) * y;
        float length2 = sin(pitch) * sqrt(pow(x, 2.0) + pow(z, 2.0));
        if (abs(length1 - length2) >= HEIGHT / 2 || abs(length1 + length2) >= HEIGHT / 2) {
            float alpha = (1 - texColor.a) / 10;
            if (alpha > 0) {
                applyColor = 1;
                float pitchAdd = cos(pitch - 3.1415 / 2) * HEIGHT - alpha - 0.01;
                pos.y += cos(pitch) * HEIGHT;
                pos.x += sin(yaw) * pitchAdd;
                pos.z -= cos(yaw) * pitchAdd;
            }
        }
    }

    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
}
