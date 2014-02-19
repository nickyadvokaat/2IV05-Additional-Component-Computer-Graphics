package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private Node anchorPoints;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("BridgeBuilder Alpha");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // sky color
        viewPort.setBackgroundColor(ColorRGBA.Cyan);

        // cam speed
        flyCam.setMoveSpeed(30.0f);

        // a "+" in the middle of the screen to help aiming
        initCrossHairs();

        // load custom key mappings
        initKeys();

        // load terrain
        Spatial terrain = assetManager.loadModel("Scenes/Level1.j3o");
        rootNode.attachChild(terrain);
        TerrainLodControl lodControl = ((Node) terrain).getControl(TerrainLodControl.class);
        if (lodControl != null) {
            lodControl.setCamera(getCamera());
        }

        anchorPoints = new Node("Anchorpoint");
        rootNode.attachChild(anchorPoints);


        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);

        Box box = new Box(0.5f, 0.5f, 0.5f);
        
        Geometry gm = new Geometry("Box", box);
        gm.setLocalTranslation(new Vector3f(0, 0, -15));
        gm.setMaterial(mat);

        Geometry gm2 = new Geometry("Box", box);
        gm2.setLocalTranslation(new Vector3f(5, 0, -15));
        gm2.setMaterial(mat);

        Geometry gm3 = new Geometry("Box", box);
        gm3.setLocalTranslation(new Vector3f(0, 0, 25));
        gm3.setMaterial(mat);

        Geometry gm4 = new Geometry("Box", box);
        gm4.setLocalTranslation(new Vector3f(5, 0, 25));
        gm4.setMaterial(mat);
        
        Geometry gm5 = new Geometry("Box", box);
        gm5.setLocalTranslation(new Vector3f(0, -15, 0));
        gm5.setMaterial(mat);

        Geometry gm6 = new Geometry("Box", box);
        gm6.setLocalTranslation(new Vector3f(5, -15, 0));
        gm6.setMaterial(mat);

        Geometry gm7 = new Geometry("Box", box);
        gm7.setLocalTranslation(new Vector3f(0, -15, 10));
        gm7.setMaterial(mat);

        Geometry gm8 = new Geometry("Box", box);
        gm8.setLocalTranslation(new Vector3f(5, -15, 10));
        gm8.setMaterial(mat);

        
        anchorPoints.attachChild(gm);
        anchorPoints.attachChild(gm2);
        anchorPoints.attachChild(gm3);
        anchorPoints.attachChild(gm4);
        anchorPoints.attachChild(gm5);
        anchorPoints.attachChild(gm6);
        anchorPoints.attachChild(gm7);
        anchorPoints.attachChild(gm8);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initKeys() {
        inputManager.addMapping("Click",
                new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Click");
    }
    /**
     * Click object
     */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Click") && !keyPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                // 3. Collect intersections between Ray and Anchorpoints in results list.
                anchorPoints.collideWith(ray, results);
                // 4. Use the results 
                // reset color
                Material mat = new Material(assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Gray);
                anchorPoints.setMaterial(mat);
                // apply new
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();

                    Material mat2 = new Material(assetManager,
                            "Common/MatDefs/Misc/Unshaded.j3md");
                    mat2.setColor("Color", ColorRGBA.Red);
                    closest.getGeometry().setMaterial(mat2);
                }
            }
        }
    };

    /**
     * A centred plus sign to help the player aim.
     */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}
