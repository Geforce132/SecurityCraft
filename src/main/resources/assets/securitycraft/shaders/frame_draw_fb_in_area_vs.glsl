#version 120

in vec3 Position;
in vec2 UV0;

uniform mat4 modelView;
uniform mat4 projection;

out vec2 texCoord0;

void main(){
    gl_Position = projection * modelView * vec4(Position, 1.0);
    texCoord0 = UV0;
}