package dmace.gesture_recognition;

import java.util.ArrayList;

public class RotationNormalizer {
	
	private static final double EPSILON = 1e-10;
	
	public static ArrayList<Vector3> normalizeRotation(double[] fftCoefficients, ArrayList<Vector3> normalizedData) {
		double[] y=new double[128];
		for(int j=0; j<128; j++) {
			double x=(double)j/128*Math.PI*2;
			y[j]=0;
			//find y'' for given x
			for(int k=1; k<fftCoefficients.length/2; k++) {
				y[j]+=(k*k*fftCoefficients[(k-1)*2])*Math.cos(k*x)+k*k*(fftCoefficients[(k-1)*2+1])*Math.sin(k*x);
				//concav[j]+=(k*k*fftCoefficients[(k-1)*2])*Math.cos(k*x)+(k*k*fftCoefficients[(k-1)*2+1])*Math.sin(k*x);
			}
		}
		
		ArrayList<Vector3> gestureSegments=new ArrayList<Vector3>();
		ArrayList<Integer> gestureSegmentBreaks=new ArrayList<Integer>();
		Vector3 sum=Vector3.zero();
		for(int j=1; j<128; j++) {
			sum=Vector3.add(sum,normalizedData.get(j));
			//System.out.print(normalizedData.get(j)+"  ");
			if(Math.signum(y[j])!=Math.signum(y[j-1])  || j==127 ) { //pt of inflection
				gestureSegmentBreaks.add(j);
				gestureSegments.add(sum);
				//System.out.println("jk"+sum);
				sum=Vector3.zero();
			}
		}
		Vector3 planeEq=findXYPlaneAngles(gestureSegments);
		ArrayList<Vector3> rotationNormalizedDataXY=rotationNormalizePointsXY(normalizedData,planeEq);
		double slope=getBestFitLine(rotationNormalizedDataXY);
		//System.out.println(slope);
		ArrayList<Vector3> rotationNormalizedDataZ=rotationNormalizePointsZ(rotationNormalizedDataXY,slope);
		return rotationNormalizedDataZ;
	}
	
	private static Vector3 getPlaneNormalVector(Vector3 planeEq) {
		double m1=planeEq.x, m2=planeEq.y;
	    double normx=Math.sqrt(1/(1+m1*m1));
	    double normy=Math.sqrt(1/(1+m2*m2));
	    double normz=m1*normx+m2*normy;
	    Vector3 planeNormVector = new Vector3(normx,normy,normz);//<A,B,C>
	    planeNormVector=Vector3.normalize(planeNormVector);
	    return planeNormVector;
	}
	
	private static ArrayList<Vector3> rotationNormalizePointsXY(ArrayList<Vector3> original, Vector3 planeEq) {
		ArrayList<Vector3> out=new ArrayList<Vector3>();
		for(int i=0; i<original.size(); i++) {
			double d=planeEq.z;
			Vector3 p=original.get(i), n=new Vector3(planeEq.x, planeEq.y, -1);
			//Vector3 p=new Vector3(1,2,1), n=new Vector3(1, 1, 1);
			double t = (Vector3.dot(p,n)+d)/Vector3.dot(n,n);
			Vector3 q=Vector3.sub( p, Vector3.mult(n,t) ); //projection on plane
			//double samez=(planeEq.x*p.x+planeEq.y*p.y-p.z+planeEq.z)/Vector3.magnitude(new Vector3(planeEq.x,planeEq.y,-1));
			double nz=Vector3.magnitude(Vector3.sub(q,p))*(p.z<q.z?-1:1);
			Vector2 nxy=getPlanar2DCoords(q, n, d);
			out.add(new Vector3(nxy.x,nxy.y,nz));
		}
		return out;
	}
	
	private static double getBestFitLine(ArrayList<Vector3> gestureSegments) {
		double bestError=Double.MAX_VALUE;
		int bestValue=0;
		for(int i=0; i<16; i++) {
			double angle=i*Math.PI/8, m=Math.tan(angle);
			double error=0;
			for(int j=0; j<128; j++) {
				double cx=gestureSegments.get(j).x, cy=gestureSegments.get(j).y;
				error+=Math.abs(angle-Math.atan2(cy,cx));//can try Math.sqrt(cx*cx+cy*cy)*
			}
			if(error<bestError) {bestError=error; bestValue=i; }
		}
		return bestValue*Math.PI/8;
	}
	
	private static ArrayList<Vector3> rotationNormalizePointsZ(ArrayList<Vector3> original, double rotation) {
		ArrayList<Vector3> out=new ArrayList<Vector3>();
		for(Vector3 p: original) {
			double theta=Math.atan2(p.y,p.x);
			double rad=Math.sqrt(p.x*p.x+p.y*p.y);
			double ntheta=theta+rotation;
			double nx=Math.cos(ntheta)*rad, ny=Math.sin(ntheta)*rad;
			out.add(new Vector3(nx,ny,p.z));
		}
		return out;
	}
	
	private static Vector2 getPlanar2DCoords(Vector3 pt, Vector3 n, double d) {
		Vector3 origin=new Vector3(0,0,d);
		Vector3 newVector=Vector3.sub(pt,origin);
		double dx=Math.sqrt((double)1/(1+n.x*n.x));
		double dz=n.x*dx;
		Vector3 xAxis=new Vector3(dx,0,dz);
		double dot=Vector3.dot(Vector3.normalize(newVector),xAxis);
		double radius=Vector3.magnitude(newVector);
		double nx=radius*dot, ny=radius*Math.sqrt(1-dot*dot);
		Vector3 cross=Vector3.cross(newVector,xAxis);
		if(cross.z>0) { ny*=-1;  }
		return new Vector2(nx,ny);
	}
	
	// Gaussian elimination with partial pivoting
    public static double[] lsolve(double[][] A, double[] b) {
        int N  = b.length;

        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double   t    = b[p]; b[p] = b[max]; b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) <= EPSILON) {
                throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }
	
	private static Vector3 findXYPlaneAngles(ArrayList<Vector3> d) {
     	int N = 3;
        double[][] A = { { sx2(d),sxy(d),sx(d) },
                         { sxy(d),sy2(d),sy(d) },
                         { sx(d),sy(d),s1(d) }
                       };
        double[] b = { sxz(d),syz(d),sz(d) };
        double[] planeEq = lsolve(A, b);//Ax+By+C=z
        return new Vector3(planeEq[0],planeEq[1],planeEq[2]);
    }
	
    private static double sx2(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.x*d.x;
    	}
    	return out;
    }
    
    private static double sx(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.x;
    	}
    	return out;
    }
    
    private static double sy2(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.y*d.y;
    	}
    	return out;
    }
    
    private static double sy(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.y;
    	}
    	return out;
    }
    
    private static double sxy(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.x*d.y;
    	}
    	return out;
    }
    
    private static double s1(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=1;
    	}
    	return out;
    }
    
    private static double sxz(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.x*d.z;
    	}
    	return out;
    }
    
    private static double syz(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.y*d.z;
    	}
    	return out;
    }
    
    private static double sz(ArrayList<Vector3> dp) {
    	double out=0;
    	for(Vector3 d:dp) {
    		out+=d.z;
    	}
    	return out;
    }
}
