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
import com.jme3.ui.Picture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

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
    private boolean left = false, right = false, up = false, down = false, keyq = false, keyz = false;
    private int building_mode = 0;
    private ArrayList<Geometry> addedBlocks;
    private ArrayList<Geometry> addedAnchors;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("BridgeBuilder Alpha");
        settings.setSettingsDialogImage("Textures/logo.jpg");
        try {
            settings.setIcons(new BufferedImage[]{
                ImageIO.read(Main.class.getResourceAsStream("/Textures/icon.png")),
                ImageIO.read(Main.class.getResourceAsStream("/Textures/icon128.png")),
                ImageIO.read(Main.class.getResourceAsStream("/Textures/icon32.png")),
                ImageIO.read(Main.class.getResourceAsStream("/Textures/icon16.png"))
            });
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        addedBlocks = new ArrayList<Geometry>();
        addedAnchors = new ArrayList<Geometry>();

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

        // draw HUD buttons
        initHUD();

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

        Geometry geom = new Geometry("Anchor" + (addedAnchors.size() - 1), box);
        geom.setLocalTranslation(a);
        geom.setMaterial(mat);


        geom.setUserData("type", 0);
        anchorPoints.attachChild(geom);
        addedAnchors.add(geom);
    }

    private void addTargetPoint(Vector3f a) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);

        Box box = new Box(0.51f, 0.51f, 0.51f);

        Geometry geom = new Geometry("Target", box);
        geom.setLocalTranslation(a);
        geom.setMaterial(mat);

        geom.setUserData("type", 1);
        targetPoints.attachChild(geom);
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

        Geometry geom = new Geometry("Block" + (addedBlocks.size() - 1), box);
        geom.setLocalTranslation(new Vector3f((from.x + to.x) / 2, (from.y + to.y) / 2, (from.z + to.z) / 2));
        geom.setMaterial(mat);

        geom.setUserData("type", 2);
        buildingBlocks.attachChild(geom);
        addedBlocks.add(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f upDir = new Vector3f(0, 1, 0);
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
        if (keyq) {
            walkDirection.addLocal(upDir);
        }
        if (keyz) {
            walkDirection.addLocal(upDir.negate());
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
        inputManager.addMapping("KeyQ", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("KeyZ", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("KeyR", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Key1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Key2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "KeyQ");
        inputManager.addListener(actionListener, "KeyZ");
        inputManager.addListener(actionListener, "KeyR");
        inputManager.addListener(actionListener, "Key1");
        inputManager.addListener(actionListener, "Key2");
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
            } else if (name.equals("KeyQ")) {
                if (keyPressed) {
                    keyq = true;
                } else {
                    keyq = false;
                }
            } else if (name.equals("KeyZ")) {
                if (keyPressed) {
                    keyz = true;
                } else {
                    keyz = false;
                }
            }
            if (name.equals("KeyR")) {
                if (!keyPressed && addedBlocks.size() != 0) {
                    Geometry g = addedBlocks.get(addedBlocks.size() - 1);
                    addedBlocks.remove(g);
                    buildingBlocks.detachChild(g);

                    g = addedAnchors.get(addedAnchors.size() - 1);
                    addedAnchors.remove(g);
                    anchorPoints.detachChild(g);
                }
            }
            if (name.equals("Key1")) {
                building_mode = 1;
                initHUD();
            }
            if (name.equals("Key2")) {
                building_mode = 2;
                initHUD();
            }
            if (name.equals("Click") && !keyPressed && building_mode == 1) {
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
    Picture pic_block;
    Picture pic_undo;
    Picture pic_start;
    Picture pic_cable;

    private void initHUD() {
        pic_block = new Picture("block");
        drawBlock(building_mode == 1);

        pic_cable = new Picture("cable");
        drawCable(building_mode == 2);

        pic_undo = new Picture("undo");
        drawUndo();

        pic_start = new Picture("start");
        drawStart();
    }

    private void drawBlock(Boolean isSelected) {
        if (isSelected) {
            pic_block.setImage(assetManager, "Textures/button_block_selected.png", true);
        } else {
            pic_block.setImage(assetManager, "Textures/button_block.png", true);
        }
        int width = settings.getWidth() / 10;
        int height = width / 2;
        int x = settings.getWidth() * 5 / 10;
        int y = settings.getHeight() - 10 - height;

        drawButton(pic_block, width, height, x, y);
    }

    private void drawCable(Boolean isSelected) {
        if (isSelected) {
            pic_cable.setImage(assetManager, "Textures/button_cable_selected.png", true);
        } else {
            pic_cable.setImage(assetManager, "Textures/button_cable.png", true);
        }
        int width = settings.getWidth() / 10;
        int height = width / 2;
        int x = settings.getWidth() * 7 / 10;
        int y = settings.getHeight() - 10 - height;

        drawButton(pic_cable, width, height, x, y);
    }

    private void drawUndo() {
        pic_undo.setImage(assetManager, "Textures/button_undo.png", true);

        int width = settings.getWidth() / 10;
        int height = width;
        int x = settings.getWidth() - width - 10;
        int y = settings.getHeight() - 10 - height;

        drawButton(pic_undo, width, height, x, y);
    }

    private void drawStart() {
        pic_start.setImage(assetManager, "Textures/button_play.png", true);

        int width = settings.getWidth() / 10;
        int height = width;
        int x = settings.getWidth() - width - 10;
        int y = settings.getHeight() - 2 * 10 - 2 * height;

        drawButton(pic_start, width, height, x, y);
    }

    private void drawButton(Picture pic, int width, int height, int x, int y) {
        pic.setWidth(width);
        pic.setHeight(height);
        pic.setPosition(x, y);
        guiNode.attachChild(pic);
    }
}
