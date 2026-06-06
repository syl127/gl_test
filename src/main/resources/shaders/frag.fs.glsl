#version 330 core

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

uniform float overlayOpacity;

uniform sampler2D tex;
uniform sampler2D tex1;

void main()
{
    //FragColor = ourColor;
    //FragColor = vertexColor;
    //FragColor = vec4(vertexPos.xyz, 0.5);

    //FragColor = texture(tex, TexCoord) * vec4(vertexColor.rgb, 1);
    //vec4 overlay = texture(tex1, texCoord);
    //fragColor = mix(texture(tex, texCoord), overlay, overlayOpacity * overlay.a) * vec4(1, 1, 1, 0.8);// * vec4(vertexColor.rgb, 1);
    vec4 texSample = texture(tex, texCoord);
    if (texSample.a < 0.02) {
        discard;
    }

    fragColor = texSample * vec4(vertexColor.rgb, 1);
}