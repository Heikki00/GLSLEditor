#version 430

START_VERTEX

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 texCoord;



out vec2 texCoord0;


void main(){

	gl_Position = vec4(position, 1);
	
	texCoord0 = texCoord.xy;

	
	
}




END_VERTEX



START_FRAGMENT


in vec2 texCoord0;


uniform sampler2D R_gBuffer_0;
uniform sampler2D R_gBuffer_3;
uniform vec3 R_ambient;


out vec4 finalColor;



void main(){
gl_FragDepth = texture(R_gBuffer_3, texCoord0).r;



finalColor = texture(R_gBuffer_0, texCoord0) * vec4(R_ambient, 1);





}





END_FRAGMENT