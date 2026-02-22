#version 150

uniform sampler2D InSampler;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord0);

    if (color.a != 1.0) {
        color.a = 1.0;
    }

    fragColor = color;
}