package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // sky color
        viewPort.setBackgroundColor(ColorRGBA.Cyan);

        // cam speed
        flyCam.setMoveSpeed(30.0f);

        // load terrain
        Spatial terrain = assetManager.loadModel("Scenes/Level1.j3o");
        rootNode.attachChild(terrain);
        TerrainLodControl lodControl = ((Node) terrain).getControl(TerrainLodControl.class);
        if (lodControl != null) {
            lodControl.setCamera(getCamera());
        }        


        Box box = new Box(10, 10, 10);
        Geometry gm1 = new Geometry("Box", box);
        gm1.setLocalTranslation(new Vector3f(1, 3, 1));
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Gray);
        gm1.setMaterial(mat2);
        
        rootNode.attachChild(gm1);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
