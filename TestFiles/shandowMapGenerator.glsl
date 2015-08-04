#version 430

START_VERTEX

in vec3 position;


uniform mat4 T_MVP;


void main(){

	
	gl_Position = T_MVP * vec4(position, 1);

}

END_VERTEX



START_FRAGMENT

out vec4 finalColor;

void main(){
	
	
	float depth = gl_FragCoord.z;
	
	
	
	
	
	float dx = dFdx(depth);
	float dy = dFdy(depth);
	
	float moment2  = depth * depth + 0.25 * (dx*dx + dy*dy);
	
	finalColor = vec4(depth, moment2, 0.0, 0.0);


}





END_FRAGMENT










