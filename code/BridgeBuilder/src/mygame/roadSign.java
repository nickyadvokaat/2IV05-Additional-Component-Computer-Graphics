/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author s107200
 */
public class roadSign{

    Node roadSign;
    Geometry Board;
    Box box1;
    Cylinder cyl;

    public Node getSign(Material mat1, Material mat2) {
        roadSign = new Node();
        roadSign = addBoard(roadSign,mat1);
        roadSign = addPlatue(roadSign,mat2);
        roadSign = addBars(roadSign, mat2,0.5f);
        roadSign = addBars(roadSign, mat2,3.5f);
        return roadSign;
    }

    public Node addBoard(Node x, Material mat) {
        box1 = new Box(3.5f, 0.75f,0.1f);
        Board = new Geometry("Box", box1);
        Board.setLocalTranslation(new Vector3f(2, 3, -3));
        Board.setMaterial(mat);
        x.attachChild(Board);
        return x;
    }
    
    public Node addPlatue(Node x, Material mat) {
        box1 = new Box(2.5f, 0.5f,1.0f);
        Board = new Geometry("Box", box1);
        Board.setLocalTranslation(new Vector3f(2, 0, -3));
        Board.setMaterial(mat);
        x.attachChild(Board);
        return x;
    }
    
    public Node addBars(Node x, Material mat, float i) {
        cyl = new Cylinder(10,10,0.1f,3f);
        Board = new Geometry("Box", cyl);
        Board.rotate(90*FastMath.DEG_TO_RAD, 0f, 0f);
        Board.setLocalTranslation(new Vector3f(i, 1.5f, -3));
        Board.setMaterial(mat);
        x.attachChild(Board);
        return x;
    }
    

}
