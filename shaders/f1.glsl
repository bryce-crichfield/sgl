#version 430 core
in vec4 frag_normal;
out vec4 gl_FragColor;
void main() 
{
  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0) * frag_normal;
}
