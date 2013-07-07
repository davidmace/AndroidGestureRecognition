package dmace.gesture_recognition;

import java.util.ArrayList;


public class PreProcessor {
	
	public static ArrayList<Vector3> normalizeData(ArrayList<Vector3> in) {
		int start=0, end=0;
		int threshold=4;
		for(int i=0; i<in.size()-4; i++) {
			int sum=0;
			for(int j=i; j<i+4; j++) {
				sum+=Vector3.distance(in.get(j), in.get(j+1));
			}
			if(sum/3>threshold) { start=i+2; break; }
		}
		
		for(int i=in.size()-1; i>=4; i--) {
			int sum=0;
			for(int j=i; j>i-4; j--) {
				sum+=Vector3.distance(in.get(j), in.get(j-1));
			}
			if(sum/3>threshold) { end=i-2; break; }
		}
		//System.out.println(start+" "+end+" "+in.size());
		
		ArrayList<Vector3> step1=new ArrayList<Vector3>(), out=new ArrayList<Vector3>();
		/*double dframe=(double)(end-start)/128;
		Vector3 sum=Vector3.zero();
		for(int i=0; i<in.size(); i++) sum=Vector3.add(sum,in.get(i));
		for(int i=0; i<128; i++) {
			double curframe=start+dframe*i;
			step1.add(in.get((int)curframe));
		}*/
		
		double dframe=(double)(end-start)/128;
		Vector3 sum=Vector3.zero();
		for(int i=0; i<128; i++) {
			double curframe=start+dframe*i;
			Vector3 cur=in.get((int)curframe);
			step1.add(cur);
			sum=Vector3.add(sum,cur);
		}
		Vector3 avg=Vector3.div(sum,128);
		for(int i=0; i<128; i++) {
			out.add(Vector3.sub(step1.get(i),avg));
		}
		return out;
		//}
	}

}
