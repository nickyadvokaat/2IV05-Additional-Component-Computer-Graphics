package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
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
import com.jme3.math.Transform;
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

    private Node clickable;
    private Node anchorPoints;
    private Node targetPoints;
    private Node buildingBlocks;
    private Geometry prevClickedGeometry;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("BridgeBuilder Alpha");
        settings.setSettingsDialogImage("Textures/logo.jpg");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
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
        // We set up collision detection for the scene by creating a static 
        //RigidBodyControl with mass zero.*/
        terrain.addControl(new RigidBodyControl(0));

        // set up camera
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setFallSpeed(0);
        player.setGravity(0);
        player.setPhysicsLocation(new Vector3f(-10, 10, 10));

        clickable = new Node("Clickable");
        rootNode.attachChild(clickable);

        anchorPoints = new Node("AnchorPoints");
        clickable.attachChild(anchorPoints);
        targetPoints = new Node("TargetPoints");
        clickable.attachChild(targetPoints);
        buildingBlocks = new Node("BuildingBlocks");
        clickable.attachChild(buildingBlocks);

        addAnchorPoint(new Vector3f(0, 0, -15));
        addAnchorPoint(new Vector3f(5, 0, -15));
        addAnchorPoint(new Vector3f(0, 0, 25));
        addAnchorPoint(new Vector3f(5, 0, 25));
        addAnchorPoint(new Vector3f(0, -15, 0));
        addAnchorPoint(new Vector3f(5, -15, 0));
        addAnchorPoint(new Vector3f(0, -15, 10));
        addAnchorPoint(new Vector3f(5, -15, 10));

        bulletAppState.getPhysicsSpace().add(terrain);
        bulletAppState.getPhysicsSpace().add(player);
    }

    private void addAnchorPoint(Vector3f a) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);

        Box box = new Box(0.5f, 0.5f, 0.5f);

        Geometry gm8 = new Geometry("Box", box);
        gm8.setLocalTranslation(a);
        gm8.setMaterial(mat);

        gm8.setUserData("type", 0);
        anchorPoints.attachChild(gm8);
    }

    private void addTargetPoint(Vector3f a) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);

        Box box = new Box(0.51f, 0.51f, 0.51f);

        Geometry gm8 = new Geometry("Box", box);
        gm8.setLocalTranslation(a);
        gm8.setMaterial(mat);

        gm8.setUserData("type", 1);
        targetPoints.attachChild(gm8);
    }

    private void addBuildingBlock(Vector3f from, Vector3f to) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.DarkGray);

        Box box = new Box(0.5f, 0.5f, 0.5f);

        if (from.x == to.x && !(from.z == to.z)) {
            box = new Box(0.25f, 0.25f, 2.5f);
        } else if (from.y == to.y) {
            box = new Box(2.5f, 0.25f, 0.25f);
        } else if (from.z == to.z) {
            box = new Box(0.25f, 2.5f, 0.25f);
        }

        Geometry gm8 = new Geometry("Box", box);
        gm8.setLocalTranslation(new Vector3f((from.x + to.x) / 2, (from.y + to.y) / 2, (from.z + to.z) / 2));
        gm8.setMaterial(mat);

        gm8.setUserData("type", 2);
        buildingBlocks.attachChild(gm8);

        System.out.println(from.toString());
        System.out.println(to.toString());
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
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
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(actionListener, "Left");
    inputManager.addListener(actionListener, "Right");
    inputManager.addListener(actionListener, "Up");
    inputManager.addListener(actionListener, "Down");
    }
    
    
 
    /**
     * Click object
     */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Left")) {
                if (keyPressed) {
                    left = true;
                } else {
                    left = false;
                }
            } else if (name.equals("Right")) {
                if (keyPressed) {
                    right = true;
                } else {
                    right = false;
                }
            } else if (name.equals("Up")) {
                if (keyPressed) {
                    up = true;
                } else {
                    up = false;
                }
            } else if (name.equals("Down")) {
                if (keyPressed) {
                    down = true;
                } else {
                    down = false;
                }
            }
                if (name.equals("Click") && !keyPressed) {
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();
                    // 2. Aim the ray from cam loc to cam direction.
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    // 3. Collect intersections between Ray and Anchorpoints in results list.
                    clickable.collideWith(ray, results);
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
                        Geometry geometry = closest.getGeometry();

                        switch ((Integer) (geometry.getUserData("type"))) {
                            case 0: // anchor
                                // reset target
                                targetPoints.detachAllChildren();

                                Material mat2 = new Material(assetManager,
                                        "Common/MatDefs/Misc/Unshaded.j3md");
                                mat2.setColor("Color", ColorRGBA.Red);
                                geometry.setMaterial(mat2);

                                prevClickedGeometry = geometry;

                                Vector3f translation = closest.getGeometry().getLocalTransform().getTranslation();

                                addTargetPoint(new Vector3f(translation.x, translation.y + 5, translation.z));
                                addTargetPoint(new Vector3f(translation.x, translation.y - 5, translation.z));
                                addTargetPoint(new Vector3f(translation.x + 5, translation.y, translation.z));
                                addTargetPoint(new Vector3f(translation.x, translation.y, translation.z + 5));
                                addTargetPoint(new Vector3f(translation.x - 5, translation.y, translation.z));
                                addTargetPoint(new Vector3f(translation.x, translation.y, translation.z - 5));
                                break;
                            case 1: // target
                                addAnchorPoint(geometry.getLocalTranslation());
                                addBuildingBlock(prevClickedGeometry.getLocalTransform().getTranslation(),
                                        geometry.getLocalTransform().getTranslation());
                                // reset target
                                targetPoints.detachAllChildren();
                                break;
                            case 2: // building block
                                break;

                        }
                    } else {

                        // reset target
                        targetPoints.detachAllChildren();
                    }
                }
            }
        }

        ;

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
            
