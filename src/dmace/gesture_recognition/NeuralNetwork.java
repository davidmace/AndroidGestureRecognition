package dmace.gesture_recognition;

import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork {
	
	static int numGestures=4,numFeatures;
	static double[][][] weights;
	static GestureFeatureAspects[][] gfa;
	
	public static void trainAll(ArrayList[] tVectors) {
		for(int i=0; i<tVectors.length; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				//System.out.println(i+" "+Arrays.toString((double[])tVectors[i].get(j)));
			}
		}
		numFeatures=((double[])tVectors[0].get(0)).length;
		gfa=new GestureFeatureAspects[numGestures][numFeatures];
		weights=new double[numGestures][numGestures][numFeatures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numGestures; j++) {
				for(int k=0; k<numFeatures; k++) {
					weights[i][j][k]=(double)1/numFeatures;
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////Find Avg Values of Features For Each Gesture/////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//sum up magnitude values for each gesture and each feature
		//System.out.println("AVG");
		double[][] gestureFeatureAvg=new double[numGestures][numFeatures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				double[] tv=(double[])tVectors[i].get(j);
				for(int k=0; k<tv.length; k++) {
					gestureFeatureAvg[i][k]+=tv[k];
				}
				//System.out.println(Arrays.toString(tv)+" "+i);
			}
		}
		
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
				gestureFeatureAvg[i][j]/=tVectors[i].size();
			}
		}
		
		double[][] stdevSums=new double[numGestures][numFeatures];
		for(int i=0; i<tVectors.length; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				double[] tv=(double[])tVectors[i].get(j);
				for(int k=0; k<tv.length; k++) {
					stdevSums[i][k]+=Math.pow( tv[k]-gestureFeatureAvg[i][k] , 2 );
				}
			}
		}
		
		//System.out.println("GFA");
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
				gfa[i][j]=new GestureFeatureAspects( gestureFeatureAvg[i][j] , Math.sqrt(stdevSums[i][j]/tVectors[i].size() ) );
				 //System.out.print("("+gfa[i][j].center+" "+gfa[i][j].stdev+")");
			}
			//System.out.println();
		}
		//System.out.println();
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////Calculate Membership Values For Each Gesture/////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*featureMax=new double[numFeatures];
		featureMin=new double[numFeatures];
		for(int i=0; i<numFeatures; i++) {
			featureMax[i]=Double.MIN_VALUE; featureMin[i]=Double.MAX_VALUE;
			for(int j=0; j<numGestures; j++) {
				for(int k=0; k<tVectors[j].size(); k++) {
					double cur=((double[])tVectors[j].get(k))[i];
					if(cur>featureMax[i]) featureMax[i]=cur;
					if(cur<featureMin[i]) featureMin[i]=cur;
				}
			}
		}*/
		
		//find membership of feature k of gesture trial j of gesture type i relative to gesture type l
		//System.out.println("Membership");
		double[][][][] membershipValues=new double[numGestures][][][];
		for(int i=0; i<numGestures; i++) {
			membershipValues[i]=new double[tVectors[i].size()][numFeatures][numGestures];
			for(int j=0; j<tVectors[i].size(); j++) {
				for(int k=0; k<numFeatures; k++) {
					for(int l=0; l<numGestures; l++) {
						//if(i==3 && j==2 && k==12 && l==1) System.out.println(gfa[l][k].center+" "+gfa[l][k].stdev+" "+((double[])tVectors[i].get(j))[k]+" "+gfa[l][k].calculateCloseness( ((double[])tVectors[i].get(j))[k]));
						membershipValues[i][j][k][l]=gfa[l][k].calculateCloseness( ((double[])tVectors[i].get(j))[k] );
						//System.out.println(i+" "+j+" "+k+" "+l+" "+membershipValues[i][j][k][l]);
					}
				}
			}
		}
		/*for(int i=0; i<numGestures; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				for(int k=0; k<numFeatures; k++) {
					for(int l=0; l<numGestures; l++) {
						if(i==l) continue;
						double sum=membershipValues[i][j][k][l]+membershipValues[i][j][k][i];
						if(sum==0) continue;
						//double sv=membershipValues[i][j][k][l];
						membershipValues[i][j][k][l]=membershipValues[i][j][k][i]/sum;
						//membershipValues[l][j][k][i]=sv/sum;
					}
				}
			}
		}*/
		double[][][][][] comparisonValues=new double[numGestures][][][][];
		for(int i=0; i<numGestures; i++) {
			comparisonValues[i]=new double[tVectors[i].size()][numFeatures][numGestures][numGestures];
			for(int j=0; j<tVectors[i].size(); j++) {
				for(int k=0; k<numFeatures; k++) {
					for(int l=0; l<numGestures; l++) {
						for(int m=0; m<l; m++) {
							if(l==m) continue;
							double sum=membershipValues[i][j][k][l]+membershipValues[i][j][k][m];
							if(sum==0) continue;
							//double sv=membershipValues[i][j][k][l];
							comparisonValues[i][j][k][l][m]=membershipValues[i][j][k][m]/sum;
							comparisonValues[i][j][k][m][l]=membershipValues[i][j][k][l]/sum;
						}
					}
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////Calculate Feature Separability Weights////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		for(int i=0; i<numGestures; i++) {
			//weightSum=0;
			for(int j=0; j<numGestures; j++) {
				if(i==j) continue;
				double weightSum=0;
				for(int k=0; k<numFeatures; k++) {
					double overlap=0;
					for(int l=0; l<tVectors[j].size(); l++) {
						overlap+=comparisonValues[j][l][k][i][j];
						//if(i==0 && k==0) System.out.println(j+" "+membershipValues[j][l][k][i]);
					}
					//if(i==0 && k==0) System.out.println(j+" "+overlap);
					weights[i][j][k]=tVectors[j].size()*((double)1/2)-overlap;
					if(weights[i][j][k]<0) weights[i][j][k]=0;
					weights[i][j][k]=Math.pow(weights[i][j][k],4);
					weightSum+=weights[i][j][k];
				}
				//double as=0;
				for(int k=0; k<numFeatures; k++) {
					if(weightSum==0) weights[i][j][k]=0;
					else weights[i][j][k]/=weightSum;
					//as+=weights[i][j][k];
					//System.out.print(weights[i][j][k]+" ");
				}
				//System.out.println("as "+as);
			}
		}
		/*double m=1;
		for(int i=0; i<numGestures; i++) {
			double sumWeights=0;
			for(int j=0; j<numFeatures; j++) {
				double sumSame=0, sumDiff=0;
				int numSame=0, numDiff=0;
				for(int k=0; k<numGestures; k++) {
					for(int l=0; l<tVectors[k].size(); l++) {
						double cur=((double[])tVectors[k].get(l))[j];
						if
						(i==k) {
							sumSame+=Math.pow(Math.abs(gfa[i][j].center-cur),m);
							numSame++;
						}
						else {
							sumDiff+=Math.pow(Math.abs(gfa[i][j].center-cur),m);
							//System.out.println(Math.pow(gfa[i][j].center-cur),1/m));
							numDiff++;
						}
					}
				}
				sumSame=Math.pow(sumSame/numSame,1/m);
				sumDiff=Math.pow(sumDiff/numDiff,1/m);
				weights[i][j]=sumSame/sumDiff;
				sumWeights+=weights[i][j];
				//System.out.println(sumSame+" "+sumDiff);
			}
			for(int j=0; j<numFeatures; j++) {
				weights[i][j]/=sumWeights;
			}
		}*/
		
		/*for(int i=0; i<numGestures; i++) {
			int sumWeights=0;
			for(int j=0; j<numFeatures; j++) {
				double sumSame=0, sumDiff=0;
				int numSame=0, numDiff=0;
				for(int k=0; k<numGestures; k++) {
					for(int l=0; l<tVectors[k].size(); l++) {
						if(i==k) { numSame++; sumSame+=gfa[i][j].calculateCloseness(membershipValues[k][l][j][i]); }
						else { numDiff++; sumDiff+=gfa[i][j].calculateCloseness(membershipValues[k][l][j][i]); }
					}
				}
				System.out.println(sumSame+" "+sumDiff);
				if(sumDiff==0) weights[i][j]=0;
				else weights[i][j]=sumSame/sumDiff;
				sumWeights+=weights[i][j];
			}
			for(int j=0; j<numFeatures; j++) {
				if(sumWeights==0) continue;
				weights[i][j]/=sumWeights;
				System.out.println(sumWeights);
			}
		}*/
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////Make Neural Network For Each Gesture That Defines Feature Weights/////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		//train it on true and false gestures and make it return closest value to 1 for true gestures and 
		//closest value to 0 for false gestures
		double alpha=0.005;
		//sum of weights for one gesture is 1, init weights to 1/numfeatures
		for(int i=0; i<numGestures; i++) { //loop nns
			for(int j=0; j<1; j++) { //loop trials of training
				for(int k=0; k<numGestures; k++) {//loop registered gestures
					for(int l=0; l<tVectors[k].size(); l++) {
						double sum=0;
						for(int m=0; m<numFeatures; m++) {
							sum+=weights[i][m] * membershipValues[k][l][m][i]; //lth member of k compared to mth feature of group i
							//System.out.println(i+" "+j+" "+k+" "+l+" "+m+" "+weights[i][m]+" "+membershipValues[k][l][m][i]);
						}
						//System.out.println();
						double weightSum=0;
						for(int m=0; m<numFeatures; m++) {
							int des=(k==i)?1:0;
							double cur=membershipValues[k][l][m][i];
							weights[i][m]+=alpha*(des-sum)*cur;
							if(weights[i][m]<0) weights[i][m]=0;
							weightSum+=weights[i][m];
						}
						for(int m=0; m<numFeatures; m++) { //normalize weights to sum to 1
							weights[i][m]/=weightSum;
						}
					}
				}
			}
		}
		for(int i=0; i<weights.length; i++) {
			for(int j=0; j<weights[0].length; j++) {
				//System.out.print(weights[i][j]+" ");
			}
			//System.out.println();
		}
		System.out.println();
		*/
	}
	
	public static int query(double[] data) {
		////System.out.println(Arrays.toString(data));
		/////System.out.println("GFA");
		for(int i=0; i<numGestures; i++) {
			for(int k=0; k<numFeatures; k++) {
				/////System.out.print("("+gfa[i][k].center+" "+gfa[i][k].stdev+") ");
			}
			/////System.out.println();
		}
		
		//System.out.println("Membership Initial");
		double[][] membershipValues=new double[numGestures][numFeatures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
					//System.out.println(i+" "+j);
					membershipValues[i][j]=gfa[i][j].calculateCloseness( data[j] );
					//System.out.println(membershipValues[i][j][k]);
				
				//System.out.println();
			}
		}
		double[][][] comparisonValues=new double[numGestures][numGestures][numFeatures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<i; j++) {
				for(int k=0; k<numFeatures; k++) {
					//System.out.println(i+" "+j);
					double sum=membershipValues[i][k]+membershipValues[j][k];
					comparisonValues[i][j][k]=membershipValues[j][k]/sum;
					comparisonValues[j][i][k]=membershipValues[i][k]/sum;
					//System.out.println(membershipValues[i][k]);
				
				//System.out.println();
			}
		}
			
			//System.out.println("Membership Ratio");
			/*for(int i=0; i<numGestures; i++) {
				for(int j=0; j<numGestures; j++) {
					//System.out.print(membershipValues[i][j][k]+" ");
				}
				//System.out.println();
			}*/
			//System.out.println();
			//System.out.println("QUERY");
			//System.out.println(Arrays.toString(membershipValues[i]));
		}
		double[][] percentage=new double[numGestures][numGestures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numGestures; j++) {
				if(i==j) continue;
				//System.out.print(i+" "+j+" ");
				for(int k=0; k<numFeatures; k++) {
					percentage[i][j]+=weights[i][j][k]*comparisonValues[i][j][k];
					//System.out.print("("+k+" "+weights[i][j][k]+" "+comparisonValues[i][j][k]+") ");
				}
				//System.out.println();
			}
		}
		int[] wins=new int[numGestures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<i; j++) {
				//System.out.println(i+" "+j+" "+percentage[i][j]+" "+percentage[j][i]);
				if(i==j) continue;
				if(percentage[i][j]>=percentage[j][i]) wins[i]++;
				else wins[j]++;
			}
		}
		int best=0;
		for(int i=0; i<numGestures; i++) {
			//System.out.println("wins "+i+" "+wins[i]);
			if(wins[i]>wins[best]) best=i;
			
			else if(wins[i]==wins[best]) {
				int sum=0;
				for(int j=0; j<numGestures; j++) {
					if(i==j) continue;
					sum+=percentage[i][j];
				}
			}
			
		}
		return best;
		/*double[] wins=new double[numGestures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numGestures; j++) {
				for(int k=0; k<numFeatures; k++) {
					wins[i]+=weights[i][j]*membershipValues[i][j];
					System.out.print("("+weights[i][j]+" "+membershipValues[i][j]+") ");
				}
			}
			System.out.println();
		}
		for(int i=0; i<numGestures; i++) {
			System.out.println(percentMatch[i]);
		}
		int best=0;
		for(int i=1; i<numGestures; i++) {
			if(percentMatch[i]>percentMatch[best]) best=i;
		}
		//if(percentMatch[best]<0.5) return -1; //no gesture
		return best;*/
	}
	//0 1 22 23 24 25
}

/*import java.util.ArrayList;
import java.util.Arrays;


public class NeuralNetwork {
	
	static int numGestures=6,numFeatures;
	static double[][][] weights;
	static double[][] threshold;
	static GestureFeatureAspects[][] gfa;
	static ArrayList[] tVectors=new ArrayList[numGestures];
	
	public static void trainAll(ArrayList[] tVectors) {
		for(int i=0; i<tVectors.length; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				//System.out.println(i+" "+Arrays.toString((double[])tVectors[i].get(j)));
			}
		}
		numFeatures=((double[])tVectors[0].get(0)).length;
		gfa=new GestureFeatureAspects[numGestures][numFeatures];
		weights=new double[numGestures][numGestures][numFeatures];
		threshold=new double[numGestures][numGestures];
		for(int h=0; h<numGestures; h++) {
			for(int i=0; i<h; i++) {
				for(int j=0; j<numFeatures; j++) {
					weights[i][h][j]=(double)1/numFeatures;
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////Find Avg Values of Features For Each Gesture/////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//sum up magnitude values for each gesture and each feature
		double[][] gestureFeatureAvg=new double[numGestures][numFeatures];
		for(int i=0; i<6; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				double[] tv=(double[])tVectors[i].get(j);
				for(int k=0; k<tv.length; k++) {
					gestureFeatureAvg[i][k]+=tv[k];
					// System.out.println(tv[k]+" "+i+" "+k);
				}
			}
		}
		
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
				gestureFeatureAvg[i][j]/=tVectors[i].size();
			}
		}
		
		double[][] stdevSums=new double[numGestures][numFeatures];
		for(int i=0; i<tVectors.length; i++) {
			for(int j=0; j<tVectors[i].size(); j++) {
				double[] tv=(double[])tVectors[i].get(j);
				for(int k=0; k<tv.length; k++) {
					stdevSums[i][k]+=Math.pow( tv[k]-gestureFeatureAvg[i][k] , 2 );
				}
			}
		}
		
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
				gfa[i][j]=new GestureFeatureAspects( gestureFeatureAvg[i][j] , Math.sqrt(stdevSums[i][j]/tVectors[i].size() ) );
			}
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////Calculate Membership Values For Each Gesture/////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//need to findmembership to other classes as well
		double[][][][] membershipValues=new double[numGestures][][][];
		for(int i=0; i<numGestures; i++) {
			membershipValues[i]=new double[tVectors[i].size()][numFeatures][numGestures];
			for(int j=0; j<tVectors[i].size(); j++) {
				for(int k=0; k<numFeatures; k++) {
					for(int l=0; l<numGestures; l++) {
						membershipValues[i][j][k][l]=gfa[l][k].calculateCloseness( ((double[])tVectors[i].get(j))[k] );
						//System.out.println(i+" "+j+" "+k+" "+membershipValues[i][j][k]);
					}
				}
			}
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////Make Neural Network For Each Gesture That Defines Feature Weights/////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//train it on true and false gestures and make it return closest value to 1 for true gestures and 
		//closest value to 0 for false gestures
		double alpha=0.005;
		//sum of weights for one gesture is 1, init weights to 1/numfeatures
		for(int i=0; i<numGestures; i++) { //loop nns
			boolean trained=false;
			while(!trained) { //loop trials of training
				trained=true;
				for(int k=0; k<numGestures; k++) {//loop registered gestures
					if(i==k) continue;
					int class1=Math.min(i,k), class2=Math.max(i,k);
					for(int l=0; l<tVectors[k].size(); l++) {
						double sum1=0, sum2=0;
						for(int m=0; m<numFeatures; m++) {
							sum1+=weights[class1][class2][m] * membershipValues[k][l][m][k];
							sum2+=weights[class1][class2][m] * membershipValues[k][l][m][i];//lth member of k compared to mth feature of group i
							//System.out.println(i+" "+j+" "+k+" "+l+" "+m+" "+weights[i][m]+" "+membershipValues[k][l][m][i]);
						}
						//System.out.println(sum1+" "+sum2); if type k then max using k and min using i
						//System.out.println();
						if(sum2>sum1) {
							trained=false;
							double weightSum=0;
							for(int m=0; m<numFeatures; m++) {
								weights[class1][class2][m]+=alpha*(1-sum1)*membershipValues[k][l][m][k];//train larger to one
								weights[class1][class2][m]+=alpha*(0-sum2)*membershipValues[k][l][m][i];
								if(weights[class1][class2][m]<0) weights[class1][class2][m]=0;
								weightSum+=weights[class1][class2][m];
							}
							for(int m=0; m<numFeatures; m++) { //normalize weights to sum to 1
								weights[class1][class2][m]/=weightSum;
							}
						}
					}
				}
			}
		}
		
		//System.out.println("adsf"+(System.currentTimeMillis()-time));
		
	}
	
	public static int query(double[] data) {
		double[][] membershipValues=new double[numGestures][numFeatures];
		for(int i=0; i<numGestures; i++) {
			for(int j=0; j<numFeatures; j++) {
				//System.out.println(i+" "+j);
				membershipValues[i][j]=gfa[i][j].calculateCloseness( data[j] );
			}
		}
		int[] wins=new int[numGestures];
		for(int h=0; h<numGestures; h++) {
			for(int i=0; i<h; i++) {
				double sum1=0, sum2=0;
				for(int j=0; j<numFeatures; j++) {
					sum1+=weights[i][h][j]*membershipValues[i][j];
					sum2+=weights[i][h][j]*membershipValues[i][h];
					//System.out.println(i+" "+j+" "+weights[i][j]+" "+membershipValues[i][j]);
				}
				//System.out.println(threshold[i][h]);
				if(sum1>sum2) wins[h]++;
				else wins[i]++;
			}
		}
		for(int i=0; i<numGestures; i++) {
			System.out.println(wins[i]);
		}
		int best=0;
		for(int i=1; i<numGestures; i++) {
			if(wins[i]>wins[best]) best=i;
		}
		//if(percentMatch[best]<0.5) return -1; //no gesture
		return best;
	}
	//0 1 22 23 24 25
}*/