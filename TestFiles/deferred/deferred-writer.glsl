 #version 430


// #ifdef VS

// layout(location = 0) in vec3 position0;
// layout(location = 1) in vec2 texCoord0;
// layout(location = 2) in vec3 normal;
// layout (location = 3) in vec3 tanget;

// uniform mat4 T_MVP;
// uniform mat4 T_model;

// out vec2 texCoord;
// out vec3 worldPos;

START_VERTEX

layout(location = 0) in vec3 position0;
layout(location = 1) in vec3 texCoord0;
layout(location = 2) in vec3 normal;
layout (location = 3) in vec3 tanget;

uniform mat4 T_MVP;
uniform mat4 T_model;

out vec2 texCoord;
out vec3 worldPos;
out mat3 tbnMatrix;

void main(){

	gl_Position = T_MVP * vec4(position0, 1);
	
	texCoord = texCoord0.xy;
	
	worldPos = (T_model * vec4(position0, 1)).xyz;
	
	vec3 n = normalize((T_model * vec4(normal, 0.0)).xyz);
	vec3 t = normalize((T_model * vec4(tanget, 0.0)).xyz);
	
	t = normalize(t - (dot(t,n)) * n);
	
	
	vec3 biTanget = (cross(t,n));
	
	tbnMatrix = mat3(t,biTanget, n);
	
	
	
}




END_VERTEX





START_FRAGMENT

#include "Sampling.glh"

//layout(early_fragment_tests) in;

in vec2 texCoord;
in vec3 worldPos;
in mat3 tbnMatrix;

uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;

uniform float dispMapBias;
uniform float dispMapScale;

uniform float specularIntensity;
uniform float specularPower;

uniform float opacity;

uniform vec3 C_eyePos;

layout (location = 0) out vec4 diffuseColor;
layout (location = 1) out vec4 normalColor;
layout (location = 2) out vec4 positionColor;
layout (location = 3) out float depthColor;

void main(){

vec2 texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, normalize(C_eyePos - worldPos), texCoord, dispMapScale, dispMapBias);



vec3 n = normalize(tbnMatrix * (255.0/128.0 * texture(normalMap, texCoords).xyz - 1)); 



diffuseColor = vec4(texture(diffuse, texCoords).xyz, opacity);

normalColor = vec4(n, specularIntensity);

positionColor = vec4(worldPos, specularPower);

depthColor = gl_FragCoord.z;
	
	


}





END_FRAGMENT








































