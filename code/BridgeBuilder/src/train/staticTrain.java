/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package train;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author s107200
 */
public class staticTrain {

    Node staticTrain;
    Geometry Plate;
    Box box1;
    Cylinder cyl;
    openCylinder o = new openCylinder();
    Mesh mesh = new Mesh();

    public Node getSign(Material mat1, Material mat2, Material mat3) {
        staticTrain = new Node();
        staticTrain = addBasicPlate(staticTrain, mat2);
        staticTrain = addBasicPlateCylFront(staticTrain, mat1, 1.5f);
        staticTrain = addBasicPlateCylFront(staticTrain, mat1, -1.5f);
        staticTrain = addBasicPlateCylFront2(staticTrain, mat1, 1.5f);
        staticTrain = addBasicPlateCylFront2(staticTrain, mat1, -1.5f);
        staticTrain = addBasicCabin(staticTrain, mat1,-2.75f);
        staticTrain = addBasicCabin(staticTrain, mat1,2.75f);
        staticTrain = addBasicCabin2(staticTrain, mat1);
        staticTrain = addBasicCabinTop(staticTrain, mat1);
        staticTrain = addBasicCabinBottom(staticTrain, mat1);
        staticTrain = addBasicCabinBars(staticTrain, mat1, 0.0f, 0.0f);
        staticTrain = addBasicCabinBars(staticTrain, mat1, -5.4f, 0.0f);
        staticTrain = addBasicCabinBars(staticTrain, mat1, 0.0f, 6.25f);
        staticTrain = addBasicCabinBars(staticTrain, mat1, -5.4f, 6.25f);
        staticTrain = addBasicCyl(staticTrain, mat1);
        staticTrain = addBasicCylFront(staticTrain, mat2);
        staticTrain = addBasicCylFront2(staticTrain, mat1);
        staticTrain = addBasicTopCyl(staticTrain, mat1);
        staticTrain = addBasicTopCylRings(staticTrain, mat2);
        staticTrain = addBasicTopCylRings2(staticTrain, mat2);
        staticTrain = addSmallTopCyl1(staticTrain, mat2);
        staticTrain = addSmallTopCyl2(staticTrain, mat2);
        staticTrain = addWheel(staticTrain, mat3);
        staticTrain = addWheelCyl(staticTrain, mat2);
        return staticTrain;

    }
    
    public Node getWheelsCyl(Material mat2, int rad){
        Node wheels = new Node();
        wheels = addWheelCyl2(wheels, mat2, 0+ rad);
        wheels = addWheelCyl2(wheels, mat2, 30+ rad);
        wheels = addWheelCyl2(wheels, mat2, 60+rad);
        wheels = addWheelCyl2(wheels, mat2, 120+ rad);
        wheels = addWheelCyl2(wheels, mat2, 150+ rad);
        wheels = addWheelCyl2(wheels, mat2, 90+ rad);
        return wheels;
}

    public Node addBasicPlate(Node x, Material mat) {
        box1 = new Box(3.5f, 1f, 10f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(0, 0, 2));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicPlateCylFront(Node x, Material mat,float f1) {
        cyl = new Cylinder(20, 20, 0.5f, 1.0f, true);
        Plate = new Geometry("Box", cyl);
        Plate.setLocalTranslation(new Vector3f(f1, 0.0f, 12.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicPlateCylFront2(Node x, Material mat,float f1) {
        cyl = new Cylinder(20, 20, 0.75f, 0.25f, true);
        Plate = new Geometry("Box", cyl);
        Plate.setLocalTranslation(new Vector3f(f1, 0.0f, 13f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addBasicCabin(Node x, Material mat, float f1) {
        box1 = new Box(0.4f, 4f, 3.75f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(f1, 3.5f, -3.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicCabin2(Node x, Material mat) {
        box1 = new Box(3.0f, 5f, 0.4f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(0.0f, 4.0f, 0.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addBasicCabinTop(Node x, Material mat) {
        box1 = new Box(3.2f, 0.25f, 3.75f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(0, 12.5f, -3.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
     public Node addBasicCabinBottom(Node x, Material mat) {
        box1 = new Box(3.0f, 0.25f, 3.75f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(0, 1.25f, -3.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addBasicCabinBars(Node x, Material mat, float f1, float f2) {
        box1 = new Box(0.5f, 3f, 0.5f);
        Plate = new Geometry("Box", box1);
        Plate.setLocalTranslation(new Vector3f(2.7f + f1, 9.5f, -6.75f + f2));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addBasicCyl(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 3f, 11.25f, true);
        Plate = new Geometry("Box", cyl);
        Plate.setLocalTranslation(new Vector3f(0f, 4f, 5.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addBasicCylFront(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 3.1f, 0.5f, true);
        Plate = new Geometry("Box", cyl);
        Plate.setLocalTranslation(new Vector3f(0f, 4f, 11.0f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicCylFront2(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 1.1f, 0.5f, true);
        Plate = new Geometry("Box", cyl);
        Plate.setLocalTranslation(new Vector3f(0f, 4f, 11.25f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicTopCyl(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 1f, 3.75f, true);
        Plate = new Geometry("Box", cyl);
        Plate.rotate(90 * FastMath.DEG_TO_RAD, 0f, 0f);
        Plate.setLocalTranslation(new Vector3f(0f, 8.5f, 8.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicTopCylRings(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 1.25f, 0.25f, true);
        Plate = new Geometry("Box", cyl);
        Plate.rotate(90 * FastMath.DEG_TO_RAD, 0f, 0f);
        Plate.setLocalTranslation(new Vector3f(0f, 7.0f, 8.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }
    
    public Node addBasicTopCylRings2(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 1.25f, 0.25f, true);
        Plate = new Geometry("Box", cyl);
        Plate.rotate(90 * FastMath.DEG_TO_RAD, 0f, 0f);
        Plate.setLocalTranslation(new Vector3f(0f, 10.375f, 8.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addSmallTopCyl1(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 0.5f, 0.75f, true);
        Plate = new Geometry("Box", cyl);
        Plate.rotate(90 * FastMath.DEG_TO_RAD, 0f, 0f);
        Plate.setLocalTranslation(new Vector3f(0f, 7.2f, 5.5f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addSmallTopCyl2(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 0.5f, 0.75f, true);
        Plate = new Geometry("Box", cyl);
        Plate.rotate(90 * FastMath.DEG_TO_RAD, 0f, 0f);
        Plate.setLocalTranslation(new Vector3f(0f, 7.2f, 3.0f));
        Plate.setMaterial(mat);
        x.attachChild(Plate);
        return x;
    }

    public Node addWheel(Node x, Material mat) {
        mesh = new Mesh();
        mesh = o.getCylinder(1.5f, 0.9f, 0.25f, 150);
        Geometry geo = new Geometry("OurMesh", mesh); // using our custom mesh object
        Geometry geo1 = new Geometry("OurMesh", mesh); // using our custom mesh object
        Geometry geo2 = new Geometry("OurMesh", mesh); // using our custom mesh object
        Geometry geo3 = new Geometry("OurMesh", mesh); // using our custom mesh object
        mesh = o.getCylinder(3.0f, 2.2f, 0.25f, 150);
        Geometry geo4 = new Geometry("OurMesh", mesh); // using our custom mesh object
        Geometry geo5 = new Geometry("OurMesh", mesh); // using our custom mesh object
        geo.setMaterial(mat);
        geo1.setMaterial(mat);
        geo2.setMaterial(mat);
        geo3.setMaterial(mat);
        geo4.setMaterial(mat);
        geo5.setMaterial(mat);
        geo.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo1.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo2.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo3.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo4.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo5.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo.setLocalTranslation(3.5f, -0.5f, 8.5f);
        geo1.setLocalTranslation(3.5f, -0.5f, 5.0f);
        geo2.setLocalTranslation(-3.75f, -0.5f, 8.5f);
        geo3.setLocalTranslation(-3.75f, -0.5f, 5.0f);
        geo4.setLocalTranslation(3.5f, 0.5f, -3.0f);
        geo5.setLocalTranslation(-3.75f, 0.5f, -3.0f);
        x.attachChild(geo);
        x.attachChild(geo1);
        x.attachChild(geo2);
        x.attachChild(geo3);
        x.attachChild(geo4);
        x.attachChild(geo5);
        return x;
    }

    public Node addWheelCyl(Node x, Material mat) {
        cyl = new Cylinder(20, 20, 0.2f, 0.5f, true);
        Geometry geo = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo1 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo2 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo3 = new Geometry("OurMesh", cyl); // using our custom mesh object
        cyl = new Cylinder(20, 20, 0.5f, 0.5f, true);
        Geometry geo4 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo5 = new Geometry("OurMesh", cyl); // using our custom mesh object
        geo.setMaterial(mat);
        geo1.setMaterial(mat);
        geo2.setMaterial(mat);
        geo3.setMaterial(mat);
        geo4.setMaterial(mat);
        geo5.setMaterial(mat);
        geo.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo1.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo2.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo3.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo4.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo5.rotate(0f, 90 * FastMath.DEG_TO_RAD, 0f);
        geo.setLocalTranslation(3.5f, -0.5f, 8.5f);
        geo1.setLocalTranslation(3.5f, -0.5f, 5.0f);
        geo2.setLocalTranslation(-3.75f, -0.5f, 8.5f);
        geo3.setLocalTranslation(-3.75f, -0.5f, 5.0f);
        geo4.setLocalTranslation(3.5f, 0.5f, -3.0f);
        geo5.setLocalTranslation(-3.75f, 0.5f, -3.0f);
        x.attachChild(geo);
        x.attachChild(geo1);
        x.attachChild(geo2);
        x.attachChild(geo3);
        x.attachChild(geo4);
        x.attachChild(geo5);
        return x;
    }

    public Node addWheelCyl2(Node x, Material mat, int rad) {
        cyl = new Cylinder(20, 20, 0.075f, 2.0f, true);
        Geometry geo = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo1 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo2 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo3 = new Geometry("OurMesh", cyl); // using our custom mesh object
        cyl = new Cylinder(20, 20, 0.15f, 5.0f, true);
        Geometry geo4 = new Geometry("OurMesh", cyl); // using our custom mesh object
        Geometry geo5 = new Geometry("OurMesh", cyl); // using our custom mesh object
        geo.setMaterial(mat);
        geo1.setMaterial(mat);
        geo2.setMaterial(mat);
        geo3.setMaterial(mat);
        geo4.setMaterial(mat);
        geo5.setMaterial(mat);
        geo.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo1.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo2.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo3.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo4.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo5.rotate(rad * FastMath.DEG_TO_RAD, 0f, 0f);
        geo.setLocalTranslation(3.5f, -0.5f, 8.5f);
        geo1.setLocalTranslation(3.5f, -0.5f, 5.0f);
        geo2.setLocalTranslation(-3.75f, -0.5f, 8.5f);
        geo3.setLocalTranslation(-3.75f, -0.5f, 5.0f);
        geo4.setLocalTranslation(3.5f, 0.5f, -3.0f);
        geo5.setLocalTranslation(-3.5f, 0.5f, -3.0f);
        x.attachChild(geo);
        x.attachChild(geo1);
        x.attachChild(geo2);
        x.attachChild(geo3);
        x.attachChild(geo4);
        x.attachChild(geo5);
        return x;
    }
}
