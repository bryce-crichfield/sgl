#version 430 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texture_coord;

layout (location = 0) uniform mat4 transform;
out vec4 frag_normal;
void main() 
{
  vec4 filled = vec4(position, 1.0);
  gl_Position = transform * filled;
  frag_normal = vec4(normal, 1.0);

}
