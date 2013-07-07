package dmace.gesture_recognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class FeatureExtractor {
	
	public static double[] calculateFeatures(double[] fftx, double[] ffty, int time) {
		
		double[] fftmag=new double[128];
		for(int i=0; i<fftx.length; i++) {
			fftmag[i]=Math.sqrt(fftx[i]*fftx[i]+ffty[i]*ffty[i]);
		}
		
		double[] x=new double[128];
		for(int i=1; i<fftx.length/2; i++) {
			double re=fftx[2*i], im=fftx[2*i+1];
			for(int k=0; k<x.length; k++) {
				x[k]+=re*Math.cos((float)k/x.length*Math.PI*2*i)+im*Math.sin((float)k/x.length*Math.PI*2*i);
			}
		}
		
		double[] y=new double[128];
		for(int i=1; i<ffty.length/2; i++) {
			double re=ffty[2*i], im=ffty[2*i+1];
			for(int k=0; k<y.length; k++) {
				y[k]+=re*Math.cos((float)k/y.length*Math.PI*2*i)+im*Math.sin((float)k/y.length*Math.PI*2*i);
			}
		}
		
		double[] mag=new double[128];
		for(int k=0; k<y.length; k++) {
			mag[k]+=Math.sqrt(x[k]*x[k]+y[k]*y[k]);
		}
		
		double[] features=new double[82];
		features[0]=rms(x);
		features[1]=rms(y);
		features[2]=rms(mag);
		features[3]=interquartileRange(x); 
		features[4]=interquartileRange(y);  
		features[5]=interquartileRange(mag);  
		features[6]=areaApproximation(x); 
		features[7]=areaApproximation(y); 
		features[8]=areaApproximation(mag); 
		features[9]=averageMaxDispersion(x);
		features[10]=averageMaxDispersion(y);
		features[11]=averageMaxDispersion(mag);
		features[12]=amplitudeConstantness(x);
		features[13]=amplitudeConstantness(y);
		features[14]=amplitudeConstantness(mag);
		features[15]=numHighPoints(x);
		features[16]=numHighPoints(y);
		features[17]=numHighPoints(mag);
		features[18]=largestPoint(x);
		features[19]=largestPoint(y);
		features[20]=largestPoint(mag);
		features[21]=avgLargestPoint(x);
		features[22]=avgLargestPoint(y);
		features[23]=avgLargestPoint(mag);
		features[24]=slopeGMean(x);
		features[25]=slopeGMean(y);
		features[26]=slopeGMean(mag);
		features[27]=arcLength(x);
		features[28]=arcLength(y);
		features[29]=arcLength(mag);
		features[30]=endWeightedHeightAvg(x);
		features[31]=endWeightedHeightAvg(y);
		features[32]=endWeightedHeightAvg(mag);
		features[33]=frontWeightedHeightAvg(x);
		features[34]=frontWeightedHeightAvg(y);
		features[35]=frontWeightedHeightAvg(mag);
		features[36]=averageMaxDispersionV2(x);
		features[37]=averageMaxDispersionV2(y);
		features[38]=averageMaxDispersionV2(mag);
		features[39]=avgDistBetweenZeroes(x);
		features[40]=avgDistBetweenZeroes(y);
		features[41]=avgDistBetweenZeroes(mag);
		features[42]=averageEnergy(fftx);
		features[43]=averageEnergy(ffty);
		features[44]=averageEnergy(fftmag);
		features[45]=sumReciprocalFFT(fftx);
		features[46]=sumReciprocalFFT(ffty);
		features[47]=sumReciprocalFFT(fftmag);
		features[48]=sumGeometricFFT(fftx);
		features[49]=sumGeometricFFT(ffty);
		features[50]=sumGeometricFFT(fftmag);
		features[51]=geomMeanFFT(fftx);
		features[52]=geomMeanFFT(ffty);
		features[53]=geomMeanFFT(fftmag);
		features[54]=avgStartFFT(fftx);
		features[55]=avgStartFFT(ffty);
		features[56]=avgStartFFT(fftmag);
		features[57]=avgEndFFT(fftx);
		features[58]=avgEndFFT(ffty);
		features[59]=avgEndFFT(fftmag);
		features[60]=time;
		features[61]=correlation(x,y);
		for(int i=62; i<72; i++) { features[i]=fftx[i-62]; features[i+10]=ffty[i-62]; }
		//Random r=new Random();
		//for(int i=82; i<82+a; i++) features[i]=r.nextInt(100)+1000;
		
		return features;
	}
	
	/*height of largest point
	avg height of three largest points
	number of points that go up a lot
	gmean val of slope
	arc length
	avg front focused height of max and min
	avg end focused height of max and min
	avg dist between places where hits avg val
	sum 1/2^n ffts
	sum 1/n ffts
	avg first three ffts
	avg second three ffts
	geom mean of ffts */

	public static double numHighPoints(double[] y) {
		
		//delete multiples
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		
		//find all local maximums or minimums
		ArrayList<Integer> max=new ArrayList<Integer>();
		max.add(0);
		for(int i=1; i<ynew.size()-1; i++) {
			if( (ynew.get(i).y>ynew.get(i-1).y && ynew.get(i).y>ynew.get(i+1).y) || (ynew.get(i).y<ynew.get(i-1).y && ynew.get(i).y<ynew.get(i+1).y) ) max.add((int)ynew.get(i).x);
		}
		max.add(y.length-1);
		
		double[] xcopy=new double[y.length];
		for(int i=0; i<y.length; i++) {
			xcopy[i]=y[i];
		}
		Arrays.sort(xcopy);
		double interquartile=xcopy[y.length/4]-xcopy[y.length/4*3];
		
		int total=0;
		for(int i=1; i<max.size(); i++) {
			if(max.get(i)-max.get(i-1)>interquartile) total++;
		}
		return total;
	}
	
	public static double largestPoint(double[] y) {
		double best=0;
		for(int i=0; i<y.length; i++) {
			best=Math.max(best,Math.abs(y[i]));
		}
		return best;
	}

	public static double avgLargestPoint(double[] y) {
		double[] largest=new double[]{0,0,0};
		for(int i=0; i<y.length; i++) {
			if(y[i]>largest[0]) largest[0]=y[i];
			if(largest[0]>largest[1]) { double temp=largest[1]; largest[1]=largest[0]; largest[0]=temp; }
			if(largest[1]>largest[2]) { double temp=largest[2]; largest[2]=largest[1]; largest[1]=temp; }
		}
		return (y[0]+y[1]+y[2])/3;
	}
	
	public static double slopeGMean(double[] y) {
		double sum=0;
		for(int i=1; i<y.length; i++) {
			sum+=Math.pow(y[i]-y[i-1],2);
		}
		return Math.sqrt(sum/y.length);
	}
	
	public static double arcLength(double[] y) {
		double sum=0;
		for(int i=1; i<y.length; i++) {
			sum+=Math.sqrt(1+Math.pow(y[i]-y[i-1],2));
		}
		return sum;
	}
	
	public static double endWeightedHeightAvg(double[] y) {
		
		//delete multiples
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		
		//find all local maximums or minimums
		ArrayList<Integer> max=new ArrayList<Integer>();
		max.add(0);
		for(int i=1; i<ynew.size()-1; i++) {
			if( (ynew.get(i).y>ynew.get(i-1).y && ynew.get(i).y>ynew.get(i+1).y) || (ynew.get(i).y<ynew.get(i-1).y && ynew.get(i).y<ynew.get(i+1).y) ) max.add((int)ynew.get(i).x);
		}
		max.add(y.length-1);
		
		//calculate height jump between each set of maxes
		ArrayList<Double> rise=new ArrayList<Double>();
		for(int i=1; i<max.size(); i++) {
			double dy=0;
			if(i!=0) dy+=y[max.get(i)]-y[max.get(i-1)]/2;
			//if(i!=max.size()-1) dy+=y[max.get(i)]-y[max.get(i+1)]/2;
			rise.add(dy);
		}
		
		//calculate dx*dy for each pair of maximums
		double total=0;
		for(int i=1; i<rise.size(); i++) {
			//for(int j=0; j<i; j++) {
				double dy=Math.abs(rise.get(i)*rise.get(i-1));
				double x=max.get(i);
				//System.out.println(dy);
				total+=dy*x/y.length;;
			//}
		}
		return total;
	}
	
	public static double frontWeightedHeightAvg(double[] y) {
		
		//delete multiples
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		
		//find all local maximums or minimums
		ArrayList<Integer> max=new ArrayList<Integer>();
		max.add(0);
		for(int i=1; i<ynew.size()-1; i++) {
			if( (ynew.get(i).y>ynew.get(i-1).y && ynew.get(i).y>ynew.get(i+1).y) || (ynew.get(i).y<ynew.get(i-1).y && ynew.get(i).y<ynew.get(i+1).y) ) max.add((int)ynew.get(i).x);
		}
		max.add(y.length-1);
		
		//calculate height jump between each set of maxes
		ArrayList<Double> rise=new ArrayList<Double>();
		for(int i=1; i<max.size(); i++) {
			double dy=0;
			if(i!=0) dy+=y[max.get(i)]-y[max.get(i-1)]/2;
			//if(i!=max.size()-1) dy+=y[max.get(i)]-y[max.get(i+1)]/2;
			rise.add(dy);
		}
		
		//calculate dx*dy for each pair of maximums
		double total=0;
		for(int i=1; i<rise.size(); i++) {
			//for(int j=0; j<i; j++) {
				double dy=Math.abs(rise.get(i)*rise.get(i-1));
				double x=max.get(i);
				//System.out.println(dy);
				total+=dy*(y.length-x)/y.length;
			//}
		}
		return total;
	} 
	
	public static double averageMaxDispersionV2(double[] y) {
		
		//delete multiples
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		
		//find all local maximums or minimums
		ArrayList<Integer> max=new ArrayList<Integer>();
		max.add(0);
		for(int i=1; i<ynew.size()-1; i++) {
			if( (ynew.get(i).y>ynew.get(i-1).y && ynew.get(i).y>ynew.get(i+1).y) || (ynew.get(i).y<ynew.get(i-1).y && ynew.get(i).y<ynew.get(i+1).y) ) max.add((int)ynew.get(i).x);
		}
		max.add(y.length-1);
		
		//calculate height jump between each set of maxes
		ArrayList<Double> rise=new ArrayList<Double>();
		for(int i=1; i<max.size(); i++) {
			double dy=0;
			if(i!=0) dy+=y[max.get(i)]-y[max.get(i-1)]/2;
			//if(i!=max.size()-1) dy+=y[max.get(i)]-y[max.get(i+1)]/2;
			rise.add(dy);
		}
		
		//calculate dx*dy for each pair of maximums
		double total=0;
		double totalY=0;
		for(int i=1; i<rise.size(); i++) {
			for(int j=0; j<i; j++) {
				double dx=max.get(i)-max.get(j);
				double dy=Math.abs(rise.get(i)*rise.get(j));
				//System.out.println(dy);
				total+=dx*dy;
				totalY+=dy;
			}
		}
		return total/totalY;
	} 
	
	public static double avgDistBetweenZeroes(double[] y) {
		double avg=0;
		for(int i=0; i<y.length; i++) {
			avg+=y[i];
		}
		avg/=y.length;
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		int lastX=-1;
		double sumX=0,numX=0;;
		for(int i=1; i<ynew.size(); i++) {
			if(Math.signum(ynew.get(i-1).y-avg) != Math.signum(ynew.get(i).y-avg)) {
				if(lastX!=-1) { sumX+=ynew.get(i).x-lastX; }
				lastX=(int)ynew.get(i).x;
				numX++;
			}
		}
		return sumX/numX;
	}
	
	public static double sumReciprocalFFT(double[] fftx) {
		double sum=0;
		for(int i=0; i<fftx.length; i++) {
			sum+=fftx[i]/(i+1);
		}
		return sum;
	}
	
	public static double sumGeometricFFT(double[] fftx) {
		double sum=0;
		for(int i=0; i<fftx.length; i++) {
			sum+=fftx[i]/Math.pow(2,i);
		}
		return sum;
	}
	
	public static double geomMeanFFT(double[] fftx) {
		double sum=0;
		for(int i=0; i<fftx.length; i++) {
			sum+=fftx[i]*fftx[i];
		}
		return Math.sqrt(sum/fftx.length);
	}
	
	public static double avgStartFFT(double[] fftx) {
		double sum=0;
		for(int i=0; i<3; i++) {
			sum+=fftx[i];
		}
		return sum/3;
	}
	
	public static double avgEndFFT(double[] fftx) {
		double sum=0;
		for(int i=0; i<3; i++) {
			sum+=fftx[fftx.length-1-i];
		}
		return sum/3;
	}
	
	public static double rms(double[] x) {
		double sum=0;
		for(int i=0; i<x.length; i++) {
			sum+=x[i]*x[i];
		}
		return Math.sqrt(sum/x.length);
	}
	
	public static double interquartileRange(double[] x) {
		double[] xcopy=new double[x.length];
		for(int i=0; i<x.length; i++) {
			xcopy[i]=x[i];
		}
		Arrays.sort(xcopy);
		return xcopy[x.length/4]-xcopy[x.length/4*3];
	}
	
	public static double correlation(double[] x,double[] y) {
		double sum=0;
		for(int i=0; i<y.length; i++) {
			sum+=Math.abs(y[i])-Math.abs(x[i]);
		}
		return sum;
	}
	
	public static double areaApproximation(double[] x) {
		double sum=0;
		for(int i=0; i<x.length; i++) {
			sum+=Math.abs(x[i]);
		}
		return sum;
	}
	
	public static double averageEnergy(double[] fftx) {
		double sum=0;
		for(int i=0; i<fftx.length; i++) {
			sum+=fftx[i]*fftx[i];
		}
		return sum;
	}
	
	//find average maximum dispersion
	public static double averageMaxDispersion(double[] y) {
		
		//delete multiples
		ArrayList<Vector2> ynew=new ArrayList<Vector2>();
		for(int i=0; i<y.length; i++) {
			int start=i;
			while(i<y.length && y[i]==y[start]) i++;
			int x=(i-1+start)/2;
			ynew.add(new Vector2(x,y[start]));
		}
		
		//find all local maximums or minimums
		ArrayList<Integer> max=new ArrayList<Integer>();
		max.add(0);
		for(int i=1; i<ynew.size()-1; i++) {
			if( (ynew.get(i).y>ynew.get(i-1).y && ynew.get(i).y>ynew.get(i+1).y) || (ynew.get(i).y<ynew.get(i-1).y && ynew.get(i).y<ynew.get(i+1).y) ) max.add((int)ynew.get(i).x);
		}
		max.add(y.length-1);
		
		//calculate height jump between each set of maxes
		ArrayList<Double> rise=new ArrayList<Double>();
		for(int i=1; i<max.size(); i++) {
			double dy=0;
			if(i!=0) dy+=y[max.get(i)]-y[max.get(i-1)]/2;
			//if(i!=max.size()-1) dy+=y[max.get(i)]-y[max.get(i+1)]/2;
			rise.add(dy);
		}
		
		//calculate dx*dy for each pair of maximums
		double total=0;
		double totalY=0;
		for(int i=1; i<rise.size(); i++) {
			//for(int j=0; j<i; j++) {
				double dx=max.get(i)-max.get(i-1);
				double dy=Math.abs(rise.get(i)*rise.get(i-1));
				//System.out.println(dy);
				total+=dx*dy;
				totalY+=dy;
			//}
		}
		return total/totalY;
	} 
	
	//how much of the data lies within the middle half of the average, so large jolts followed by quiet will cause high values
	public static double amplitudeConstantness(double[] y) {
		double sum=0;
		for(int i=0; i<y.length; i++) {
			sum+=Math.abs(y[i]);
		}
		double avg=sum/y.length;
		int inside=0;
		for(int i=0; i<y.length; i++) {
			if(Math.abs(y[i])<avg) inside++;
		}
		return (double)inside/y.length;
	}
	
}
