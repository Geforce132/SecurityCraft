#version 330

uniform sampler2D InSampler;

layout(std140) uniform BackgroundColor {
    float r;
    float g;
    float b;
};

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord0);

    if (color.a == 0.0) {
        color = vec4(r, g, b, 1.0);
    }

    fragColor = color;
}