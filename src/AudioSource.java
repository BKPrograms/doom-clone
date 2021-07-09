import static org.lwjgl.openal.AL10.*;

public class AudioSource {

    private int sourceID;

    public AudioSource(Vector3F sourcePos) {
        sourceID = alGenSources();
        alSourcef(sourceID, AL_GAIN, 0.5f);
        alSourcef(sourceID, AL_PITCH, 1f);
        alSource3f(sourceID, AL_VELOCITY, 0, 0, 0);
        alSource3f(sourceID, AL_POSITION, sourcePos.getX(), sourcePos.getY(), sourcePos.getZ());
    }

    public void setLooping(boolean val){
        alSourcei(sourceID, AL_LOOPING, val ? 1 : 0);
    }

    public void setVolume(float newVol){

        alSourcef(sourceID, AL_GAIN, newVol);
    }

    public void setPos(Vector3F newPos){
        alSource3f(sourceID, AL_POSITION, newPos.getX(), newPos.getY(), newPos.getZ());
    }

    public float getPos(){
        return alGetSourcei(sourceID, AL_POSITION);
    }
    public boolean isSourcePlaying(){

        return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;

    }

    public void delete(){
        alDeleteSources(sourceID);
    }

    public void play(int buffer){

        // Checks to see if source has buffer data or not (rewriting causes openAL error)
        if (alGetSourcef(sourceID, AL_BUFFER) == 0.0) {
            alSourcei(sourceID, AL_BUFFER, buffer);
        }

        if (!isSourcePlaying()){
            alSourcePlay(sourceID);
        }



    }
}
