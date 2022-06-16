precision mediump float;
const int MAX_POINT_LIGHTS = 8;
uniform float alpha_Threshold;
uniform sampler2D u_Texture;
varying vec2 vN;
uniform highp float u_MatSpecularIntensity;
uniform highp float u_Shininess;
uniform lowp vec4 u_EnvColor;
varying lowp vec4 vertFragColor;
varying lowp vec2 fragUV;
varying lowp vec3 fragNormal;
varying lowp vec3 fragPosition;
varying lowp vec3 u_LightPositions[MAX_POINT_LIGHTS];
struct Light
{
    lowp vec3 diffuseColor;
    lowp vec3 ambientColor;
    lowp vec3 direction;
};
struct Attenuation
{
    float constant;
    float linear;
    float exp;
};
struct PointLight
{
    Light baseLight;
    vec3 fragLightPos;
    Attenuation atten;
};
uniform Light u_Light;
uniform int u_PointLightCount;
/*8 Pt Lights max defined above*/
uniform PointLight u_PointLights[MAX_POINT_LIGHTS];

vec3 calcDirLight(Light light, vec3 normal)
{
    lowp float diffuseFactor = max(-dot(normal, light.direction), 0.0);
    lowp vec3 diffuseColor = u_Light.diffuseColor * diffuseFactor;
    lowp vec3 eye = normalize(fragPosition);
    lowp vec3 reflection = reflect(light.direction, normal);
    lowp float specularFactor = pow(max(0.0, -dot(reflection, eye)), u_Shininess);
    mediump vec3 specularColor = u_Light.ambientColor * u_MatSpecularIntensity * specularFactor;
    return (u_Light.ambientColor + diffuseColor + specularColor);
}

vec3 calcPointLight(int idx, vec3 normal)
{
    PointLight light = u_PointLights[idx];
    lowp vec3 lightDir = normalize(light.fragLightPos - fragPosition);
    /* diffuse shading */
    lowp float diff = max(dot(normal, lightDir), 0.0);
    /* specular shading */
    lowp vec3 eye = normalize(fragPosition);
    lowp vec3 reflectDir = reflect(-lightDir, normal);
    lowp float spec = pow(max(dot(eye, reflectDir), 0.0), u_Shininess);
    /* attenuation */
    lowp float distance = length(light.fragLightPos - fragPosition);
    lowp float attenuation =
        1.0 / (light.atten.constant +
        light.atten.linear * distance +
        light.atten.exp * (distance * distance));
    /* combine results */
    lowp vec3 ambient  = u_PointLights[idx].baseLight.ambientColor;
    lowp vec3 diffuse  = u_PointLights[idx].baseLight.diffuseColor;
    lowp vec3 specular = u_PointLights[idx].baseLight.ambientColor * u_MatSpecularIntensity;
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}

void main()
{
    /*Ambient/Diffuse*/
    lowp vec3 normal = normalize(fragNormal);
    mediump vec3 totalLight  = calcDirLight(u_Light, normal);
    for (int i=0; i<u_PointLightCount; i++)
        totalLight += calcPointLight(i, normal);

    gl_FragColor =
        vertFragColor * texture2D(u_Texture, fragUV) *
        vec4(totalLight,1.0) *
        u_EnvColor;
    if(gl_FragColor.a <= alpha_Threshold)
        discard; /*AlphaTest*/
}

