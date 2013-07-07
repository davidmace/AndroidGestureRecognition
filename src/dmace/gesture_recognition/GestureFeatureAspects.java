package dmace.gesture_recognition;

class GestureFeatureAspects {
	
	double center, stdev;
	
	public GestureFeatureAspects(double center, double stdev) {
		this.center=center;
		this.stdev=stdev;
	}
	
	public double calculateCloseness(double val) {
		//System.out.println("JKJKJKJKJKJKJ"+(val-(center+stdev)+1)+" "+Math.pow(val-(center+stdev)-1 , 0.5));
		//return Math.pow( Math.E , -Math.pow( (val-(center+stdev))/(stdrange/10) , 2 ) );
		//return Math.pow( Math.E , -Math.pow( (-(val-(center-stdev)))/(stdrange/10) , 2
		if(val>center) { return Math.pow(val-(center),2); }		//1/Math.pow(val-(center+stdev)+1 , 4); }
		if(val<center) { return Math.pow((center)-val,2); }		//return 1/Math.pow(-(val-(center-stdev))+1 , 4); }
		return 1;
		//if(stdev==0) return val==center?1:0;
		//double ans=Math.pow( Math.E , -Math.pow((val-center)/stdev,2));
		//-Math.pow(0.667/stdev*(val-center),2)+1;
		//if(ans<0) ans=0;
		//return ans;
	}
	
}
