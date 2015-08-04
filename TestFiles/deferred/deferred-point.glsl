#version 430

START_VERTEX

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;


out vec2 texCoord0;

void main(){
	
	
	
	gl_Position = vec4(position, 1);
	
	
	texCoord0 = texCoord;
	
}

END_VERTEX



START_FRAGMENT

#include "Lighting.glh"


in vec2 texCoord0;



out vec4 finalColor;

uniform PointLight R_pointLight;

uniform sampler2D R_gBuffer_0;
uniform sampler2D R_gBuffer_1;
uniform sampler2D R_gBuffer_2;


uniform vec3 C_eyePos;



void main(){

vec4 normalColor = texture(R_gBuffer_1, texCoord0);
vec4 posColor = texture(R_gBuffer_2, texCoord0);

finalColor =  texture(R_gBuffer_0, texCoord0) *  caclPointLight(R_pointLight, normalColor.xyz, posColor.xyz , C_eyePos,  normalColor.w, posColor.w);


}






END_FRAGMENT


//texture(R_bufferTexture_0, texCoord0) *  caclDirectionalLight(R_directionalLight, texture(R_bufferTexture_1, texCoord0).xyz, (texture(R_bufferTexture_2, texCoord0).xyz * 1000.0));


