/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author s107200
 */
public class TrainTrack {

    Node track;
    Geometry Wood;
    Box box1;

    public Node getTrack(int x, Material mat1, Material mat2) {
        track = new Node();
        for (int i = 0; i < x; i++) {
            track = addWood(track, mat1, i);
        }
        track = addRail(track,mat2,x, 3.8f);
        track = addRail(track,mat2,x,0.2f);
        return track;
    }

    public Node addWood(Node x, Material mat, int i) {
        box1 = new Box(3, 0.25f,1.5f);
        Wood = new Geometry("Box", box1);
        Wood.setLocalTranslation(new Vector3f(2, 0, 3*i));
        Wood.setMaterial(mat);
        x.attachChild(Wood);
        return x;
    }
    
    public Node addRail(Node x, Material mat, int i, float a) {
        box1 = new Box(0.25f, 0.25f,1.5f*i);
        Wood = new Geometry("Box", box1);
        Wood.setLocalTranslation(new Vector3f(a, 0.5f, 1.5f*i-1.5f));
        Wood.setMaterial(mat);
        x.attachChild(Wood);
        return x;
    }
}
