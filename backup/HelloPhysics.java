package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
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
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;

public class HelloPhysics extends SimpleApplication {

    public static void main(String args[]) {
        HelloPhysics app = new HelloPhysics();
        app.start();
    }
    /**
     * Prepare the Physics Application State (jBullet)
     */
    private BulletAppState bulletAppState;
    /**
     * Prepare Materials
     */
    Material wall_mat;
    Material stone_mat;
    Material floor_mat;
    /**
     * Prepare geometries and physical nodes for bricks and cannon balls.
     */
    private static final Box box;
    private static final Box connection;
    private static final Box barX;
    private static final Box barY;
    private static final Box barZ;
    private RigidBodyControl ball_phy;
    private static final Sphere sphere;
    private RigidBodyControl floor_phy;
    private static final Box floor;
    /**
     * dimensions used for bricks and wall
     */
    private static final float brickLength = 0.48f;
    private static final float brickWidth = 0.24f;
    private static final float brickHeight = 0.12f;
    private Node clickablesNode;
    private Node connectionsNode;
    private Node barsNode;
    private Node targetsNode;
    private Geometry prevClickedGeometry;
    private boolean gameStarted = false;

    static {
        /**
         * Initialize the cannon ball geometry
         */
        sphere = new Sphere(32, 32, 0.4f, true, false);
        sphere.setTextureMode(TextureMode.Projected);
        /**
         * Initialize the brick geometry
         */
        box = new Box(brickLength, brickHeight, brickWidth);
        box.scaleTextureCoordinates(new Vector2f(1f, .5f));
        /**
         * Initialize the floor geometry
         */
        floor = new Box(100f, 0.1f, 100f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));

        connection = new Box(0.5f, 0.5f, 0.5f);
        connection.scaleTextureCoordinates(new Vector2f(1f, .5f));

        barX = new Box(2f, 0.5f, 0.5f);
        barX.scaleTextureCoordinates(new Vector2f(1f, .5f));

        barY = new Box(0.5f, 2, 0.5f);
        barY.scaleTextureCoordinates(new Vector2f(1f, .5f));

        barZ = new Box(0.5f, 0.5f, 2);
        barZ.scaleTextureCoordinates(new Vector2f(1f, .5f));
    }

    @Override
    public void simpleInitApp() {
        clickablesNode = new Node("clickablesNode");
        rootNode.attachChild(clickablesNode);
        connectionsNode = new Node("connectionsNode");
        clickablesNode.attachChild(connectionsNode);
        barsNode = new Node("barsNode");
        clickablesNode.attachChild(barsNode);
        targetsNode = new Node("targetsNode");
        clickablesNode.attachChild(targetsNode);


        flyCam.setMoveSpeed(30.0f);
        /**
         * Set up Physics Game
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        /**
         * Configure cam to look at scene
         */
        cam.setLocation(new Vector3f(0, 4f, 6f));
        cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
        /**
         * Add InputManager action: Left click triggers shooting.
         */
        inputManager.addMapping("shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        inputManager.addMapping("start",
                new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "start");
        inputManager.addMapping("play",
                new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(actionListener, "play");

        //pause
        bulletAppState.setSpeed(0);

        initMaterials();
        initFloor();
        initCrossHairs();

        //buildTower();

        initConnections();
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("play")) {
                targetsNode.detachAllChildren();
                bulletAppState.setSpeed(1);
                gameStarted = true;
            }
            if (name.equals("shoot") && !keyPressed) {
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

                        System.out.println("clicked " + geometry.getName());

                        switch ((Integer) (geometry.getUserData("type"))) {
                            case 0:
                            case 1:
                                // reset target
                                targetsNode.detachAllChildren();

                                //Material mat2 = new Material(assetManager,
                                //         "Common/MatDefs/Misc/Unshaded.j3md");
                                // mat2.setColor("Color", ColorRGBA.Red);
                                //geometry.setMaterial(mat2);

                                if (prevClickedGeometry == null) {
                                    prevClickedGeometry = geometry;
                                    Vector3f translation = closest.getGeometry().getLocalTransform().getTranslation();
                                    addTarget(new Vector3f(translation.x, translation.y + 5, translation.z));
                                    addTarget(new Vector3f(translation.x, translation.y - 5, translation.z));
                                    addTarget(new Vector3f(translation.x + 5, translation.y, translation.z));
                                    addTarget(new Vector3f(translation.x, translation.y, translation.z + 5));
                                    addTarget(new Vector3f(translation.x - 5, translation.y, translation.z));
                                    addTarget(new Vector3f(translation.x, translation.y, translation.z - 5));
                                } else {
                                    if (geometry != prevClickedGeometry) {
                                        if (prevClickedGeometry.getWorldTranslation().x > geometry.getWorldTranslation().x
                                                || prevClickedGeometry.getWorldTranslation().y > geometry.getWorldTranslation().y
                                                || prevClickedGeometry.getWorldTranslation().z > geometry.getWorldTranslation().z) {
                                            addBar(geometry, prevClickedGeometry);
                                        } else {
                                            addBar(prevClickedGeometry, geometry);
                                        }
                                    }
                                    prevClickedGeometry = null;
                                    targetsNode.detachAllChildren();
                                }
                                break;
                            case 2: // target   

                                addConnection(geometry.getLocalTranslation());

                                if (prevClickedGeometry.getWorldTranslation().x > connections.get(connections.size() - 1).getWorldTranslation().x
                                        || prevClickedGeometry.getWorldTranslation().y > connections.get(connections.size() - 1).getWorldTranslation().y
                                        || prevClickedGeometry.getWorldTranslation().z > connections.get(connections.size() - 1).getWorldTranslation().z) {
                                    addBar(connections.get(connections.size() - 1), prevClickedGeometry);
                                } else {
                                    addBar(prevClickedGeometry, connections.get(connections.size() - 1));
                                }

                                // reset target
                                targetsNode.detachAllChildren();
                                prevClickedGeometry = null;
                                break;
                            case 3: // building block
                                // reset target
                                targetsNode.detachAllChildren();
                                prevClickedGeometry = null;
                                break;

                        }
                    } else {
                        // reset target
                        targetsNode.detachAllChildren();
                        prevClickedGeometry = null;
                    }
                }
            }
            if (gameStarted) {
                if (name.equals("shoot") && !keyPressed) {
                }
            } else if (name.equals("shoot") && !keyPressed) {
            }
        }
    };

    public void initMaterials() {
        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        wall_mat.setTexture("ColorMap", tex);

        stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        stone_mat.setTexture("ColorMap", tex2);

        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
    }

    public void initFloor() {
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -0.1f, 0);
        this.rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
    }
    int blockNameCounter = 0;
    int connectionNameCounter = 0;
    int barNameCounter = 0;
    ArrayList<Geometry> bricks = new ArrayList<Geometry>();
    ArrayList<HingeJoint> joints = new ArrayList<HingeJoint>();
    ArrayList<Geometry> connections = new ArrayList<Geometry>();
    ArrayList<Geometry> bars = new ArrayList<Geometry>();
    ArrayList<Geometry> targets = new ArrayList<Geometry>();
    ArrayList<HingeJoint> jointsNew = new ArrayList<HingeJoint>();

    private void initConnections() {
        addGroundConnection(new Vector3f(0, 0.5f, 0));
        addGroundConnection(new Vector3f(5, 0.5f, 0));
        addGroundConnection(new Vector3f(0, 0.5f, 5));
        addGroundConnection(new Vector3f(5, 0.5f, 5));
    }

    private void addGroundConnection(Vector3f location) {
        Geometry connectionGeometry = new Geometry("connection" + this.connectionNameCounter++, this.connection);
        connectionGeometry.setMaterial(stone_mat);
        connectionGeometry.setLocalTranslation(location);
        connectionGeometry.setUserData("type", 0);

        this.connectionsNode.attachChild(connectionGeometry);

        RigidBodyControl rbc = new RigidBodyControl(0f);
        connectionGeometry.addControl(rbc);
        this.bulletAppState.getPhysicsSpace().add(rbc);

        this.connections.add(connectionGeometry);
    }

    private void addConnection(Vector3f location) {
        Geometry connectionGeometry = new Geometry("connection" + this.connectionNameCounter++, this.connection);
        connectionGeometry.setMaterial(stone_mat);
        connectionGeometry.setLocalTranslation(location);
        connectionGeometry.setUserData("type", 1);

        this.connectionsNode.attachChild(connectionGeometry);

        RigidBodyControl rbc = new RigidBodyControl(1f);
        connectionGeometry.addControl(rbc);
        this.bulletAppState.getPhysicsSpace().add(rbc);

        this.connections.add(connectionGeometry);
    }

    private void addBar(Geometry connection1, Geometry connection2) {
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
        barGeometry.setMaterial(wall_mat);
        barGeometry.setLocalTranslation(location);
        barGeometry.setUserData("type", 3);

        this.barsNode.attachChild(barGeometry);

        RigidBodyControl rbc = new RigidBodyControl(4f);
        barGeometry.addControl(rbc);
        this.bulletAppState.getPhysicsSpace().add(rbc);

        this.bars.add(barGeometry);
        System.out.println(direction);
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

        // add first joints
        HingeJoint joint1 = new HingeJoint(barGeometry.getControl(RigidBodyControl.class),
                connection1.getControl(RigidBodyControl.class),
                v1,
                v2,
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z);
        joint1.setLimit(0, 0);
        bulletAppState.getPhysicsSpace().add(joint1);
        this.jointsNew.add(joint1);

        // add second joints
        HingeJoint joint2 = new HingeJoint(barGeometry.getControl(RigidBodyControl.class),
                connection2.getControl(RigidBodyControl.class),
                v3,
                v4,
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z);
        joint2.setLimit(0, 0);
        bulletAppState.getPhysicsSpace().add(joint2);
        this.jointsNew.add(joint2);
    }

    private void addTarget(Vector3f location) {
        Geometry targetGeometry = new Geometry("target" + this.connectionNameCounter++, this.connection);
        targetGeometry.setMaterial(floor_mat);
        targetGeometry.setLocalTranslation(location);
        targetGeometry.setUserData("type", 2);

        this.targetsNode.attachChild(targetGeometry);

        this.targets.add(targetGeometry);
    }

    private void addBrick(Vector3f location) {
        Quaternion rotateZ90 = new Quaternion();
        rotateZ90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 0, 1));

        Geometry brick_geo = new Geometry("brick" + this.blockNameCounter++, this.box);
        brick_geo.setMaterial(wall_mat);
        brick_geo.setLocalTranslation(location);
        brick_geo.setLocalRotation(rotateZ90);

        this.rootNode.attachChild(brick_geo);

        RigidBodyControl rbc = new RigidBodyControl(2f);
        brick_geo.addControl(rbc);
        this.bulletAppState.getPhysicsSpace().add(rbc);

        this.bricks.add(brick_geo);
        if (this.bricks.size() > 1) {
            HingeJoint joint = new HingeJoint(this.bricks.get(this.bricks.size() - 2).getControl(RigidBodyControl.class),
                    this.bricks.get(this.bricks.size() - 1).getControl(RigidBodyControl.class),
                    new Vector3f(this.brickLength, 0f, 0f),
                    new Vector3f(-this.brickLength, 0f, 0f),
                    Vector3f.UNIT_Z,
                    Vector3f.UNIT_Z);
            joint.setLimit(0, 0);
            bulletAppState.getPhysicsSpace().add(joint);
            joints.add(joint);
        }
    }

    private void buildTower() {
        for (int i = 0; i < 6; i++) {
            Vector3f loc = new Vector3f(0, this.brickLength + 2 * i * this.brickLength, 0);
            addBrick(loc);
        }
    }

    public void makeCannonBall() {
        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
        rootNode.attachChild(ball_geo);
        /**
         * Position the cannon ball
         */
        ball_geo.setLocalTranslation(cam.getLocation());
        /**
         * Make the ball physcial with a mass > 0.0f
         */
        ball_phy = new RigidBodyControl(5f);
        /**
         * Add physical ball to physics space.
         */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        /**
         * Accelerate the physcial ball to shoot it.
         */
        ball_phy.setLinearVelocity(cam.getDirection().mult(25));
    }

    protected void initCrossHairs() {
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
        ArrayList<HingeJoint> newJoints = new ArrayList<HingeJoint>();
        for (int i = 0; i < this.joints.size(); i++) {
            HingeJoint j = this.joints.get(i);
            if (j.getAppliedImpulse() > 4) {
                bulletAppState.getPhysicsSpace().remove(j);
            } else {
                newJoints.add(j);
            }
        }
        this.joints = newJoints;

        newJoints = new ArrayList<HingeJoint>();
        for (int i = 0; i < this.jointsNew.size(); i++) {
            HingeJoint j = this.jointsNew.get(i);
            if (j.getAppliedImpulse() > 14) {
                bulletAppState.getPhysicsSpace().remove(j);
            } else {
                newJoints.add(j);
            }
        }
        this.jointsNew = newJoints;
    }
}