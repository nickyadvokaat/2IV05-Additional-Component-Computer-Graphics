package train;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author nicky
 */
public class tunnel {

    public static Node getTunnel(AssetManager assetManager) {
        Node node = new Node();

        Material side_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/brick.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        side_mat.setTexture("ColorMap", tex);

        Material bottom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/stone.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        bottom_mat.setTexture("ColorMap", tex2);

        Box sideBottom = new Box(5f, 1f, 1f);
        Box side = new Box(5f, 2f, 0.5f);
        Box roofBox = new Box(5f, 0.5f, 3.0f);

        Geometry sideBottomLeft = new Geometry("", sideBottom);
        sideBottomLeft.setMaterial(bottom_mat);
        sideBottomLeft.setLocalTranslation(new Vector3f(0, 1f, -2.5f));
        node.attachChild(sideBottomLeft);

        Geometry sideBottomRight = new Geometry("", sideBottom);
        sideBottomRight.setMaterial(bottom_mat);
        sideBottomRight.setLocalTranslation(new Vector3f(0, 1f, 2.5f));
        node.attachChild(sideBottomRight);

        Geometry sideLeft = new Geometry("", side);
        sideLeft.setMaterial(side_mat);
        sideLeft.setLocalTranslation(new Vector3f(0, 4f, -2.5f));
        node.attachChild(sideLeft);

        Geometry sideRight = new Geometry("", side);
        sideRight.setMaterial(side_mat);
        sideRight.setLocalTranslation(new Vector3f(0, 4f, 2.5f));
        node.attachChild(sideRight);
        
        Geometry roof = new Geometry("", roofBox);
        roof.setMaterial(side_mat);
        roof.setLocalTranslation(new Vector3f(0, 6.5f, 0f));
        node.attachChild(roof);
        
        return node;
    }
}
