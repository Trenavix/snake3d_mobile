precision mediump float;
uniform float alpha_Threshold;
uniform sampler2D u_Texture;
varying vec2 vN;
uniform highp float u_MatSpecularIntensity;
uniform highp float u_Shininess;
varying lowp vec4 fragColor;
varying lowp vec2 fragUV;
varying lowp vec3 fragNormal;
varying lowp vec3 fragPosition;
struct Light
{
    lowp vec3 diffuseColor;
    lowp vec3 ambientColor;
    lowp vec3 direction;
};
uniform Light u_Light;
void main()
{
    /*Ambient/Diffuse*/
    lowp vec3 normal = normalize(fragNormal);
    lowp float diffuseFactor = max(-dot(normal, u_Light.direction), 0.0);
    lowp vec3 diffuseColor = u_Light.diffuseColor * diffuseFactor;
    lowp vec3 eye = normalize(fragPosition);
    lowp vec3 reflection = reflect(u_Light.direction, normal);
    lowp float specularFactor = pow(max(0.0, -dot(reflection, eye)), u_Shininess);
    highp vec3 specularColor = u_Light.diffuseColor * u_MatSpecularIntensity * specularFactor;
    gl_FragColor =
        fragColor * texture2D(u_Texture, fragUV)
        * vec4(u_Light.ambientColor + diffuseColor + specularColor, 1.0);
    if(gl_FragColor.a <= alpha_Threshold)
        discard; /*AlphaTest*/
}
