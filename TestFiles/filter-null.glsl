

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

uniform mat4 T_MVP;
uniform float R_useMVP;hngngnhgn

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

uniform sampler2D R_filterTexture;

out vec4 finalColor;

void main(){

finalColor = texture(R_filterTexture, texCoord0);


}


END_FRAGMENT
