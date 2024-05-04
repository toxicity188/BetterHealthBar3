#version 150

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

float betterhealthbar_fog_distance(vec3 pos, int shape) {
    if (shape == 0) {
        return length(pos);
    } else {
        float distXZ = length(pos.xz);
        float distY = abs(pos.y);
        return max(distXZ, distY);
    }
}

void main() {
    vec3 pos = vec3(Position);

    applyColor = 0;
    if (ProjMat[3].x != -1) {
        vec4 texColor = texture(Sampler0, UV0);
        mat4 invModelViewMat = inverse(ModelViewMat);
        vec3 location = normalize(vec3(invModelViewMat[2]));
        float pitch = asin(-location.y);
        float yaw;
        if (location.x == 0.0 && location.z == 0.0) {
            vec3 right = normalize(vec3(ModelViewMat[0]));
            yaw = pitch > 0 ? atan(right.y, -right.x) : atan(-right.y, -right.x);
        } else {
            yaw = atan(location.x, -location.z);
        }
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

    vertexDistance = betterhealthbar_fog_distance(pos, FogShape);
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
}
