#version 430

START_VERTEX

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 texCoord;


out vec2 texCoord0;
out vec4 shadowMapCoords0;




void main(){
	
	
	
	gl_Position = vec4(position, 1);
	
	
	texCoord0 =  texCoord.xy;
	
	
	
	
	
}

END_VERTEX




START_FRAGMENT

#include "Lighting"
#include "Sampling"

in vec2 texCoord0;



out vec4 finalColor;

uniform DirectionalLight R_directionalLight;

uniform sampler2D R_gBuffer_0;
uniform sampler2D R_gBuffer_1;
uniform sampler2D R_gBuffer_2;
uniform sampler2D R_gBuffer_3;



uniform vec3 C_eyePos;



void main(){
gl_FragDepth = texture(R_gBuffer_3, texCoord0).r;




vec4 normalColor = texture(R_gBuffer_1, texCoord0);
vec4 posColor = texture(R_gBuffer_2, texCoord0);





	vec4 lightAmt = caclDirectionalLight(R_directionalLight, normalColor.xyz, posColor.xyz , C_eyePos,  normalColor.w, posColor.w);
		
		
		
		finalColor =  texture(R_gBuffer_0, texCoord0) * lightAmt ;
	





}






END_FRAGMENT



