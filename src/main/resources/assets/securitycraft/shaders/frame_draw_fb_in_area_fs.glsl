#version 120

uniform sampler2D sampler;

in vec2 texCoord0;

void main() {
    gl_FragColor = texture2D(sampler, texCoord0);
}