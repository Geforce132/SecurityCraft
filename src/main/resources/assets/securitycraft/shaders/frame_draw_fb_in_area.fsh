#version 150

uniform sampler2D InSampler;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    fragColor = texture(InSampler, texCoord0);
}