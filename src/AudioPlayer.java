import org.lwjgl.openal.AL;

public class AudioPlayer {

    private Sound[] sounds;

    public AudioPlayer(String[] names) {

        sounds = new Sound[names.length];

        try {
            AL.create();

            for (int i = 0; i < names.length; i++) {

                Sound temp = new Sound(names[i]);

                sounds[i] = temp;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void playAt(int index){

        sounds[index].play();

    }

    public void stopAt(int index){
        sounds[index].stop();
    }

}
