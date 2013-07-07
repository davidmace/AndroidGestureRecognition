package dmace.gesture_recognition;

import java.util.ArrayList;

class TrainingVector {
	double[] data;
	boolean target;
	
	public TrainingVector(double[] data, boolean target) {
		this.data=data;
		/*data=new double[datapts.size()*3];
		for(int i=0; i<datapts.size(); i++) {
			data[3*i]=datapts.get(i).acc.x;
			data[3*i+1]=datapts.get(i).acc.y;
			data[3*i+2]=datapts.get(i).acc.z;
		}*/
		this.target=target;
	}
}
