


END_VERTEX



START_FRAGMENT

in vec2 texCoord0;
in vec3 color;

uniform sampler2D diffuse;
uniform float opacity;


out vec4 finalColor;

void main(){

finalColor = texture(diffuse, texCoord0);
//finalColor = vec4(color, 1);
}







END_FRAGMENT






