#version 330

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:sample_lightmap.glsl>
#endif

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
in ivec2 UV2;
#endif

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;
#endif

uniform vec3 ChunkOffset;

out vec4 vertexColor;
out vec2 texCoord0;
out float applyColor;

#define DISPLAY_HEIGHT 8192.0 / 40.0

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
    vec3 pos = Position;
    applyColor = 0;

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
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
        if (length(pos + ChunkOffset - vec3(invModelViewMat[3])) >= (DISPLAY_HEIGHT / 2)) {
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
    sphericalVertexDistance = fog_spherical_distance(pos);
    cylindricalVertexDistance = fog_cylindrical_distance(pos);
    vertexColor = Color * sample_lightmap(Sampler2, UV2);
#else
    vertexColor = Color;
#endif
    texCoord0 = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
}
