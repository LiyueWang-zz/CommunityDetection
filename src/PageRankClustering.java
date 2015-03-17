import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


//import main.MyPathCont;
//import main.SparseMatrix;
//import main.SparseMatrixEntry;
//import main.SparseMatrix;
//import weka.core.Instance;
//import weka.core.Instances;

//import main.MyGraph;
//import main.OriginalPageCont;
//import main.PageRank;
//import main.PathContVec;

/**
 * @author: LiyueWang
 * @time: 2014/09/15
 * @function: main class
 *  
 */
public class PageRankClustering {
	
	public static void main(String[] args) throws Exception
	{
		long begin=System.currentTimeMillis();	
		/*
		 * Shabnam's code
		 */
//		MyGraph graph = MyGraph.create_from_links_file("../data/dolphins/dolphins.net.digraph.txt");
//		PageRank.fix_graph_for_pagerank(graph);
		
//		int[] target_pages = new int[] {159951 /*spam*/, 458/*nonspam*/};
//		int[] target_pages = pick_some_target_pages(100);
//		int[] target_pages=new int[]{1,2,3};
//		PathContVec.run_stage1(target_pages, 0.85);
//		PathContVec.run_stage2(target_pages, 0.85, 105896555);
//        String outfile = "output.txt";
//		double[][] m = OriginalPageCont.calc_and_save_original_page_cont(graph, 0.85, outfile);
		
		
		/*step-1: compute page contribution matrix*/
//		String datafile="../data/dolphins/dolphins.net.digraph.txt";		
//		boolean weighted=false;	
//		double[][] adj_matrix=PageCont.init_adj_matrix(datafile,weighted);
//		double[][] tran_matrix=PageCont.init_tran_matrix(adj_matrix);
//		double[][] pathContribution=PageCont.compute_pathcont(tran_matrix);
//		double[] pageRank=PageCont.compute_pageRank(tran_matrix);
		
//		SparseMatrix sm= get_path_cont_matrix(tran_matrix,tran_matrix.length,pageRank,0.85,0);
//		System.out.println(sm);
//		double[][] pageCont=PageCont.pageContribution(datafile,weighted);
		
		/*step-2: compute similarity matrix based on page contribution matrix*/
//		double[][] simi_matrix=PageCont.compute_similarity(pageCont);
		
		/*step-3: clustering based on similarity matrix */
//		Instances data=null;
//		SpectralClusterer sc=new SpectralClusterer();
//		sc.buildClusterer(data);
		
		
		/***for ML project***/
//		String datafile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\testbenchmark\\com-amazon.ungraph0.05.small.reindex.txt";		
//		boolean weighted=false;	
//		
//		double[][] adj_matrix=PageCont.init_adj_matrix(datafile,weighted);
//		double[][] tran_matrix=PageCont.init_tran_matrix(adj_matrix);
//		double[] pageRank=PageCont.compute_pageRank_thresh(tran_matrix);
//		double[][] pathContribution=PageCont.compute_pathcont_thresh(tran_matrix);
//		double[][] pageContribution=PageCont.compute_pagecont(pageRank,pathContribution);
//		double[][] simi_matrix=PageCont.compute_similarity(pageContribution);
		
		//writeToFile(datafile,simi_matrix);
		
//		String datafile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\testbenchmark\\com-amazon.ungraph0.05.small.reindex.txt";		
//		compareV1V2(datafile);
		
		System.out.println("Hello Word\t!");
		
//		MyGraph mg=MyGraph.create_from_file(datafile);
//		double[] pr=PageCont.compute_pageRank_thresh(mg, 0.85, null);
//		for(int i=0;i<pr.length;i++)
//			System.out.print(pr[i]+" ");
//		System.out.println();
//		SparseMatrix sm=SparseMatrix.create_from_graph(mg, 0.85);
//		SparseMatrix pathcont=PageCont.get_path_cont_matrix(sm,pageRank);
//		double[][] pathcont_matrix=pathcont.get_2dim_array();
//		writeToFile(datafile,pathcont_matrix);
		
		//test time
		long end=System.currentTimeMillis();
		long time_cost=end-begin; //unit: ms
		System.out.println("Time cost: "+Long.toString(time_cost)+"ms");
	}

	// compare the result between double[][] version and SparseMatrix version to make sure the correct of SparseMatrix version
	public static void compareV1V2(String datafile) throws Exception
	{
		//for PageContV1
		//String datafile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\testbenchmark\\com-amazon.ungraph0.05.small.reindex.txt";		
		String outfile=datafile.substring(0,datafile.length()-4)+"_";		
		boolean weighted=false;	
		
		double[][] adj_matrix=PageCont.init_adj_matrix(datafile,weighted);
//		save_to_file(adj_matrix,outfile+"adjMatrix1.txt");
		double[][] tran_matrix=PageCont.init_tran_matrix(adj_matrix);
		double[] pageRank=PageCont.compute_pageRank_thresh(tran_matrix);
		double[][] pathContribution=PageCont.compute_pathcont_thresh(tran_matrix);
		double[][] pageContribution=PageCont.compute_pagecont(pageRank,pathContribution);
		double[][] simi_matrix=PageCont.compute_similarity(pageContribution);
		
//		save_to_file(adj_matrix,outfile+"adjMatrix.txt");
//		save_to_file(tran_matrix,outfile+"tranMatrix.txt");
//		save_pr(pageRank,outfile+"pageRank.txt");
		save_to_file(pathContribution,outfile+"pathCont.txt");
		save_to_file(pageContribution,outfile+"pageCont.txt");
		save_to_file(simi_matrix,outfile+"simiMatrix.txt");
	}
	
	//pageCont[i]: for row i, all nodes's page contribution for node i
	public static void writeToFile(String infile, double[][] matrix) throws IOException
	{
		String outfile=infile.substring(0, infile.length()-4)+"_similarity_"+PageCont.DAMPLE_FACTOR+"_"+PageCont.CONVERGENCE_THRESH+".dat";
		FileWriter fw=new FileWriter(outfile);
		int n=matrix.length;
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				BigDecimal b=new   BigDecimal(matrix[i][j]);
				double pc=b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				//TODO: align the numbers
				fw.write(""+pc+"    ");
			}
			if(i<n-1)
				fw.write("\n");
		}
		
		fw.close();
	}
	
	//to compare with the result of SparseMatrix
	public static void save_to_file(double[][] matrix,String outfile) throws IOException
	{
		FileWriter fw=new FileWriter(outfile);
		int n=matrix.length;
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				if(matrix[i][j]==0.0)	continue;
				BigDecimal b=new   BigDecimal(matrix[i][j]);
				double value=b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				//TODO: align the numbers
				fw.write(""+j+":"+value+" ");
			}
			fw.write("\n");
		}
		
		fw.close();
	}
	
	public static void save_pr(double[] pr,String outfile)throws IOException
	{
		FileWriter fw=new FileWriter(outfile);
		int n=pr.length;
		for(int i=0;i<n;i++)
		{
				BigDecimal b=new   BigDecimal(pr[i]);
				double value=b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				//TODO: align the numbers
				fw.write(""+value+"\n");
		}
		
		fw.close();
	}

}
