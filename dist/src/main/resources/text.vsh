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
uniform vec3 ChunkOffset;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out float applyColor;

#define DISPLAY_HEIGHT 8192.0 / 40.0

float betterhealthbar_fog_distance(vec3 pos, int shape) {
    if (shape == 0) {
        return length(pos);
    } else {
        float distXZ = length(pos.xz);
        float distY = abs(pos.y);
        return max(distXZ, distY);
    }
}

//GenerateOtherDefinedMethod
vec3 betterhealthbar_rotate(vec3 v, vec4 q) {
    vec3 u = q.xyz;
    float s = q.w;
    return 2.0 * dot(u, v) * u + (s * s - dot(u, u)) * v + 2.0 * s * cross(u, v);
}

vec4 betterhealthbar_toQuaternion(float roll, float pitch, float yaw) {
    float cr = cos(roll * 0.5);
    float sr = sin(roll * 0.5);
    float cp = cos(pitch * 0.5);
    float sp = sin(pitch * 0.5);
    float cy = cos(yaw * 0.5);
    float sy = sin(yaw * 0.5);
    return vec4(sr * cp * cy - cr * sp * sy, cr * sp * cy + sr * cp * sy, cr * cp * sy - sr * sp * cy, cr * cp * cy + sr * sp * sy);
}
//GenerateOtherDefinedMethod

void main() {
    vec3 pos = vec3(Position);

    applyColor = 0;

    //GenerateOtherMainMethod
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
        if (length(Position + ChunkOffset - vec3(invModelViewMat[3])) >= (DISPLAY_HEIGHT / 2)) {
            float alpha = texColor.a;
            if (alpha < 1) {
                applyColor = 1;
                vec3 add = betterhealthbar_rotate(vec3(0, DISPLAY_HEIGHT, alpha / 40), betterhealthbar_toQuaternion(pitch, yaw, 0.0));
                pos.x += add.x;
                pos.y += add.y;
                pos.z -= add.z;
            }
        }
    }
    //GenerateOtherMainMethod

    vertexDistance = betterhealthbar_fog_distance(pos, FogShape);
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
}
