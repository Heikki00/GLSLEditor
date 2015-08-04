#version 430

START_VERTEX

in vec3 position;


uniform mat4 T_MVP;


void main(){S

	
	gl_Position = T_MVP * vec4(position, 1);

}

END_VERTEX



START_FRAGMENT

out vec4 finalColor;

void main(){
	
	
	float depth = gl_FragCoord.z;
	

	finalColor = vec4(depth);


}






END_FRAGMENT









