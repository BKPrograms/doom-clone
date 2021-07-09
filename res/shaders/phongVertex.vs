# version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texturePosition;
layout (location = 2) in vec3 normalPos;

out vec2 texture0;
out vec3 normal0;
out vec3 worldPos0;

uniform mat4 transform; // "Global" matrix

uniform mat4 transformProjected; // "POV" matrix

void main(){

    gl_Position = transformProjected * vec4(position, 1.0);

    texture0 = texturePosition;

    normal0 = (transform * vec4(normalPos, 0.0)).xyz; // "Swizzling"

    worldPos0 = (transform * vec4(position, 1.0)).xyz;

}