package dmace.gesture_recognition;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


public class DataStore {
		
	private static final double EPSILON = 1e-10;
	private static ArrayList<Vector3> data=new ArrayList<Vector3>();
	private static DataStore instance;
	private static ConsoleActivity activity;
	public static Activity context;
	
	public static void add(byte[] buffer) {
		synchronized(ReadWriteThread.getInstance()) {
			
			if(buffer.length!=7 || buffer[4]==0 || buffer[5]==0 || buffer[6]==0)
				return;
			Vector3 newAcc=new Vector3(buffer[4],buffer[5],buffer[6]);
			data.add(newAcc);
			Log.d("MyApp","add");
		}
	}
	
	public static void clearData() {
		synchronized(ReadWriteThread.getInstance()) {
			data.clear();
		}
	}
	
	public static double[] getModifiedData() {
		if(data.size()==0) {
			Toast.makeText(context.getApplicationContext(), "Nothing Registered", Toast.LENGTH_SHORT).show();
			return null;
		}
		ArrayList<Vector3> normalizedData=PreProcessor.normalizeData(data);
		
		double[] magData=new double[normalizedData.size()];
		for(int k=0; k<normalizedData.size(); k++) magData[k]=Vector3.magnitude(normalizedData.get(k));
		
		double[] fftCoefficients=FFTFinder.getFrequencyData(magData);
		
		ArrayList<Vector3> rotationNormalizedData=RotationNormalizer.normalizeRotation(fftCoefficients,normalizedData);
		//System.out.println(Arrays.toString(fftCoefficients));
		
		//fix screen size, toast to gesture len 0 or gesture training sets 0
		
		
		
		double[] xPoints=new double[rotationNormalizedData.size()];
		for(int k=0; k<rotationNormalizedData.size(); k++) xPoints[k]=rotationNormalizedData.get(k).x;
		double[] yPoints=new double[rotationNormalizedData.size()];
		for(int k=0; k<rotationNormalizedData.size(); k++) yPoints[k]=rotationNormalizedData.get(k).y;
		double[] zPoints=new double[rotationNormalizedData.size()];
		for(int k=0; k<rotationNormalizedData.size(); k++) zPoints[k]=rotationNormalizedData.get(k).z;
		double[] fftCoefficientsX=FFTFinder.getFrequencyData(xPoints);
		double[] fftCoefficientsY=FFTFinder.getFrequencyData(yPoints);
		double[] fftCoefficientsZ=FFTFinder.getFrequencyData(zPoints);
		
		double[] features=FeatureExtractor.calculateFeatures(fftCoefficientsX, fftCoefficientsY, data.size());
		return features;
	}
    
}
