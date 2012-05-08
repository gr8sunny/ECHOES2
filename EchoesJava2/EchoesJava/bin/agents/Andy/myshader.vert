
uniform mat4 Transforms[24];
uniform bool textureAvailable;
uniform vec4 AmbientMaterial;
uniform vec4 DiffuseMaterial;
uniform vec4 SpecularMaterial;
uniform vec4 MorphWeights;

attribute vec4 Vertex;
attribute vec3 Normal;
attribute vec4 Weights;
attribute vec4 MatrixIndices;
attribute vec2 TextureCoordinates;

attribute vec3 MorphPosition1;
attribute vec3 MorphNormal1;
attribute vec3 MorphPosition2;
attribute vec3 MorphNormal2;
attribute vec3 MorphPosition3;
attribute vec3 MorphNormal3;
attribute vec3 MorphPosition4;
attribute vec3 MorphNormal4;

varying vec4 Color;
varying vec2 TexCoord;

const vec4 AMBIENT_BLACK = vec4(0.0, 0.0, 0.0, 1.0);
const vec4 DEFAULT_BLACK = vec4(0.0, 0.0, 0.0, 0.0);

bool isLightEnabled(in int i)
	{
	    // A separate variable is used to get
	    // rid of a linker error.
	    bool enabled = true;

	    // If all the colors of the Light are set
	    // to BLACK then we know we don't need to bother
	    // doing a lighting calculation on it.
	    if ((gl_LightSource[i].ambient  == AMBIENT_BLACK) &&
	        (gl_LightSource[i].diffuse  == DEFAULT_BLACK) &&
	        (gl_LightSource[i].specular == DEFAULT_BLACK))
	        enabled = false;

	    return(enabled);
	}

void DirectionalLight(in int i, in vec3 normal, inout vec4 ambient, inout vec4 diffuse, inout vec4 specular)
{
	float nDotVP = max(0.0, dot(normal, normalize(vec3(gl_LightSource[i].position))));
	
	float nDotHV = max(0.0, dot(normal, vec3(gl_LightSource[i].halfVector)));

	float pf;	
	if (nDotVP == 0.0)
		pf = 0.0;
	else
		pf = pow(nDotHV, gl_FrontMaterial.shininess);

	ambient  += gl_LightSource[i].ambient;
	diffuse  += gl_LightSource[i].diffuse*nDotVP;
	specular += gl_LightSource[i].specular*pf;
}

void PointLight(in int i,
                in vec3 eye,
                in vec3 ecPosition3,
                in vec3 normal,
                inout vec4 ambient,
                inout vec4 diffuse,
                inout vec4 specular)
{
    float nDotVP;         // normal . light direction
    float nDotHV;         // normal . light half vector
    float pf;             // power factor
    float attenuation;    // computed attenuation factor
    float d;              // distance from surface to light source
    vec3  VP;             // direction from surface to light position
    vec3  halfVector;     // direction of maximum highlights

    // Compute vector from surface to light position
    VP = vec3(gl_LightSource[i].position) - ecPosition3;

    // Compute distance between surface and light position
    d = length(VP);

    // Normalize the vector from surface to light position
    VP = normalize(VP);

    // Compute attenuation
    attenuation = 1.0 / (gl_LightSource[i].constantAttenuation +
                         gl_LightSource[i].linearAttenuation * d +
                         gl_LightSource[i].quadraticAttenuation * d * d);

    halfVector = normalize(VP + eye);

    nDotVP = max(0.0, dot(normal, VP));
    nDotHV = max(0.0, dot(normal, halfVector));

    if (nDotVP == 0.0)
        pf = 0.0;
    else
        pf = pow(nDotHV, gl_FrontMaterial.shininess);

    ambient += gl_LightSource[i].ambient * attenuation;
    diffuse += gl_LightSource[i].diffuse * nDotVP * attenuation;
    specular += gl_LightSource[i].specular * pf * attenuation;
}

void SpotLight(in int i,
               in vec3 eye,
               in vec3 ecPosition3,
               in vec3 normal,
               inout vec4 ambient,
               inout vec4 diffuse,
               inout vec4 specular)
{
    float nDotVP;           // normal . light direction
    float nDotHV;           // normal . light half vector
    float pf;               // power factor
    float spotDot;          // cosine of angle between spotlight
    float spotAttenuation;  // spotlight attenuation factor
    float attenuation;      // computed attenuation factor
    float d;                // distance from surface to light source
    vec3 VP;                // direction from surface to light position
    vec3 halfVector;        // direction of maximum highlights

    // Compute vector from surface to light position
    VP = vec3(gl_LightSource[i].position) - ecPosition3;

    // Compute distance between surface and light position
    d = length(VP);

    // Normalize the vector from surface to light position
    VP = normalize(VP);

    // Compute attenuation
    attenuation = 1.0 / (gl_LightSource[i].constantAttenuation +
                         gl_LightSource[i].linearAttenuation * d +
                         gl_LightSource[i].quadraticAttenuation * d * d);

    // See if point on surface is inside cone of illumination
    spotDot = dot(-VP, normalize(gl_LightSource[i].spotDirection));

    if (spotDot < gl_LightSource[i].spotCosCutoff)
        spotAttenuation = 0.0; // light adds no contribution
    else
        spotAttenuation = pow(spotDot, gl_LightSource[i].spotExponent);

    // Combine the spotlight and distance attenuation.
    attenuation *= spotAttenuation;

    halfVector = normalize(VP + eye);

    nDotVP = max(0.0, dot(normal, VP));
    nDotHV = max(0.0, dot(normal, halfVector));

    if (nDotVP == 0.0)
        pf = 0.0;
    else
        pf = pow(nDotHV, gl_FrontMaterial.shininess);

    ambient  += gl_LightSource[i].ambient * attenuation;
    diffuse  += gl_LightSource[i].diffuse * nDotVP * attenuation;
    specular += gl_LightSource[i].specular * pf * attenuation;
}

float round(float a)
{
	if (fract(a) >= 0.5)
		return ceil(a);
	else 
		return floor(a);

}


void main()
	{	
		vec4 position = Vertex;
		vec3 normal = Normal;
		
		if(length(MorphWeights) > 0.01)
		{
			
			position.xyz += MorphWeights.x*MorphPosition1.xyz;
			position.xyz += MorphWeights.y*MorphPosition2.xyz;
			position.xyz += MorphWeights.z*MorphPosition3.xyz;
			position.xyz += MorphWeights.w*MorphPosition4.xyz;
			
			normal += MorphWeights.x*MorphNormal1;
			normal += MorphWeights.y*MorphNormal2;
			normal += MorphWeights.z*MorphNormal3;
			normal += MorphWeights.w*MorphNormal4;
			
		}
		
	
		mat4 transform  = Weights.x*Transforms[int(round(MatrixIndices.x))];
        	transform += Weights.y*Transforms[int(round(MatrixIndices.y))];
        	transform += Weights.z*Transforms[int(round(MatrixIndices.x))];
        	transform += Weights.w*Transforms[int(round(MatrixIndices.w))];
	 	
		//gl_Position = transform *Vertex;
		gl_Position = transform *position;
		//gl_Position = Vertex;

		vec3 ecPosition = vec3(gl_ModelViewMatrix*Vertex);

		vec3 eye = -normalize(ecPosition);

		normal = vec3(transform*vec4(normal,0.0));
		
		normal = normalize(gl_NormalMatrix * normal);


		vec4 ambient = vec4(0.0);
		vec4 diffuse = vec4(0.0);
		vec4 specular = vec4(0.0);


		for (int i = 0; i < gl_MaxLights; i++)
		{
			if(isLightEnabled(i))
			{
				if (gl_LightSource[i].position.w == 0.0)
        				DirectionalLight(i, normal, ambient, diffuse, specular);
    				else if (gl_LightSource[i].spotCutoff == 180.0)
        				PointLight(i, eye, ecPosition, normal, ambient, diffuse, specular);
				else
        				SpotLight(i, eye, ecPosition, normal, ambient, diffuse, specular);

			}
		}
		
		
		if (textureAvailable)
		{
			Color = length(vec3(AmbientMaterial))*ambient 
					+ length(vec3(DiffuseMaterial))*diffuse 
					+ length(vec3(SpecularMaterial))*specular ;
		}
		else 
		{
			Color = ambient*AmbientMaterial// gl_FrontLightModelProduct.sceneColor 
					+ diffuse*DiffuseMaterial 
					+ specular*SpecularMaterial;
		}
		
		TexCoord = TextureCoordinates;

		gl_Position = gl_ModelViewProjectionMatrix* gl_Position;
	}
