/**
 * 
 */
package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Rui
 *
 */
public class EvUtils {
	
	/**given two array of int
	 * return spearman correlation 
	 * @param x
	 * @param y
	 * @return  double
	 */
	public static double spearmanCor(int[] x, int[] y){
		
		int sum =0;
		int n=x.length;
		for (int i=0; i<n;i++){
			sum+=(x[i]-y[i])*(x[i]-y[i]);
		}
		
		
		return 1.0-6.0*sum/(double)(n*(n*n-1));
	}
	
	/**given file name
	 * save (add to the end, not overwrite) to a file of ranked operations(opname, wsdl name, score) and rank numbers
	 * @param origOps  this is the order number of all the operations
	 * @param rankedOps
	 * @param fileName
	 * @param notes     print out some notes at the beginning of the file content
	 */
	public void printFile (List<OpWsdl> origOps, List<OpWsdlScore> rankedOps, String fileName, String notes){
		
		int[] orders = this.getRankOrder(origOps, rankedOps);
		try {    
            FileWriter fw = new FileWriter(fileName,true);    
            PrintWriter pw=new PrintWriter(fw); 
            pw.println("======================================"+ notes+"=====================================================================");   
           
            for (int i=0; i<rankedOps.size(); i++){
	             pw.println((i+1) + "**" + rankedOps.get(i).getOpName()+"--------------------------"+ rankedOps.get(i).getWsdlName()+"---------------------------"+ rankedOps.get(i).getScore());   

            }	
            for (int j=0; j<orders.length; j++){
	             pw.println(orders[j]);
           }	
            pw.close () ;    
            fw.close () ;                
            
        } catch (IOException e) {    
            e.printStackTrace();    
        } 
	}
	
	/**given original ordered operation list and ranked operation list
	 * return a array of rank orders for original operation list.
	 * @param origOps
	 * @param rankedOps
	 * @return
	 */
	public int[] getRankOrder (List<OpWsdl> origOps, List<OpWsdlScore> rankedOps){
		int[] orders = new int[origOps.size()];
		for(int i=0; i<origOps.size(); i++){
			for(int j=0; j<rankedOps.size(); j++){
				if (origOps.get(i).getOpName().equals(rankedOps.get(j).getOpName()) && origOps.get(i).getWsdlName().equals(rankedOps.get(j).getWsdlName())){
					orders[i]=j+1;
					break;
				}
			}
		}
		
		return orders;
	}

	/**constructor
	 * 
	 */
	public EvUtils() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] x ={1,2,3,4,5};
		int[] y = {1,4,5,6,9};
		System.out.println(spearmanCor(x, y));

	}

}
