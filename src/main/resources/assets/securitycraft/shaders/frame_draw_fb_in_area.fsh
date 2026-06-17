#version 330

uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);

    if (color.a != 1.0) {
        color.a = 1.0;
    }

    fragColor = color;
}