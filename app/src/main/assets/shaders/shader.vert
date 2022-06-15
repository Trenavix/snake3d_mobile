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
uniform bool u_Billboard;
uniform vec3 u_Billboard_Position;
uniform vec2 u_Billboard_Scale;
uniform mediump float u_Billboard_Rotation;
void main()
{
    fragColor = vertColor;
    fragUV = vertUV;
    /*SphereMapping*/
    if(sphereMapping)
    {
        vec4 p = vec4( vertPos, 1. );
        vec3 e = normalize( (mWorld*mView*p).xyz );

        vec3 r = reflect(e, vertNormal);
        float m = 2. *
        sqrt(pow( r.x, 2. ) +
        pow( r.y, 2. ) +
        pow( r.z + 1., 2. ));
        vN = r.xy / m + .5;
        fragUV = vN;
    }

    /*Texture scrolling*/
    if(u_TexScrollS != 0.0)
        fragUV.s += (u_TexScrollS * u_Time);
    if(u_TexScrollT != 0.0)
        fragUV.t += (u_TexScrollT * u_Time);

    vec3 CameraRight_worldspace = vec3(mView[0][0], mView[1][0], mView[2][0]);
    vec3 CameraUp_worldspace = vec3(mView[0][1], mView[1][1], mView[2][1]);
    vec2 rotationVector = vec2(cos(u_Time/50.0), sin(u_Time/50.0));
    fragNormal = (mWorld * vec4(vertNormal, 0.0)).xyz;

    if(u_Billboard)
    {
        vec3 p = vertPos;
        float new_x = p.x*cos(u_Billboard_Rotation) - p.y*sin(u_Billboard_Rotation);
        float new_y = p.y*cos(u_Billboard_Rotation) + p.x*sin(u_Billboard_Rotation);

        vec3 vertexPosition_worldspace =
        u_Billboard_Position +
        (CameraRight_worldspace * new_x * u_Billboard_Scale.x) +
        (CameraUp_worldspace * new_y * u_Billboard_Scale.y);
        fragPosition = (mWorld * vec4(vertexPosition_worldspace, 1.0)).xyz;
        gl_Position = mProj * mView * mWorld * vec4(vertexPosition_worldspace, 1.0);
    }
    else
    {
        fragPosition = (mWorld * vec4(vertPos, 1.0)).xyz;
        gl_Position = mProj * mView * mWorld * vec4(vertPos, 1.0);
    }

}
