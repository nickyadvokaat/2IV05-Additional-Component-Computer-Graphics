/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package train;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 *
 * @author s107200
 */
public class openCylinder {

    
    public Mesh getCylinder(float f1, float f2, float f3, int i){
        Mesh m = new Mesh();
        Vector3f [] vertices = new Vector3f[4*i];
        float rate = FastMath.TWO_PI / i;
        float angle = 0;
        for (int j = 0; j < i; j++)
              {
                float x = FastMath.cos(angle);
                float y = FastMath.sin(angle); 
                vertices[j] = new Vector3f(x*f1,y*f1,0);
                vertices[j+i] = new Vector3f(x*f1,y*f1,f3);
                vertices[j+2*i] = new Vector3f(x*f2,y*f2,0);
                vertices[j+3*i] = new Vector3f(x*f2,y*f2,f3);
                angle += rate;
                }
        m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(getIndexes(i)));
        m.updateBound();
        return m;
    }
    
    public int[] getIndexes(int i){
        int p = 0;
        int[] indexes = new int[24*i+3];
        
        for(int j = 0; j < i;j++){
            indexes[p+0] = j;
            indexes[p+1] = j+1;
            indexes[p+2] = j+i;
            p = p+3;
        }
        for(int j = 0; j < i;j++){
            indexes[p+2] = j;
            indexes[p+0] = j+i;
            indexes[p+1] = j+i-1;
            p = p+3;
        }
         for(int j = 2*i; j <3*i;j++){
            indexes[p+2] = j;
            indexes[p+1] = j+1;
            indexes[p+0] = j+i;
            p = p+3;
        }
        for(int j = 2*i; j < 3*i;j++){
            indexes[p+0] = j;
            indexes[p+2] = j+i;
            indexes[p+1] = j+i-1;
            p = p+3;
        }
        for(int j = 0; j < i;j++){
            indexes[p+2] = j;
            indexes[p+1] = j+1;
            indexes[p+0] = j+2*i;
            p = p+3;
        }
        for(int j = 0; j < i;j++){
            indexes[p+2] = j;
            indexes[p+1] = j+2*i;
            indexes[p+0] = j+2*i-1;
            p = p+3;
        }
        
        for(int j = i; j < 2*i;j++){
            indexes[p+0] = j;
            indexes[p+1] = j+1;
            indexes[p+2] = j+2*i;
            p = p+3;
        }
        for(int j = i; j < 2*i;j++){
            indexes[p+0] = j;
            indexes[p+1] = j+2*i;
            indexes[p+2] = j+2*i-1;
            p = p+3;
        }
        indexes[p] = 2*i-1;
        indexes[p+1]=i;
        indexes[p+2]=3*i-1;
        return indexes;
    }
}
