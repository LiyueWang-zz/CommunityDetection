import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.Math;

public class ExpEvaluation {
	
	public static void main(String[] args) throws Exception
	{
		long begin=System.currentTimeMillis();	
		if(args.length==0)
		{
			printHelp();
		}
		else
		{
			String model=args[0];
			if(model.equals("-1"))
			{
				String labelfile=args[1];   //the ground-truth community file
				String expfile=args[2];     //the experiment result of clustering file
				String dsName="";
				
				averageF1(labelfile, expfile,dsName);
				omegaIndex(labelfile, expfile,dsName);
				NMI(labelfile, expfile,dsName);
				
			}
			else if(model.equals("-2"))
			{
				String labeldir=args[1];
				String expdir=args[2];
				
				File commdir=new File(labeldir);
				File[] comms=commdir.listFiles();
				for(int i=0;i<comms.length;i++)
				{
					String labelfile=comms[i].getPath(); //TODO:check
					String file_index=comms[i].getName().split("\\.")[1];
					String expfile=expdir+"/"+"re_edgefile."+file_index+"_approx_0.01-simiMatrix_cs_2_gmmcluster.txt"; //TODO: 
//					String expfile=expdir+"/"+"re_edgefile."+file_index+"_approx_0.01-simiMatrix_cs_2_specluster.txt"; 			
					String dsName="File"+file_index;
					
					averageF1(labelfile, expfile,dsName);
					omegaIndex(labelfile, expfile,dsName);
					NMI(labelfile, expfile,dsName);
				}
				
			}
			else 
				printHelp();
		}
		
		evaluation();
		
		//test time
		long end=System.currentTimeMillis();
		long time_cost=end-begin; //unit: ms
		System.out.println("Time cost: "+Long.toString(time_cost)+"ms");
	}
	
	/**
	 * Usage print
	 */
	static void printHelp()
	{
		System.out.println("Usage: ExpEvaluation [-option]");
		System.out.println("where options include:");
		System.out.println("	-1	labelfile expfile //evaluate single file");
		System.out.println("	-2	labeldir expdir //evaluate multiple files in a directory");
	}
	
	/**
	 * Useless: Evaluation for big data sets
	 */
	public static void evaluation() throws IOException
	{
		
		//amazon-0.05
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\com-amazon.top5000.cmty.clean0.05.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\result\\com-amazon.ungraph0.05.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_75_gmmcluster.txt";
		
		//amazon-0.1
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\com-amazon.top5000.cmty.clean0.1.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\result\\com-amazon.ungraph0.1.small.reindex_simimatrix_0.85_1.0000000000000002E-7_cs_151_gmmcluster.txt";
		
		//amazon-0.2
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\com-amazon.top5000.cmty.clean0.2.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\AMAZON\\result\\.txt";
		
		//dblp-0.04
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\com-dblp.top5000.cmty.clean0.04.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\result\\com-dblp.ungraph0.04.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_198_gmmcluster.txt";
		
		//dblp-0.05
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\com-dblp.top5000.cmty.clean0.05.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\result\\com-dblp.ungraph0.05.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_248_gmmcluster.txt";
		
		//dblp-0.08
//		String labelfile1="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\com-dblp.top5000.cmty.clean0.08.reindex.txt";
//		String exfile1="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\result\\com-dblp.ungraph0.08.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_396_gmmcluster.txt";
//		int nodes1=6522;
		
//		ExpEvaluation.averageF1(labelfile1, exfile1);
//		ExpEvaluation.omegaIndex(labelfile1, exfile1,nodes1,"dblp-0.08");
//		ExpEvaluation.NMI(labelfile, exfile, nodes);
		
		//youtube-0.04:2146
//		String labelfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\com-youtube.top5000.cmty.clean0.04.reindex.txt";
//		String exfile="E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\result\\com-youtube.ungraph0.04.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_190_gmmcluster.txt";
		
		//batch test 
		ArrayList<String> dsName=new ArrayList<String>();
		ArrayList<String> labelfile=new ArrayList<String>();
		ArrayList<String> exfile=new ArrayList<String>();
		ArrayList<Integer> nodes=new ArrayList<Integer>();
		
		dsName.add("dblp-0.1-app-0.1");
		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\com-dblp.top5000.cmty.clean0.1.reindex.txt");
		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\dblp-0.1-app-0.01-sc0Cluster.txt");
		nodes.add(2051);
		
	/*	
		//dblp-0.04: 2051
		dsName.add("dblp-0.04-cm");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);
		
		dsName.add("dblp-0.04-sm");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_sparseMatrix_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);

		dsName.add("dblp-0.04-app-0.01");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_approx_0.01_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);
		
		dsName.add("dblp-0.04-app-0.005");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_approx_0.005_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);
		
		dsName.add("dblp-0.04-app-0.001");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_approx_0.001_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);
		
		dsName.add("dblp-0.04-app-0.0001");
		labelfile.add("/home/liyuew/Dropbox/CommunityDetection/dblp/com-dblp.top5000.cmty.clean0.04.reindex.txt");
		exfile.add("/home/liyuew/CentOS/test/dblp-0.04/com-dblp.ungraph0.04.small.reindex_approx_0.0001_simiMatrix_cs_198_gmmcluster.txt");
		nodes.add(2051);
	*/
		
//		//youtube-0.05: 3774
//		dsName.add("youtube-0.05");
//		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\com-youtube.top5000.cmty.clean0.05.reindex.txt");
//		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\result\\com-youtube.ungraph0.05.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_238_gmmcluster.txt");
//		nodes.add(3774);
//		
//		//youtube-0.1
//		dsName.add("youtube-0.1");
//		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\com-youtube.top5000.cmty.clean0.1.reindex.txt");
//		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\YOUTUBE\\result\\com-youtube.ungraph0.1.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_477_gmmcluster.txt");
//		nodes.add(5313);
//		
//		//livej-0.04
//		dsName.add("livej-0.04");
//		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\LIVE\\com-lj.top5000.cmty.clean0.04.reindex.txt");
//		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\LIVE\\result\\com-lj.ungraph0.04.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_188_gmmcluster.txt");
//		nodes.add(4084);
//		
//		//livej-0.05
//		dsName.add("livej-0.05");
//		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\LIVE\\com-lj.top5000.cmty.clean0.05.reindex.txt");
//		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\LIVE\\result\\com-lj.ungraph0.05.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_235_gmmcluster.txt");
//		nodes.add(6341);
//		
//		//orkut-0.005: 5315
//		dsName.add("orkut-0.005");
//		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\ORKUT\\com-orkut.top5000.cmty.clean0.005.reindex.txt");
//		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\ORKUT\\result\\com-orkut.ungraph0.005.small.reindex_simimatrix_0.85_1.0000000000000004E-8_cs_24_gmmcluster.txt");
//		nodes.add(5315);
		
		for(int i=0;i<nodes.size();i++)
		{
			averageF1(labelfile.get(i), exfile.get(i), dsName.get(i));
			omegaIndex(labelfile.get(i), exfile.get(i),dsName.get(i));
			NMI(labelfile.get(i), exfile.get(i),dsName.get(i));
		}
		
	}
	
	/**
	 * evaluation 
	 */
	//Average F1 Score
	public static double averageF1(String labelfile, String exfile,String dsName) throws IOException
	{
		ArrayList<ArrayList<Integer>> labelcomms=new ArrayList<ArrayList<Integer>>();
		ArrayList<Double> labelF1=new ArrayList<Double>();
		ArrayList<ArrayList<Integer>> expcomms=new ArrayList<ArrayList<Integer>>();
		ArrayList<Double> expF1=new ArrayList<Double>();
		
		BufferedReader labr=new BufferedReader(new FileReader(labelfile));
		BufferedReader exbr=new BufferedReader(new FileReader(exfile));
		String line="";
		//read community labels from label datafile
		while((line=labr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			labelcomms.add(com);
		}
		labr.close();
		//read community labels from experiment result datafile
		while((line=exbr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			expcomms.add(com);
		}
		exbr.close();
		//g(i)
		double labelsumf1=0.0;
		for(int i=0;i<labelcomms.size();i++)
		{
			double maxf1=0.0;
			for(int j=0;j<expcomms.size();j++)
			{
				double f1=pairF1(labelcomms.get(i),expcomms.get(j));
				if(f1>maxf1)
					maxf1=f1;
			}
			labelF1.add(maxf1);
			labelsumf1+=maxf1;
		}
		//g'(i)
		double expsumf1=0.0;
		for(int i=0;i<expcomms.size();i++)
		{
			double maxf1=0.0;
			for(int j=0;j<labelcomms.size();j++)
			{
				double f1=pairF1(expcomms.get(i),labelcomms.get(j));
				if(f1>maxf1)
					maxf1=f1;
			}
			expF1.add(maxf1);
			expsumf1+=maxf1;
		}
		//compute averge F1 score
		double avergef1=(labelsumf1+expsumf1)/(2*expcomms.size());
		System.out.println("the average f1("+dsName+")="+avergef1);
		return avergef1;
		
		
	}
	// each community nodes, order from small to large index
	public static double pairF1(ArrayList<Integer> al1,ArrayList<Integer> al2)
	{
		double f1=0.0;
		int a1=0;
		int a2=0;
		int b=0;
		int c=0;
		for(int i=0;i<al1.size();i++)
		{
			if(al2.contains(al1.get(i)))
				a1++;
			else
				b++;
		}
		for(int i=0;i<al2.size();i++)
		{
			if(al1.contains(al2.get(i)))
				a2++;
			else
				c++;
		}
		if(a1!=a2)
			System.out.println("Error in pairF1!!!");
		f1=2.0*a1/(2*a1+b+c);
		return f1;
	}

	//Omega Index
	public static double omegaIndex(String labelfile, String exfile, String dsName)throws IOException
	{
		double omega=0.0;
		ArrayList<ArrayList<Integer>> labelcomms=new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> expcomms=new ArrayList<ArrayList<Integer>>();
		
		BufferedReader labr=new BufferedReader(new FileReader(labelfile));
		BufferedReader exbr=new BufferedReader(new FileReader(exfile));
		String line="";
		//read community labels from label datafile
		while((line=labr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			labelcomms.add(com);
		}
		labr.close();
		//read community labels from experiment result datafile
		line=exbr.readLine(); //read the first line"Nodes: xx" to get the #of nodes
		int v=Integer.parseInt(line.trim().split("\\s")[1]);
		while((line=exbr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			expcomms.add(com);
		}
		exbr.close();
		
		int share=0;
		for(int i=0;i<v;i++)
		{
			for(int j=i;j<v;j++)
			{
				int label=0;
				int exp=0;
				for(int m=0;m<labelcomms.size();m++)
					if(labelcomms.get(m).contains(i)&&labelcomms.get(m).contains(j))
						label++;
				for(int m=0;m<expcomms.size();m++)
					if(expcomms.get(m).contains(i)&&expcomms.get(m).contains(j))
						exp++;
				if(label==exp)
					share++;
			}
		}
		
		omega=1.0*share/(v*v);
		System.out.println("omega index("+dsName+")="+omega);
		return omega;
	}
	
	//normalized mutual information
	public static double NMI(String labelfile, String exfile,String dsName)throws IOException
	{
		double nmi=0.0;
		ArrayList<ArrayList<Integer>> labelcomms=new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> expcomms=new ArrayList<ArrayList<Integer>>();
		
		BufferedReader labr=new BufferedReader(new FileReader(labelfile));
		BufferedReader exbr=new BufferedReader(new FileReader(exfile));
		String line="";
		line=exbr.readLine(); //read the first line"Nodes: xx" to get the #of nodes
		int v=Integer.parseInt(line.trim().split("\\s")[1]);
		//read community labels from label datafile
		double hx=0.0;
		double px=0.0;
		while((line=labr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			if(com.size()==0)
				continue;
			labelcomms.add(com);
			px=1.0*com.size()/v;
			hx+=px*Math.log(px);
		}
		hx=hx*(-1);
		labr.close();
		//read community labels from experiment result datafile
		double hy=0.0;
		double py=0.0;
		
		while((line=exbr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			if(com.size()==0)
			{
				//TODO: why line=="" not in
				if(line=="")
					System.out.println("A"+line+"A");
//				System.out.println("H"+line+"H");
				continue;
			}
			expcomms.add(com);
			py=1.0*com.size()/v;
			hy+=py*Math.log(py);
		}
		hy=hy*(-1);
		exbr.close();
		
		double ixy=0.0;
		
//		double I=0.0;
//		double Ix=0.0;
//		double Iy=0.0;
//		double Ixy=0.0;
		for(int i=0;i<labelcomms.size();i++)
		{
//			Iy=0.0;
			for(int j=0;j<expcomms.size();j++)
			{
//				Iy+=expcomms.get(j).size()*Math.log(1.0*expcomms.get(j).size()/v);
				int nxy=0;
				for(int m=0;m<labelcomms.get(i).size();m++)
					if(expcomms.get(j).contains(labelcomms.get(i).get(m)))
						nxy++;
				if(nxy!=0)
				{
					//TODO: check the p(x,y)
					//ixy=p(x,y)log(p(x,y)/(p(x)*p(y)))
					ixy+=(1.0*nxy/v)*Math.log(1.0*nxy*v/(expcomms.get(j).size()*labelcomms.get(i).size()));
//					Ixy+=1.0*nxy*Math.log(1.0*nxy*v/(expcomms.get(j).size()*labelcomms.get(i).size()));
				}
			}
//			Ix+=labelcomms.get(i).size()*Math.log(1.0*labelcomms.get(i).size()/v);
		}
//		I=(-2)*Ixy/(Ix+Iy);
		nmi=2*ixy/(hx+hy);
//		System.out.println("hx="+hx);
//		System.out.println("hy="+hy);
//		System.out.println("ixy="+ixy);
		System.out.println("NMI("+dsName+")="+nmi);
//		System.out.println("I("+dsName+")="+I);
		
		return nmi;
	}

	

}
