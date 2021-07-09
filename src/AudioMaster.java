import org.lwjgl.openal.AL;
import org.lwjgl.util.WaveData;

import static org.lwjgl.openal.AL10.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class AudioMaster {

    private static ArrayList<Integer> bufferIDs = new ArrayList<>();

    public static void init(){

        try {
            AL.create();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void setListenerPos(Vector3F pos){

        alListener3f(AL_POSITION, pos.getX(), pos.getY(), pos.getZ());
        alListener3f(AL_VELOCITY, 1, 1, 1);



    }

    public static int loadSound(String filename){
        int buffer = alGenBuffers();
        bufferIDs.add(buffer);

        try {
            WaveData waveData = WaveData.create(new BufferedInputStream(new FileInputStream(".\\res\\music\\" + filename)));
            alBufferData(buffer, waveData.format, waveData.data, waveData.samplerate);
            waveData.dispose();
            return buffer;
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }


    public static void cleanUp(){

        for (int i: bufferIDs) {


            System.out.println(alGetBufferf(i, AL_BUFFER));
            alDeleteBuffers(i);

        }

        AL.destroy();
    }
}
