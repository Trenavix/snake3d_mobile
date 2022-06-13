attribute vec3 vertPos;
attribute vec2 vertUV;
attribute vec4 vertColor;
attribute vec3 vertNormal;
varying vec4 fragColor;
varying vec2 fragUV;
varying lowp vec3 fragNormal;
varying lowp vec3 fragPosition;
varying vec2 vN;
uniform mat4 mWorld;
uniform mat4 mView;
uniform mat4 mProj;
uniform bool sphereMapping;
uniform lowp float u_TexScrollS;
uniform lowp float u_TexScrollT;
uniform mediump float u_Time;
void main()
{
    fragColor = vertColor;
    fragUV = vertUV;
    /*SphereMapping*/
    vec4 p = vec4( vertPos, 1. );
    vec3 e = normalize( (mWorld*mView*p).xyz );

    vec3 r = reflect(e, vertNormal);
    float m = 2. *
    sqrt(pow( r.x, 2. ) +
    pow( r.y, 2. ) +
    pow( r.z + 1., 2. ));
    vN = r.xy / m + .5;
    if(sphereMapping)
        fragUV = vN;

    /*Texture scrolling*/
    if(u_TexScrollS != 0.0)
        fragUV.s += (u_TexScrollS * u_Time);
    if(u_TexScrollT != 0.0)
        fragUV.t += (u_TexScrollT * u_Time);

    fragNormal = (mWorld * vec4(vertNormal, 0.0)).xyz;
    fragPosition = (mWorld * vec4(vertPos, 1.0)).xyz;
    gl_Position = mProj * mView * mWorld * vec4(vertPos, 1.0);
}
