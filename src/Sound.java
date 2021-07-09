import org.lwjgl.util.WaveData;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_BUFFER;

public class Sound {
    private int bufferID;
    private int source;

    public Sound(String filename) {

        try {
            bufferID = alGenBuffers();
            WaveData data = WaveData.create(new BufferedInputStream(new FileInputStream(".\\res\\music\\" + filename)));

            alBufferData(bufferID, data.format, data.data, data.samplerate);

            source = alGenSources();

            alSourcei(source, AL_BUFFER, bufferID);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void play(){
        alSourcePlay(source);
    }

    public void stop(){
        alSourcePause(source);
    }
}
