#version 430

START_VERTEX

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

out vec2 texCoord0;


void main(){

	gl_Position = vec4(position, 1);
	
	texCoord0 = texCoord;


	
}




END_VERTEX



START_FRAGMENT


in vec2 texCoord0;


uniform sampler2D diffuse;



uniform sampler2D R_gBuffer_3T;




out vec4 finalColor;
 


void main(){

gl_FragDepth = texture(R_gBuffer_3T, texCoord0).r;



finalColor = vec4(texture(diffuse, texCoord0));


}





END_FRAGMENT