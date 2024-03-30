    package crystals;

    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.Sprite;
    import com.mygdx.game.MyGDXGame;

    import java.util.HashMap;
    import java.util.Map;

    public class Crystal extends Sprite {
        private final float x;
        private final float y;
        private final int id;
        private static final int FRAME_WIDTH = 32;
        private static final int FRAME_HEIGHT = 32;
        private static int nextId = 1;
        private static final Map<Integer, Crystal> idsToCrystal = new HashMap<>();

        public Crystal(float x, float y) {
            this.x = x;
            this.y = y;
            this.id = Crystal.nextId++;

            Texture texture = new Texture("Items/crystal.png");

            idsToCrystal.put(id, this);

            setBounds(x / MyGDXGame.PPM, y / MyGDXGame.PPM, FRAME_WIDTH / MyGDXGame.PPM, FRAME_HEIGHT / MyGDXGame.PPM);
            setRegion(texture);
        }

        public static Crystal getCrystalById(int id) {
            return idsToCrystal.get(id);
        }
        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
        public int getId() {
            return id;
        }
    }
