#version 330 core

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D tex;

void main()
{
//    vec4 texSample = texture(tex, texCoord);
//    if (texSample.a < 0.02) {
//        discard;
//    }
//
//    fragColor = texSample * vec4(vertexColor.rgb, 1);

    fragColor = vertexColor;
}