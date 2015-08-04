#version 430
cccccccc
START_VERTEX

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

uniform mat4 T_MVP;
uniform float R_useMVP;

out vec2 texCoord0;

void main(){
	texCoord0 = texCoord;
	
	if(R_useMVP == 1.0)
	gl_Position = T_MVP * vec4(position, 1);

	else
	gl_Position = vec4(position, 1);
	
}

END_VERTEX



START_FRAGMENT

in vec2 texCoord0;

uniform vec3 R_blurScale;
uniform sampler2D R_filterTexture;

out vec4 finalColor;

void main(){

vec4 color = vec4(0.0);

color += texture(R_filterTexture, texCoord0 + (vec2(-3.0) * R_blurScale.xy)) * (1.0 / 64.0)	;
color += texture(R_filterTexture, texCoord0 + (vec2(-2.0) * R_blurScale.xy)) * (6.0 / 64.0)	;
color += texture(R_filterTexture, texCoord0 + (vec2(-1.0) * R_blurScale.xy)) * (15.0 / 64.0);
color += texture(R_filterTexture, texCoord0 + (vec2(0.0) * R_blurScale.xy))  * (20.0 / 64.0);
color += texture(R_filterTexture, texCoord0 + (vec2(1.0) * R_blurScale.xy))  * (15.0 / 64.0);
color += texture(R_filterTexture, texCoord0 + (vec2(2.0) * R_blurScale.xy))  * (6.0 / 64.0)	;
color += texture(R_filterTexture, texCoord0 + (vec2(3.0) * R_blurScale.xy))  * (1.0 / 64.0)	;

finalColor = color;
//finalColor = texture(R_filterTexture, texCoord0);


}






END_FRAGMENT






