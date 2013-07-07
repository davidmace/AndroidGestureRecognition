package dmace.gesture_recognition;

public class Vector3 {

	double x,y,z;
	
	public Vector3(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public static Vector3 zero() {
		return new Vector3(0,0,0);
	}
	
	public static Vector3 unitX() {
		return new Vector3(1,0,0);
	}
	
	public static Vector3 unitY() {
		return new Vector3(0,1,0);
	}
	
	public static Vector3 unitZ() {
		return new Vector3(0,0,1);
	}
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x+b.x,a.y+b.y,a.z+b.z);
	}
	
	public static Vector3 sub(Vector3 a, Vector3 b) {
		return new Vector3(a.x-b.x,a.y-b.y,a.z-b.z);
	}
	
	public static double difference(Vector3 a, Vector3 b) {
		double dx=a.x-b.x, dy=a.y-b.y, dz=a.z-b.z;
		return Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	
	public static Vector3 div(Vector3 a, double b) {
		return new Vector3(a.x/b,a.y/b,a.z/b);
	}
	
	public static Vector3 mult(Vector3 a,double b) {
		return new Vector3(a.x*b,a.y*b,a.z*b);
	}
	
	public static double magnitude(Vector3 a) {
		return Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
	}
	
	public static Vector3 normalize(Vector3 a) {
		double mag=Vector3.magnitude(a);
		return Vector3.div(a, mag);
	}
	
	
	public static double dot(Vector3 a, Vector3 b) {
		return a.x*b.x+a.y*b.y+a.z*b.z;
	}
	
	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
	}
	
	public static double distance(Vector3 a, Vector3 b) {
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2)+Math.pow(a.z-b.z,2));
	}
	
	public String toString() {
		String sx=Double.toString(x); while(sx.length()<7) sx+="0"; if(sx.length()>7) sx=sx.substring(0,7);
		String sy=Double.toString(y); while(sy.length()<7) sy+="0"; if(sy.length()>7) sy=sy.substring(0,7);
		String sz=Double.toString(z); while(sz.length()<7) sz+="0"; if(sz.length()>7) sz=sz.substring(0,7);
		return sx+" "+sy+" "+sz;
	}
	
	/*public void reduce(int amount) {
		if(x>=amount) x-=amount;
		if(x<=-amount) x+=amount;
		if(y>=amount) y-=amount;
		if(y<=-amount) y+=amount;
		if(z>=amount) z-=amount;
		if(z<=-amount) z+=amount;
	}
	
	public Vector3 quantize(int threshold) {
		return new Vector3( ((int)x)/threshold*threshold , ((int)y)/threshold*threshold , ((int)z)/threshold*threshold);	
	}*/
}