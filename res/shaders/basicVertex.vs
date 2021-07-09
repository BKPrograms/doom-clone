// VERTEX SHADER
#version 330

layout (location = 0) in vec3 position;

layout (location = 1) in vec2 texturePosition;

out vec2 texture0;

uniform mat4 transform;

void main(){

    gl_Position = transform * vec4(position, 1.0);

    texture0 = texturePosition;

}