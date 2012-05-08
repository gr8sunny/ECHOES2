
uniform sampler2D tex;
uniform bool textureAvailable;

varying vec2 TexCoord;
varying vec4 Color;

void main()
{	
	if (textureAvailable)
	{
		vec3 texColor = vec3(texture2D(tex, TexCoord.st));
		gl_FragColor = Color*vec4(texColor, 1.0);
	}
	else
	{
		gl_FragColor = Color;
	}
}
