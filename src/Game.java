public class Game {

    private static Level level;
    public static boolean isRunning;

    public Game() {

        isRunning = true;

        Player player = new Player(new Vector3F(0, 0, 0));

        // Change Level Name and sprite/texture sheet here:
        level = new Level("levelTest.png", "DoomSpriteSheet.png", player);

        Transform.setProjection(70, GameWindow.getWidth(), GameWindow.getHeight(), 0.01f, 1000f);
        Transform.setCamera(level.getPlayer().getCamera());

    }

    public void input() {
        if (isRunning){
            level.input();
        }
    }


    public void update() {

        if (isRunning){
            level.update();
        }

    }

    public void render() {

        if (isRunning){
            level.render();
        }


    }

    public static void setIsRunning(boolean val){
        isRunning = val;
    }

    public static Level getLevel(){
        return level;
    }
}
