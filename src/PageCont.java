/**
 * @author: LiyueWang
 * @time: 2014/09/15
 * @function: calculate the page contribution
 *  
 *  Use PageContV2.java instead
 */
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

public class PageCont {
	public static int ITERATIONS=100;
	public static double DAMPLE_FACTOR=0.85;
	public static double CONVERGENCE_THRESH=0.000001;
	
	public static double[][] pageContribution(String datafile,boolean weighted) throws IOException
	{
		double[][] adj_matrix=init_adj_matrix(datafile,weighted);
		double[][] tran_matrix=init_tran_matrix(adj_matrix);
		double[][] pathContribution=compute_pathcont(tran_matrix);
		double[] pageRank=compute_pageRank(tran_matrix);
		double[][] pageContribution=compute_pagecont(pageRank,pathContribution);
		
		return pageContribution;
		
	}
	//if graph with weight, adj_matrix element is weight, otherwise 1
	//adj_matrix[i][j]=1 if exits edge(j,i)
	    //datafile: firstline: Node: 34
		//datafile of unweighted graph form: node1 node2 //only one space between node1 node2
		//datafile of weighted graph form: node1 node2 weight
	// the node is index from 0
	public static double[][] init_adj_matrix(String datafile, boolean weighted) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
		String line=null;	
		//read the # of nodes from the first line: Nodes: xxx
		String firstline=reader.readLine();
		int nodes_num=Integer.parseInt(firstline.split("\\s")[1]);
		double[][] adj_matrix=new double[nodes_num][nodes_num];
		setConThresh(nodes_num);
		System.out.println("\ninitial adjacency matrix of directed graph......");
		while ((line = reader.readLine()) != null) {
			//TODO: for small data line.split("\\s") 
			//      for big data  line.split("\\t") 
			int sindex=Integer.parseInt(line.split("\\t")[0]);   
			int tindex=Integer.parseInt(line.split("\\t")[1]);	
			//TODO: for some data indexed from 1 should be converted indexed from 0
			if(weighted)
				adj_matrix[tindex][sindex]=Double.parseDouble(line.split(" ")[2]);
			else
				adj_matrix[tindex][sindex]=1; 
		}
		System.out.println("initial adjacenct matrix successfully!");
		reader.close();
		
		return adj_matrix;
	}
	
	//set CONVERGENCE_THRESH according the number of nodes
	public static void setConThresh(int nodes_num)
	{
		double thresh=0.001;
		while(nodes_num>0)
		{
			nodes_num/=10;
			thresh*=0.1;
		}
		CONVERGENCE_THRESH=thresh;
		System.out.println("set the covergence thresh as="+thresh);
	}
	
	//transfer the adj_matrix to tran_matrix, tran_matrix[i][j]=adj_matrix[i][j]/outdegree(j)
	public static double[][] init_tran_matrix(double[][] adj_matrix) throws IOException
	{
		int n=adj_matrix.length;
		System.out.println("\ninitial transition matrix of directed graph......");
		for(int i=0;i<n;i++)
		{
			double out=0;
			for(int j=0;j<n;j++)
				out+=adj_matrix[j][i];
			for(int j=0;j<n;j++)
				if(out!=0.0)
					adj_matrix[j][i]/=out;  //use the adj_matrix directly, not change the name
		}
		System.out.println("initial transition matrix successfully!");
		
		return adj_matrix;
	}
	
	//TODO: improve efficiency 
	//compute pagerank with iteration
	public static double[] compute_pageRank(double[][] tran_matrix)
	{
		int n=tran_matrix.length;
		double[] prv=new double[n]; //page rank vector
		double[] new_prv=new double[n];
		for(int i=0;i<n;i++)
		{
			prv[i]=1.0/n;
		}
		for(int it=0;it<ITERATIONS;it++)
		{
			for(int i=0;i<n;i++)
			{
				new_prv[i]=0;
				for(int j=0;j<n;j++)
				{
					new_prv[i]+=tran_matrix[i][j]*prv[j];
				}
				new_prv[i]=new_prv[i]*DAMPLE_FACTOR+(1-DAMPLE_FACTOR)/n;
			}
			System.arraycopy(new_prv,0,prv,0,n);			
		}
		//System.out.println("\npagerank_vector:");	
		return prv;
	}
	
	//compute pagerank with CONVERGENCE_THRESH
	public static double[] compute_pageRank_thresh(double[][] tran_matrix)
	{
		int n=tran_matrix.length;
		double[] prv=new double[n]; //page rank vector
		double[] new_prv=new double[n];
		for(int i=0;i<n;i++)
		{
			prv[i]=1.0/n;
		}
		int it=0;
		for(it=0;;it++)
		{
			for(int i=0;i<n;i++)
			{
				new_prv[i]=0;
				for(int j=0;j<n;j++)
				{
					new_prv[i]+=tran_matrix[i][j]*prv[j];
				}
				new_prv[i]=new_prv[i]*DAMPLE_FACTOR+(1-DAMPLE_FACTOR)/n;
			}
			
			if(check_for_convergence(new_prv, prv) == true)
				break;
			System.arraycopy(new_prv,0,prv,0,n);			
		}
		System.out.println("\n pagerank_vector iteration#="+it+" for CONVERGENCE_THRESH="+CONVERGENCE_THRESH);	
		return prv;
	}
	
	//TODO: check array copy problem
	//for row i, all nodes's path contribution for node i
	public static double[][] compute_pathcont(double[][] tran_matrix)
	{
		int n=tran_matrix.length;
		double dample_power=DAMPLE_FACTOR;
		double[][] pathcm=new double[n][n]; // path contribution matrix
		double[][] tranmatrix_power=new double[n][n]; // L^t
		double[][] identity_matrix=new double[n][n];
		//M=(1-dample_factor)*sum(dample_factor^t*L^t) t:0->infinity  (L:trans_matrix)
	    	// when t=0, dample_factor^t*L^t=identity_matrix, when t=1: dample_factor^t*L^t=dample_factor*L
			// when t=1, sum=identity_matrix+dample_factor*L
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				if(i==j)
					identity_matrix[i][j]=1.0;
				else
					identity_matrix[i][j]=0.0;
				pathcm[i][j]=tran_matrix[i][j]*dample_power+identity_matrix[i][j];
				tranmatrix_power[i][j]=tran_matrix[i][j];
					
			}
		}
			//when t=2->ITERATIONS
		for(int it=2;it<ITERATIONS;it++)
		{
			dample_power*=DAMPLE_FACTOR;
			double[][] new_tranmatrix_power=new double[n][n];
			for(int i=0;i<n;i++)
			{
				for(int j=0;j<n;j++)
				{
					new_tranmatrix_power[i][j]=0;
					for(int k=0;k<n;k++)
					{
						new_tranmatrix_power[i][j]+=tranmatrix_power[i][k]*tran_matrix[k][j];
					}
					pathcm[i][j]+=new_tranmatrix_power[i][j]*dample_power;
				}
			}
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					tranmatrix_power[i][j]=new_tranmatrix_power[i][j];		
		}
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				pathcm[i][j]=(1-DAMPLE_FACTOR)*pathcm[i][j];
		
		return pathcm;
	}
	
	public static double[][] compute_pathcont_thresh(double[][] tran_matrix)
	{
		int n=tran_matrix.length;
		double dample_power=DAMPLE_FACTOR;
		double[][] pathcm=new double[n][n]; // path contribution matrix
		double[][] tranmatrix_power=new double[n][n]; // L^t
		double[][] identity_matrix=new double[n][n];
		//M=(1-dample_factor)*sum(dample_factor^t*L^t) t:0->infinity  (L:trans_matrix)
	    	// when t=0, dample_factor^t*L^t=identity_matrix, when t=1: dample_factor^t*L^t=dample_factor*L
			// when t=1, sum=identity_matrix+dample_factor*L
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				if(i==j)
					identity_matrix[i][j]=1.0;
				else
					identity_matrix[i][j]=0.0;
				pathcm[i][j]=tran_matrix[i][j]*dample_power+identity_matrix[i][j];
				tranmatrix_power[i][j]=tran_matrix[i][j];
					
			}
		}
			//when t=2->ITERATIONS
		boolean converged = true;
		int it=2;
		for(it=2;;it++)
		{
			converged = true;
			dample_power*=DAMPLE_FACTOR;
			double[][] new_tranmatrix_power=new double[n][n];
			for(int i=0;i<n;i++)
			{
				for(int j=0;j<n;j++)
				{
					new_tranmatrix_power[i][j]=0;
					for(int k=0;k<n;k++)
					{
						new_tranmatrix_power[i][j]+=tranmatrix_power[i][k]*tran_matrix[k][j];
					}
					pathcm[i][j]+=new_tranmatrix_power[i][j]*dample_power;
//					if(new_tranmatrix_power[i][j]*dample_power>=CONVERGENCE_THRESH)
//						converged=false;
				}
			}
			if(check_for_convergence(new_tranmatrix_power, tranmatrix_power,dample_power))
				break;
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					tranmatrix_power[i][j]=new_tranmatrix_power[i][j];		
		}
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				pathcm[i][j]=(1-DAMPLE_FACTOR)*pathcm[i][j];
		
		System.out.println("\n pathcm iteration#="+it+" for CONVERGENCE_THRESH="+CONVERGENCE_THRESH);			
		return pathcm;
	}
	
	//TODO:convergence 
	public static SparseMatrix get_path_cont_matrix(SparseMatrix matrix, double[] org_pagerank) throws Exception
	{
		int i = 0, n = matrix.n_rows;
		//SparseMatrix m = SparseMatrix.create_from_2d_array(matrix);
		SparseMatrix m=matrix;
		//Matrix "exp" will hold L^i at each step; initialize the same as m:
		//SparseMatrix exp = SparseMatrix.create_from_2d_array(matrix);
		SparseMatrix exp=matrix; //TODO: check deep copy???
		
		//Initialize the result matrix with identity matrix:
		SparseMatrix res = SparseMatrix.create_identity_matrix(n, n);
	
		while(true)	//Continue until convergence
		{
			//add "exp" (which is L^i) to "res":
			res = SparseMatrix.matrix_add(res, exp);
	
			//Check for convergence:exp.get_energy() <= 0.0000000001
			if(exp.get_energy() <= 0.0001)
				break;
			//Put L^{i+1} in "exp":
			exp = SparseMatrix.matrix_multiply(exp, m);
	        i++;
	        
			//Prune "exp" (L^i) with a small threshold:
			//int n_nonzero = exp.prune_by_prank(org_pagerank, prune_thresh);
			//System.out.println("Round " + (++i) + " done; nonzero = " + n_nonzero * 100 / (n * n) + "%");
		}
		System.out.println("Round " + (i+2));
		//Multiply all entries of res by (1-d)/n:
		//res.multiply_scalar((1. - d) / org_n);
		res.multiply_scalar(1. - DAMPLE_FACTOR);
		
		return res;
	}
	
	//for row i, all nodes's page contribution for node i
	public static double[][] compute_pagecont(double[] prv,double[][] pathcm) throws IOException
	{
		int n=pathcm.length;
		double[][] pagecm=new double[n][n]; // page contribution matrix
		//pagecm[i][j]=prv[j]*pathcm[i][j]/pathcm[j][j];
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				if(pathcm[j][j]==0.0)
	                System.err.println("pathcm[j][j]==0");
				pagecm[i][j]=prv[j]*pathcm[i][j]/pathcm[j][j];
			}
		}
		
		return pagecm;
	}
	
	// check whether convergence for page rank computation
	public static boolean check_for_convergence(double[] score, double[] old_score)
	{
		boolean converged = true;

		for(int i = 0; i < score.length; i++)
		{
			double diff = Math.abs(score[i] - old_score[i]) / Math.min(score[i], old_score[i]);
			if(diff >= CONVERGENCE_THRESH)
			{
				converged = false;
				break;
			}
		}
		return converged;
	}
	
	public static boolean check_for_convergence(double[][] matrix, double[][] old_matrix, double x)
	{
		boolean converged = true;

		for(int i = 0; i < matrix.length; i++)
		{
			for(int j = 0; j < matrix.length; j++)
			{
				double diff = Math.abs(matrix[i][j] - old_matrix[i][j]) / Math.min(matrix[i][j], old_matrix[i][j]);
				if(diff*x >= CONVERGENCE_THRESH)
				{
					converged = false;
					break;
				}
			}
		}
		return converged;
	}
	
	public static double[][] compute_similarity(double[][] pagecm) throws IOException
	{
		int n=pagecm.length;
		double[][] simi_matrix=new double[n][n];
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				simi_matrix[i][j]=cosine_similarity(pagecm[i],pagecm[j]);
			}
		}
		return simi_matrix;
	}
	
	public static double cosine_similarity(double[] vec1,double[] vec2)
	{
		double similarity=0.0;
		int n=vec1.length;
		double dotsum=0.0;
		double sum1=0.0;
		double sum2=0.0;
		for(int i=0;i<n;i++)
		{
			dotsum+=vec1[i]*vec2[i];
			sum1+=vec1[i]*vec1[i];
			sum2+=vec2[i]*vec2[i];
		}
		sum1=Math.pow(sum1, 0.5);
		sum2=Math.pow(sum2, 0.5);
		similarity=dotsum/(sum1*sum2);
		return similarity;
	}

	
	

	
}
