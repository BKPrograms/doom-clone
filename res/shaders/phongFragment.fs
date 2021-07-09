# version 330

const int MAX_POINT_LIGHTS = 4;
const int MAX_SPOT_LIGHTS = 4;

in vec2 texture0;
in vec3 normal0;
in vec3 worldPos0;

out vec4 fragColour;

struct BaseLight{

    vec3 colour;
    float intensity;

};

struct DirectionalLight{

    BaseLight base;
    vec3 direction;
};

struct Attenuation{

    float constant;
    float linear;
    float exponent;

};

struct PointLight{
    BaseLight base;
    Attenuation att;
    vec3 position;
    float range;

};

struct SpotLight{
    PointLight point;
    vec3 direction;
    float coneCutOff;

};

uniform vec3 baseColour;
uniform vec3 eyePos; // Camera Position
uniform vec3 ambientLight;
uniform sampler2D sampler;
uniform DirectionalLight directional;
uniform float specularIntensity;
uniform float specularPower;

uniform PointLight pointLights[MAX_POINT_LIGHTS];

uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal){
    float diffuseFactor = dot(normal, -direction);

    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specularColour = vec4(0, 0, 0, 0);

    if (diffuseFactor > 0){ // If the light source is actually going to have a visible difference on the surface
        diffuseColour = vec4(base.colour, 1.0) * base.intensity * diffuseFactor;


        vec3 directionToEye = normalize(eyePos - worldPos0);
        vec3 reflectDirection = normalize(reflect(direction, normal));

        float specularFactor = dot(directionToEye, reflectDirection);
        specularFactor = pow(specularFactor, specularPower);

        if (specularFactor > 0 && base.intensity != 0){
            specularColour = vec4(base.colour, 1.0) * specularIntensity * specularFactor * base.intensity;
        }
    }

    return (diffuseColour + specularColour);
}

vec4 calcDirectionalLight(DirectionalLight dl, vec3 normal){

    return calcLight(dl.base, -dl.direction, normal);

}

vec4 calcPointLight(PointLight pl, vec3 normal){

    vec3 lightDirection = worldPos0 - pl.position;

    float distToPoint = length(lightDirection);


    // This somewhat "contains" the light and gives it the circular effect
    if (distToPoint > pl.range){
        return vec4(0,0,0,0);
    }

    lightDirection = normalize(lightDirection);

    vec4 colour = calcLight(pl.base, lightDirection, normal);

    float att = pl.att.constant + (pl.att.linear * distToPoint) + (pl.att.exponent * distToPoint * distToPoint) + 0.0001;


    return colour/att;

}

vec4 calcSpotLight(SpotLight sl, vec3 normal){

    vec3 lightDirection = normalize(worldPos0 - sl.point.position);

    float spotFactor = dot(lightDirection, sl.direction); // Cosine between both directions as sl.direction is normalized

    vec4 colour = vec4(0,0,0,0);
    if (spotFactor > sl.coneCutOff){ // Current pixel is within cone

         // This ratio changes depending on current point's distant from source
         // So the closer we are to the center, the fraction will be closer to 0, and the edge to 1 vice versa.
        colour = calcPointLight(sl.point, normal) * (1.0 - (1.0-spotFactor)/(1.0-sl.coneCutOff));

    }

    return colour;

}

void main(){

    vec4 totalLight = vec4(ambientLight, 1); // Total Light at current pixel/point

    vec4 colour = vec4(baseColour, 1);

    vec4 textureColour = texture(sampler, texture0.xy);

    colour *= textureColour;

    //if (textureColour != vec4(0,0,0,0)) {
       //colour *= textureColour;
    // }

    vec3 normal = normalize(normal0);

    totalLight += calcDirectionalLight(directional, normal);

    for(int i = 0; i < MAX_POINT_LIGHTS; i++){

        if (pointLights[i].base.intensity > 0) { // If the current point light has an actual effect
            totalLight += calcPointLight(pointLights[i], normal);
        }

    }


    for(int j = 0; j < MAX_SPOT_LIGHTS; j++){

       if (spotLights[j].point.base.intensity > 0) { // If the current point light has an actual effect
             totalLight += calcSpotLight(spotLights[j], normal);
       }

    }

    fragColour = colour * totalLight;
}