package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.joints.SliderJoint;
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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.ui.Picture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import train.staticTrain;
import com.jme3.bullet.debug.*;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;
import train.StaticTrainNew;
import train.tunnel;

public class MainNew extends SimpleApplication implements ScreenController {

    static MainNew app;
    Highscore HS;

    public static void main(String args[]) {
        app = new MainNew();
        AppSettings settings = new AppSettings(true);
        // Set title
        settings.setTitle("BridgeBuilder V2");
        // Set start image
        settings.setSettingsDialogImage("Textures/logo.jpg");
        // Set icon
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
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.start();
    }
    // Prepare the Physics Application State (jBullet)
    private Nifty nifty;
    private boolean menu = false;
    private BulletAppState bulletAppStateGame;
    private BulletAppState bulletAppStateCamera;
    private CharacterControl player;
    private Material bar_mat;
    private Material connection_mat;
    private Material target_mat;
    private Material matWood;
    private Material matRail;
    private Material matBoard;
    private Material mat;
    private Material matTrain;
    private Material matTrain2;
    private Material matTrain3;
    private static final Box connection;
    private static final Box barX;
    private static final Box barY;
    private static final Box barZ;
    private RigidBodyControl ball_phy;
    private static final Sphere sphere;
    private Node clickablesNode;
    private Node connectionsNode;
    private Node barsNode;
    private Node targetsNode;
    private Node trackNode;
    private Node trackNode2;
    private Node trackNode3;
    private Node trackNode5;
    private Node trackNode6;
    private Node trackNode4;
    private Node trackNode7;
    private Node trackNode8;
    private Node trackNode9;
    private Node train;
    private Node wheels;
    private Geometry prevClickedGeometry;
    private int building_mode = 0;
    private int connectionNameCounter = 0;
    private int barNameCounter = 0;
    private ArrayList<Geometry> connections = new ArrayList<Geometry>();
    private ArrayList<Geometry> groundConnections = new ArrayList<Geometry>();
    private ArrayList<Geometry> bars = new ArrayList<Geometry>();
    private ArrayList<Geometry> targets = new ArrayList<Geometry>();
    private ArrayList<HingeJointRef> joints = new ArrayList<HingeJointRef>();
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false, keyq = false, keyz = false;
    private boolean play = false;
    private boolean gameStarted = false;
    private TrainTrack track;
    private roadSign roadSign;
    private Picture pic_block;
    private Picture pic_undo;
    private Picture pic_start;
    private Picture pic_cable;
    private VehicleControl vehicle;
    private float accelerationValue = 7.0f;
    private static Vector3f[] dirVectors;
    private int level = 0;
    private int highscore;
    private String levelStatus;
    private ArrayList<Node> trackNodes;

    static {
        // Initialize the cannon ball geometry        
        sphere = new Sphere(32, 32, 0.4f, true, false);
        sphere.setTextureMode(TextureMode.Projected);

        // Initialize the connection geometry 
        connection = new Box(0.5f, 0.5f, 0.5f);
        connection.scaleTextureCoordinates(new Vector2f(1f, .5f));

        float w = 0.5f;
        // Initialize the bar in x direction geometry 
        barX = new Box(2f, w, w);
        barX.scaleTextureCoordinates(new Vector2f(1f, .5f));

        // Initialize the bar in y direction geometry 
        barY = new Box(w, 2, w);
        barY.scaleTextureCoordinates(new Vector2f(1f, .5f));

        // Initialize the bar in z direction geometry 
        barZ = new Box(w, w, 2);
        barZ.scaleTextureCoordinates(new Vector2f(1f, .5f));

        dirVectors = new Vector3f[6];
        dirVectors[0] = new Vector3f(5, 0, 0);
        dirVectors[1] = new Vector3f(-5, 0, 0);
        dirVectors[2] = new Vector3f(0, 5, 0);
        dirVectors[3] = new Vector3f(0, -5, 0);
        dirVectors[4] = new Vector3f(0, 0, 5);
        dirVectors[5] = new Vector3f(0, 0, -5);
    }

    @Override
    public void simpleInitApp() {
        try {
            HS = new Highscore();
        } catch (IOException ex) {
            Logger.getLogger(MainNew.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Set up nodesdebugNode = BulletDebugNode('Debug')
        DebugTools debugNode;
        debugNode = new DebugTools(assetManager);
        debugNode.show(renderManager, viewPort);
        clickablesNode = new Node("clickablesNode");
        rootNode.attachChild(clickablesNode);
        connectionsNode = new Node("connectionsNode");
        clickablesNode.attachChild(connectionsNode);
        barsNode = new Node("barsNode");
        clickablesNode.attachChild(barsNode);
        targetsNode = new Node("targetsNode");
        clickablesNode.attachChild(targetsNode);


        // disable the fly cam
//        flyCam.setEnabled(false);
//        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);

        // Set speed of camera
        flyCam.setMoveSpeed(30.0f);

        // sky color
        viewPort.setBackgroundColor(ColorRGBA.Cyan);

        // Set up Physics Game         
        bulletAppStateGame = new BulletAppState();
        stateManager.attach(bulletAppStateGame);
        // Pause physics
        bulletAppStateGame.setSpeed(0);
        Spatial terrainCamera;
        Spatial terrainGame;
        // Load terrain
        switch (level) {
            case 0:
                terrainCamera = assetManager.loadModel("Scenes/Level1.j3o");
                terrainGame = assetManager.loadModel("Scenes/Level1.j3o");
                break;
            case 1:
                terrainCamera = assetManager.loadModel("Scenes/Level2.j3o");
                terrainGame = assetManager.loadModel("Scenes/Level2.j3o");
                break;
            case 2:
                terrainCamera = assetManager.loadModel("Scenes/level3.j3o");
                terrainGame = assetManager.loadModel("Scenes/level3.j3o");
                break;
            case 3:
                terrainCamera = assetManager.loadModel("Scenes/level4.j3o");
                terrainGame = assetManager.loadModel("Scenes/level4.j3o");
                break;
            default:
                terrainCamera = assetManager.loadModel("Scenes/Level1.j3o");
                terrainGame = assetManager.loadModel("Scenes/Level1.j3o");
                break;
        }

        rootNode.attachChild(terrainCamera);
        TerrainLodControl lodControlCamera = ((Node) terrainCamera).getControl(TerrainLodControl.class);
        if (lodControlCamera != null) {
            lodControlCamera.setCamera(getCamera());
        }
        terrainCamera.addControl(new RigidBodyControl(0));
        rootNode.attachChild(terrainGame);
        TerrainLodControl lodControlGame = ((Node) terrainGame).getControl(TerrainLodControl.class);
        if (lodControlGame != null) {
            lodControlGame.setCamera(getCamera());
        }
        terrainGame.addControl(new RigidBodyControl(0));

        // Set up camera
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setFallSpeed(0);
        player.setGravity(0);
        player.setPhysicsLocation(new Vector3f(-10, 10, 10));

        bulletAppStateCamera = new BulletAppState();
        stateManager.attach(bulletAppStateCamera);
        bulletAppStateCamera.getPhysicsSpace().add(terrainCamera);
        bulletAppStateCamera.getPhysicsSpace().add(player);
        bulletAppStateGame.getPhysicsSpace().add(terrainGame);

        initKeys();
        initMaterials();
        initCrossHairs();
        initGroundConnections();
        initTunnel();
//        initHUD();
        setupHUD();
        initTrack();
        buildPlayer();
    }

    private void initTrack() {
        matWood = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWood.setTexture("ColorMap",
                assetManager.loadTexture("Textures/wood.png"));
        matRail = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matRail.setTexture("ColorMap",
                assetManager.loadTexture("Textures/metal.jpg"));
        track = new TrainTrack();
        if(level ==0 ){
            initRail1();
            drawBridge1();
        }else{
            initRail2();
            drawBridge2();
        }
      
    }
    
    private void initRail2(){
        for (int i = 0; i < 6; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(0.5f, 1.75f, -88 + 9 * i);
            trackNode.addControl(new RigidBodyControl(3));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
        for (int i = 0; i < 7; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(+0.5f, 1.75f, 38 + 9 * i);
            trackNode.addControl(new RigidBodyControl(3));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
    }
    
    private void initRail1(){
        for (int i = 0; i < 8; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(0.5f, 1.75f, -88 + 9 * i);
            trackNode.addControl(new RigidBodyControl(30));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
        for (int i = 0; i < 8; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(+0.5f, 1.75f, 29 + 9 * i);
            trackNode.addControl(new RigidBodyControl(30));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
    }

    private void drawBridge1() {
        for (int i = 0; i < 5; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(+0.5f, 1.75f, -16 + 9 * i);
            trackNode.addControl(new RigidBodyControl(3));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
    }

    private void drawBridge2() {
        for (int i = 0; i < 8; i++) {
            trackNode = new Node();
            trackNode.attachChild(track.getTrack(3, matWood, matRail));
            trackNode.setLocalTranslation(+0.5f, 1.75f, -34 + 9 * i);
            trackNode.addControl(new RigidBodyControl(3));
            bulletAppStateGame.getPhysicsSpace().add(trackNode);
            rootNode.attachChild(trackNode);
        }
    }


    private void initKeys() {
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
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
        inputManager.addMapping("KeyP", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("KeyI", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("ESC", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "KeyQ");
        inputManager.addListener(actionListener, "KeyZ");
        inputManager.addListener(actionListener, "KeyR");
        inputManager.addListener(actionListener, "Key1");
        inputManager.addListener(actionListener, "Key2");
        inputManager.addListener(actionListener, "KeyP");
        inputManager.addListener(actionListener, "KeyI");
        inputManager.addListener(actionListener, "ESC");

    }
    // responds when key or button is pressed
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Left")) {
                left = keyPressed;
            } else if (name.equals("Right")) {
                right = keyPressed;
            } else if (name.equals("Up")) {
                up = keyPressed;
            } else if (name.equals("Down")) {
                down = keyPressed;
            } else if (name.equals("KeyQ")) {
                keyq = keyPressed;
            } else if (name.equals("KeyZ")) {
                keyz = keyPressed;
            }
            if (name.equals("KeyP")) {
                targetsNode.detachAllChildren();
                bulletAppStateGame.setSpeed(1);
                gameStarted = true;
            }
            // debugging
            if (name.equals("ESC") && !keyPressed) {

                if (menu) {
                    flyCam.setEnabled(true);
                    guiViewPort.removeProcessor(niftyDisplay);
                    inputManager.setCursorVisible(false);
                    menu = false;
                } else {
                    setupMenu();
                    menu = true;
                }
            }
            // debugging
            if (name.equals("KeyR") && !keyPressed) {
                //remove previous
                if (bars.size() > 0) {
                    Geometry lastBar = bars.get(bars.size() - 1);
                    bars.remove(lastBar);
                    barsNode.detachChild(lastBar);
                    bulletAppStateGame.getPhysicsSpace().remove(lastBar);

                    // remove joints
                    ArrayList<HingeJointRef> remove = new ArrayList<HingeJointRef>();
                    for (HingeJointRef hgj : joints) {
                        if (hgj.bar == lastBar) {
                            remove.add(hgj);
                        }
                    }
                    for (HingeJointRef r : remove) {
                        joints.remove(r);
                        bulletAppStateGame.getPhysicsSpace().remove(r);
                    }

                    // remove possible connections
                    ArrayList<Geometry> removeG = new ArrayList<Geometry>();
                    for (Geometry g : connections) {
                        if (!groundConnections.contains(g) && jointsCount(g) == 0) {
                            removeG.add(g);
                        }
                    }
                    for (Geometry g : removeG) {
                        connections.remove(g);
                        connectionsNode.detachChild(g);
                        bulletAppStateGame.getPhysicsSpace().remove(g);
                    }
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
            if (name.equals("Click") && !keyPressed) {
                if (gameStarted) {
                    makeCannonBall();
                } else {
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();
                    // 2. Aim the ray from cam loc to cam direction.
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    // 3. Collect intersections between Ray and Anchorpoints in results list.
                    clickablesNode.collideWith(ray, results);
                    // 4. Use the results 

                    if (results.size() > 0) {
                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();
                        Geometry geometry = closest.getGeometry();

                        switch ((Integer) (geometry.getUserData("type"))) {
                            case 0: // ground connection
                            case 1: // connection
                                targetsNode.detachAllChildren();

                                //Material mat2 = new Material(assetManager,
                                //         "Common/MatDefs/Misc/Unshaded.j3md");
                                // mat2.setColor("Color", ColorRGBA.Red);
                                //geometry.setMaterial(mat2);

                                if (prevClickedGeometry == null) {
                                    prevClickedGeometry = geometry;
                                    Vector3f translation = closest.getGeometry().getLocalTransform().getTranslation();

                                    int[] a = addExtraTargets(translation);
                                    for (int i = 0; i < 6; i++) {
                                        if (a[i] >= 1 || !isBarAtLocation(translation, translation.add(dirVectors[i]))) {
                                            addTarget(translation.add(dirVectors[i]));
                                        }
                                        if (a[i] >= 2) {
                                            addTarget(translation.add(dirVectors[i].mult(a[i])));
                                        }
                                    }
                                } else {
                                    resetTarget();
                                }
                                break;
                            case 2: // target
                                build(prevClickedGeometry, geometry);
                                resetTarget();
                                break;
                            case 3: // bar
                                resetTarget();
                                break;
                        }
                    } else {
                        resetTarget();
                    }
                }
            }
        }
    };

    private void build(Geometry geometryFrom, Geometry geometryTo) {
        Vector3f locTo = geometryTo.getLocalTranslation();
        Vector3f locFrom = geometryFrom.getWorldTranslation();
        Vector3f dir = getDirectionVector(locFrom, locTo);
        int nrBars = getNrBars(locFrom, locTo);

        Vector3f locationBegin = geometryFrom.getWorldTranslation();
        Geometry geometryBegin = geometryFrom;

        for (int i = 0; i < nrBars; i++) {
            Vector3f locationEnd = locationBegin.add(dir);

            Geometry geometryEnd = getConnectionAtLocation(locationEnd);
            if (!isConnectionAtLocation(locationEnd)) {
                addConnection(locationEnd);
                geometryEnd = connections.get(connections.size() - 1);
            }
            addBar(geometryBegin, geometryEnd);
            geometryBegin = geometryEnd;
            locationBegin = locationEnd;
        }
    }

    private int jointsCount(Geometry g) {
        int result = 0;
        for (HingeJointRef hgj : this.joints) {
            if (hgj.connection == g) {
                result++;
            }
        }
        return result;
    }

    private void resetTarget() {
        targetsNode.detachAllChildren();
        prevClickedGeometry = null;
    }

    private float distance(Vector3f v1, Vector3f v2) {
        return (float) Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2) + Math.pow(v1.z - v2.z, 2));
    }

    private void initMaterials() {
        bar_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/rock.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        bar_mat.setTexture("ColorMap", tex);

        connection_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/metal.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        connection_mat.setTexture("ColorMap", tex2);

        target_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/bmetal.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        target_mat.setTexture("ColorMap", tex3);
    }

    private void initTunnel() {
        Quaternion q = new Quaternion();
        q.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));

        Node n = tunnel.getTunnel(assetManager);
        n.setLocalRotation(q);
        n.setLocalTranslation(2.5f, 0, -90);
        n.scale(2f);
        this.rootNode.attachChild(n);

        Node n2 = tunnel.getTunnel(assetManager);
        n2.setLocalRotation(q);
        n2.setLocalTranslation(2.5f, 0, 90);
        n2.scale(2f);
        this.rootNode.attachChild(n2);
    }

    private void initGroundConnections() {
        int x = 1;
        switch (level) {
            case 0:
                addGroundConnection(new Vector3f(0, 0 + x, -15));
                addGroundConnection(new Vector3f(5, 0 + x, -15));
                addGroundConnection(new Vector3f(0, 0 + x, 25));
                addGroundConnection(new Vector3f(5, 0 + x, 25));
                addGroundConnection(new Vector3f(0, -15 + x, 0));
                addGroundConnection(new Vector3f(5, -15 + x, 0));
                addGroundConnection(new Vector3f(0, -15 + x, 10));
                addGroundConnection(new Vector3f(5, -15 + x, 10));

                for (int i = 1; i < 15; i++) {
                    addGroundConnection(new Vector3f(0, 0 + x, -15 - 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, -15 - 5 * i));
                    addGroundConnection(new Vector3f(0, 0 + x, 25 + 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, 25 + 5 * i));
                }
                break;
            case 2:
                addGroundConnection(new Vector3f(0, 0 + x, -35));
                addGroundConnection(new Vector3f(5, 0 + x, -35));
                addGroundConnection(new Vector3f(0, 0 + x, 35));
                addGroundConnection(new Vector3f(5, 0 + x, 35));
                 addGroundConnection(new Vector3f(0, -10 + x, -15));
                addGroundConnection(new Vector3f(5, -10 + x, -15));
                addGroundConnection(new Vector3f(5f, -10 + x, 5));
                addGroundConnection(new Vector3f(0f, -10 + x, 5));
                addGroundConnection(new Vector3f(0f, -10 + x, 0));
                addGroundConnection(new Vector3f(5f, -10 + x, 0));
                addGroundConnection(new Vector3f(0f, -25 + x, 15));
                addGroundConnection(new Vector3f(5f, -25 + x, 15));
                for (int i = 1; i < 13; i++) {
                    addGroundConnection(new Vector3f(0, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(0, 0 + x, 35 + 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, 35 + 5 * i));
                }
                break;
            case 1:
                addGroundConnection(new Vector3f(0, 0 + x, -35));
                addGroundConnection(new Vector3f(5, 0 + x, -35));
                addGroundConnection(new Vector3f(0, 0 + x, 35));
                addGroundConnection(new Vector3f(5, 0 + x, 35));
                addGroundConnection(new Vector3f(0, -10 + x, -15));
                addGroundConnection(new Vector3f(5, -10 + x, -15));                
                addGroundConnection(new Vector3f(5f, -25 + x, 5));
                addGroundConnection(new Vector3f(0f, -25 + x, 5));
                addGroundConnection(new Vector3f(0f, -25 + x, 10));
                addGroundConnection(new Vector3f(5f, -25 + x, 10));
                addGroundConnection(new Vector3f(0f, -25 + x, 15));
                addGroundConnection(new Vector3f(5f, -25 + x, 15));
                for (int i = 1; i < 13; i++) {
                    addGroundConnection(new Vector3f(0, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(0, 0 + x, 35 + 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, 35 + 5 * i));
                }
                break;
                
            case 3:
                addGroundConnection(new Vector3f(0, 0 + x, -35));
                addGroundConnection(new Vector3f(5, 0 + x, -35));
                addGroundConnection(new Vector3f(0, 0 + x, 40));
                addGroundConnection(new Vector3f(5, 0 + x, 40));
                addGroundConnection(new Vector3f(0f, -25 + x, 0));
                addGroundConnection(new Vector3f(5f, -25 + x, 0));
                addGroundConnection(new Vector3f(0f, -25 + x, 15));
                addGroundConnection(new Vector3f(5f, -25 + x, 15));
                for (int i = 1; i < 13; i++) {
                    addGroundConnection(new Vector3f(0, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, -35 - 5 * i));
                    addGroundConnection(new Vector3f(0, 0 + x, 40 + 5 * i));
                    addGroundConnection(new Vector3f(5, 0 + x, 40 + 5 * i));
                }
                break;
            default:
                addGroundConnection(new Vector3f(0, 0 + x, -15));
                addGroundConnection(new Vector3f(5, 0 + x, -15));
                addGroundConnection(new Vector3f(0, 0 + x, 25));
                addGroundConnection(new Vector3f(5, 0 + x, 25));
                addGroundConnection(new Vector3f(0, -15 + x, 0));
                addGroundConnection(new Vector3f(5, -15 + x, 0));
                addGroundConnection(new Vector3f(0, -15 + x, 10));
                addGroundConnection(new Vector3f(5, -15 + x, 10));
                break;
        }


    }

    private void addGroundConnection(Vector3f location) {
        Geometry connectionGeometry = new Geometry("connection" + this.connectionNameCounter++, this.connection);
        connectionGeometry.setMaterial(connection_mat);
        connectionGeometry.setLocalTranslation(location);
        connectionGeometry.setUserData("type", 0);

        this.connectionsNode.attachChild(connectionGeometry);

        RigidBodyControl rbc = new RigidBodyControl(0f);
        connectionGeometry.addControl(rbc);
        this.bulletAppStateGame.getPhysicsSpace().add(rbc);

        this.connections.add(connectionGeometry);
        this.groundConnections.add(connectionGeometry);
        // visual
        Geometry connectionGeometry2 = new Geometry("connection" + this.connectionNameCounter++, this.connection);
        connectionGeometry2.setMaterial(connection_mat);
        connectionGeometry2.setLocalTranslation(new Vector3f(location.x, location.y - 1, location.z));
        connectionGeometry2.setUserData("type", 0);
        this.rootNode.attachChild(connectionGeometry2);
    }

    private void addConnection(Vector3f location) {
        Geometry connectionGeometry = new Geometry("connection" + this.connectionNameCounter++, this.connection);
        connectionGeometry.setMaterial(connection_mat);
        connectionGeometry.setLocalTranslation(location);
        connectionGeometry.setUserData("type", 1);

        this.connectionsNode.attachChild(connectionGeometry);

        RigidBodyControl rbc = new RigidBodyControl(1f);
        connectionGeometry.addControl(rbc);
        this.bulletAppStateGame.getPhysicsSpace().add(rbc);

        this.connections.add(connectionGeometry);
    }

    private void addBar(Geometry connection1, Geometry connection2) {
        if (connection1.getWorldTranslation().x > connection2.getWorldTranslation().x
                || connection1.getWorldTranslation().y > connection2.getWorldTranslation().y
                || connection1.getWorldTranslation().z > connection2.getWorldTranslation().z) {
            addBarH(connection2, connection1);
        } else {
            addBarH(connection1, connection2);
        }
    }

    private void addBarH(Geometry connection1, Geometry connection2) {
        Vector3f location = new Vector3f(
                (connection1.getWorldTranslation().x + connection2.getWorldTranslation().x) / 2,
                (connection1.getWorldTranslation().y + connection2.getWorldTranslation().y) / 2,
                (connection1.getWorldTranslation().z + connection2.getWorldTranslation().z) / 2);

        /*
         * x 0
         * y 1
         * z 2
         */
        int direction = 0;
        if (connection1.getWorldTranslation().y != connection2.getWorldTranslation().y) {
            direction = 1;
        } else if (connection1.getWorldTranslation().z != connection2.getWorldTranslation().z) {
            direction = 2;
        }

        Box bar = this.barX;
        switch (direction) {
            case 1:
                bar = this.barY;
                break;
            case 2:
                bar = this.barZ;
                break;
        }
        Geometry barGeometry = new Geometry("connection" + this.barNameCounter++, bar);
        barGeometry.setMaterial(bar_mat);
        barGeometry.setLocalTranslation(location);
        barGeometry.setUserData("type", 3);

        this.barsNode.attachChild(barGeometry);

        RigidBodyControl rbc = new RigidBodyControl(4f);
        barGeometry.addControl(rbc);
        this.bulletAppStateGame.getPhysicsSpace().add(rbc);

        this.bars.add(barGeometry);
        Vector3f v1 = null;
        Vector3f v2 = null;
        Vector3f v3 = null;
        Vector3f v4 = null;
        switch (direction) {
            case 0:
                v1 = new Vector3f(-2f, 0f, 0f);
                v2 = new Vector3f(0.5f, 0f, 0f);
                v3 = new Vector3f(2f, 0f, 0f);
                v4 = new Vector3f(-0.5f, 0f, 0f);
                break;
            case 1:
                v1 = new Vector3f(0f, -2f, 0f);
                v2 = new Vector3f(0f, 0.5f, 0f);
                v3 = new Vector3f(0f, 2f, 0f);
                v4 = new Vector3f(0f, -0.5f, 0f);

                break;
            case 2:
                v1 = new Vector3f(0f, 0f, -2f);
                v2 = new Vector3f(0f, 0f, 0.5f);
                v3 = new Vector3f(0f, 0f, 2f);
                v4 = new Vector3f(0f, 0f, -0.5f);
                break;
        }

        // add first joint
        HingeJointRef joint1 = new HingeJointRef(barGeometry.getControl(RigidBodyControl.class),
                connection1.getControl(RigidBodyControl.class),
                v1,
                v2,
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z,
                connection1,
                barGeometry);
        joint1.setLimit(0, 0);
        bulletAppStateGame.getPhysicsSpace().add(joint1);
        this.joints.add(joint1);

        // add second joint
        HingeJointRef joint2 = new HingeJointRef(barGeometry.getControl(RigidBodyControl.class),
                connection2.getControl(RigidBodyControl.class),
                v3,
                v4,
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z,
                connection2,
                barGeometry);
        joint2.setLimit(0, 0);
        bulletAppStateGame.getPhysicsSpace().add(joint2);
        this.joints.add(joint2);
    }

    private void addTarget(Vector3f location) {
        Geometry targetGeometry = new Geometry("target" + this.connectionNameCounter++, this.connection);
        targetGeometry.setMaterial(target_mat);
        targetGeometry.scale(1.1f);
        targetGeometry.setLocalTranslation(location);
        targetGeometry.setUserData("type", 2);

        this.targetsNode.attachChild(targetGeometry);

        this.targets.add(targetGeometry);
    }

    private int[] addExtraTargets(Vector3f loc) {
        int[] dist = new int[6];

        for (int i = 0; i < dist.length; i++) {
            dist[i] = checkDirection(loc, this.dirVectors[i]);
        }

        return dist;
    }

    private Vector3f getDirectionVector(Vector3f from, Vector3f to) {
        if (from.x < to.x) {
            return this.dirVectors[0];
        }
        if (from.x > to.x) {
            return this.dirVectors[1];
        }
        if (from.y < to.y) {
            return this.dirVectors[2];
        }
        if (from.y > to.y) {
            return this.dirVectors[3];
        }
        if (from.z < to.z) {
            return this.dirVectors[4];
        }
        if (from.z > to.z) {
            return this.dirVectors[5];
        }
        return null;
    }

    private int getNrBars(Vector3f from, Vector3f to) {
        if (from.x != to.x) {
            return (int) Math.abs((from.x - to.x) / 5);
        }
        if (from.y != to.y) {
            return (int) Math.abs((from.y - to.y) / 5);
        }
        if (from.z != to.z) {
            return (int) Math.abs((from.z - to.z) / 5);
        }
        return 0;
    }

    private int checkDirection(Vector3f loc, Vector3f d) {
        int i = 1;
        while (i < 5) {
            if (isConnectionAtLocation(loc.add(d.mult(i)))) {
                break;
            }
            i++;
        }
        return i;
    }

    private Boolean isConnectionAtLocation(Vector3f loc) {
        for (Geometry g : this.connections) {
            if (g.getWorldTranslation().equals(loc)) {
                return true;
            }
        }
        return false;
    }

    private Geometry getConnectionAtLocation(Vector3f loc) {
        for (Geometry g : this.connections) {
            if (g.getWorldTranslation().equals(loc)) {
                return g;
            }
        }
        return null;
    }

    private Boolean isBarAtLocation(Vector3f v1, Vector3f v2) {
        Vector3f location = new Vector3f((v1.x + v2.x) / 2, (v1.y + v2.y) / 2, (v1.z + v2.z) / 2);
        for (Geometry g : this.bars) {
            if (g.getWorldTranslation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    private void makeCannonBall() {
        // Create a cannon ball geometry and attach to scene graph.
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", new ColorRGBA(1,0,0,0.0f));
        m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        ball_geo.setMaterial(m);
        rootNode.attachChild(ball_geo);
        // Position the cannon ball
        ball_geo.setLocalTranslation(new Vector3f(0.0f,10.0f,10.0f));
        // Make the ball physcial with a mass > 0.0f
        ball_phy = new RigidBodyControl(0.2f);
        // Add physical ball to physics space.
        ball_geo.addControl(ball_phy);
        bulletAppStateGame.getPhysicsSpace().add(ball_phy);
        // Accelerate the physcial ball to shoot it.
        ball_phy.setLinearVelocity(new Vector3f(0.0f,-15.0f,0.0f));
    }

    private void initCrossHairs() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+");        // fake crosshairs :)
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    @Override
    public void simpleUpdate(float tpf) {
        checkBreakingJoints();
        updatePlayerLocation();
        vehicle.accelerate(accelerationValue);
        if (vehicle.getPhysicsLocation().getZ() > 50) {
            bulletAppStateGame.setEnabled(false);
            highscore = 120 - bars.size();
            if (highscore > HS.getScore(level)) {
                levelStatus = "New Highscore";
            } else {
                levelStatus = "Succeed";
            }
            try {
                HS.newScore(level, highscore);
            } catch (IOException ex) {
                Logger.getLogger(MainNew.class.getName()).log(Level.SEVERE, null, ex);
            }
            complete();
        }
        if (vehicle.getPhysicsLocation().getY() < -10) {
            bulletAppStateGame.setEnabled(false);
            levelStatus = "Failed";
            highscore = 0;
            complete();
        }
        
         if (vehicle.getPhysicsLocation().getZ() > -15 && !b) {
            makeCannonBall();
            b = true;
         }
        
    }
boolean b = false;
    // if the force on a joints exceeds a certain limit it will break
    private void checkBreakingJoints() {
        ArrayList<HingeJointRef> newJoints = new ArrayList<HingeJointRef>();
        for (int i = 0; i < this.joints.size(); i++) {
            HingeJointRef j = this.joints.get(i);
            if (j.getAppliedImpulse() > 2) {
                bulletAppStateGame.getPhysicsSpace().remove(j);
            } else {
                newJoints.add(j);
            }
        }
        this.joints = newJoints;
    }

    private void updatePlayerLocation() {
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

    private void buildPlayer() {
        float x;
        switch (level) {
            default:
                x = 0;
                break;
            case 1:
                x = -27;
                break;
        }
        matTrain = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTrain.setTexture("ColorMap",
                assetManager.loadTexture("Textures/bmetal.jpg"));
        matTrain2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTrain2.setTexture("ColorMap",
                assetManager.loadTexture("Textures/rmetal.jpg"));
        mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.15f, 0.1f, 0.12f, 10.0f));

        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.6f, 0.8f, 3.8f));
        compoundShape.addChildShape(box, new Vector3f(0, 1, 0));

        //create vehicle node
        Node vehicleNode = new Node("vehicleNode");
        StaticTrainNew s = new StaticTrainNew();
        vehicleNode = s.getSign(vehicleNode, matTrain, matTrain2, mat);
        vehicleNode.scale(0.4f);
        vehicle = new VehicleControl(compoundShape, 30000000);
        vehicleNode.addControl(vehicle);

        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(10000.0f);

        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -0.5f, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1f, 0, 0); // was -1, 0, 0
        float radius = 2.0f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1.6f;
        float zOff = 3.0f;

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        vehicle.setMass(10.0f);
        vehicle.setPhysicsLocation(new Vector3f(2.0f, 3.5f, -30.0f + x));
        rootNode.attachChild(vehicleNode);

        this.bulletAppStateGame.getPhysicsSpace().add(vehicle);
    }
    private NiftyJmeDisplay niftyDisplay;
    private NiftyJmeDisplay hud;

    public void setupHUD() {
        hud = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = hud.getNifty();
        nifty.fromXml("Interface/screen1.xml", "hud", this);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(hud);
    }

    public void closeHUD() {
        guiViewPort.removeProcessor(hud);
    }

    public void setupMenu() {
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen1.xml", "start", this);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);


        // disable the fly cam
//        flyCam.setEnabled(false);
//        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
    }

    public void restartGame() {
        rootNode.detachAllChildren();
        connections.clear();
        bars.clear();
        barsNode.detachAllChildren();
        simpleInitApp();
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
        guiViewPort.removeProcessor(niftyDisplay);
        inputManager.setCursorVisible(false);
        menu = false;
        gameStarted = false;
        b = false;
    }

    public void returnToGame() {
        flyCam.setEnabled(true);
        guiViewPort.removeProcessor(niftyDisplay);
        inputManager.setCursorVisible(false);
        menu = false;
    }

    public void endGame() {
        app.requestClose(true);
    }

    public void selectLevelMenu() {
        guiViewPort.removeProcessor(niftyDisplay);
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen1.xml", "Level", this);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
    }

    public void delectLevelMenu() {
        guiViewPort.removeProcessor(niftyDisplay);
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen1.xml", "Start", this);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
    }

    public void complete() {
        if (menu == false) {
            niftyDisplay = new NiftyJmeDisplay(assetManager,
                    inputManager,
                    audioRenderer,
                    guiViewPort);
            nifty = niftyDisplay.getNifty();
            nifty.fromXml("Interface/screen1.xml", "Complete", this);

            // attach the nifty display to the gui view port as a processor
            guiViewPort.addProcessor(niftyDisplay);


            // disable the fly cam
//        flyCam.setEnabled(false);
//        flyCam.setDragToRotate(true);
            inputManager.setCursorVisible(true);
            flyCam.setEnabled(false);
            menu = true;
        }
    }

    public void startLevel(String i) {
        level = Integer.parseInt(i);
        restartGame();
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public String getStatus() {
        return levelStatus;
    }

    public String getScore() {
        return String.valueOf(highscore);
    }

    private class HingeJointRef extends HingeJoint {

        public Geometry connection;
        public Geometry bar;

        public HingeJointRef(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, Vector3f axisA, Vector3f axisB, Geometry connection, Geometry bar) {
            super(nodeA, nodeB, pivotA, pivotB, axisA, axisB);
            this.connection = connection;
            this.bar = bar;
        }
    }
}