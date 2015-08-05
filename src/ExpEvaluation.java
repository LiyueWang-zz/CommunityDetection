import java.io.*;
import java.util.*;
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
				//omegaIndex(labelfile, expfile,dsName);
				omegaIndexImprove(labelfile, expfile,dsName);
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
					omegaIndexImprove(labelfile, expfile,dsName);
					NMI(labelfile, expfile,dsName);
				}
				
			}
			else 
				printHelp();
		}
		
//		evaluation();
		
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

		
		//batch test 
		ArrayList<String> dsName=new ArrayList<String>();
		ArrayList<String> labelfile=new ArrayList<String>();
		ArrayList<String> exfile=new ArrayList<String>();
		ArrayList<Integer> nodes=new ArrayList<Integer>();
		
		dsName.add("dblp-0.1-app-0.1");
		labelfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\com-dblp.top5000.cmty.clean0.1.reindex.txt");
		exfile.add("E:\\MyDropbox\\Dropbox\\Study\\SFU\\SFU-CourseStudy\\2014Fall-726-A3\\ASN\\project\\DBLP\\dblp-0.1-app-0.01-sc0Cluster.txt");
		nodes.add(2051);

		
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
		String line=exbr.readLine();
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
		long a1=0;
		long a2=0;
		long b=0;
		long c=0;
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
	//TODO: improve 
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
		int shareNoneZero=0;
		int shareZero=0;
		int exp_num=0;
		int label_num=0;
		int total=0;
		for(int i=0;i<v;i++)
		{
			for(int j=i+1;j<v;j++)
			{
				int label=0;
				int exp=0;
				for(int m=0;m<labelcomms.size();m++)
				{
					if(labelcomms.get(m).contains(i)&&labelcomms.get(m).contains(j))
						label++;
				}
				for(int m=0;m<expcomms.size();m++)
				{
					if(expcomms.get(m).contains(i)&&expcomms.get(m).contains(j))
						exp++;
				}
				if(label==exp)
					share++;
				if(label==exp&&label!=0)	
					shareNoneZero++;
				if(label==exp&&label==0)	
					shareZero++;
				if(label!=0)	label_num++;
				if(exp!=0)	exp_num++;
				total++;
			}
		}
		
		omega=1.0*share/(v*v);
		System.out.println("share="+share+", #nodes="+v+", shareNoneZero="+shareNoneZero+", #exp="+exp_num+", #label="+label_num+", shareZero="+shareZero+", total="+total);
		System.out.println("omega index("+dsName+")="+omega);
		System.out.println();
		return omega;
	}

	//Omega Index Improve 
	public static double omegaIndexImprove(String labelfile, String exfile, String dsName)throws IOException
	{
		double omega=0.0;
		//ArrayList<ArrayList<Integer>> labelcomms=new ArrayList<ArrayList<Integer>>();
		//ArrayList<ArrayList<Integer>> expcomms=new ArrayList<ArrayList<Integer>>();

		HashMap<ArrayList<Integer>,Integer> labelMap=new HashMap<ArrayList<Integer>,Integer>();
		HashMap<ArrayList<Integer>,Integer> expMap=new HashMap<ArrayList<Integer>,Integer>();
		
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
			for(int i=0;i<com.size();i++)
			{
				for(int j=i+1;j<com.size();j++)
				{
					ArrayList<Integer> pair=new ArrayList<Integer>();
					if(com.get(i)<com.get(j))
					{
						pair.add(com.get(i));
						pair.add(com.get(j));
					}
					else
					{
						pair.add(com.get(j));
						pair.add(com.get(i));
					}
					if(labelMap.containsKey(pair))
					{
						int count=labelMap.get(pair)+1;
						labelMap.remove(pair);
						labelMap.put(pair,count);
					}
					else	labelMap.put(pair,1);
				}
			}
			//labelcomms.add(com);
		}
		labr.close();
		//read community labels from experiment result datafile
		line=exbr.readLine(); //read the first line"Nodes: xx" to get the #of nodes
		long v=Long.parseLong(line.trim().split("\\s")[1]);
		while((line=exbr.readLine())!=null)
		{
			ArrayList<Integer> com=new ArrayList<Integer>();
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens())
			{
				int ele=Integer.parseInt(st.nextToken());
				com.add(ele);
			}
			//TODO: improve here can sort com firstly
			for(int i=0;i<com.size();i++)
			{
				for(int j=i+1;j<com.size();j++)
				{
					ArrayList<Integer> pair=new ArrayList<Integer>();
					if(com.get(i)<com.get(j))
					{
						pair.add(com.get(i));
						pair.add(com.get(j));
					}
					else
					{
						pair.add(com.get(j));
						pair.add(com.get(i));
					}
					if(expMap.containsKey(pair))
					{
						int count=expMap.get(pair)+1;
						expMap.remove(pair);
						expMap.put(pair,count);
					}
					else	
					{
						expMap.put(pair,1);
					}
				}
			}
			//expcomms.add(com);
		}
		exbr.close();

		long shareNoneZero=0;
		long diffNoneZero=0;
		Iterator it=expMap.entrySet().iterator();
		while(it.hasNext())
		{	
			Map.Entry entry=(Map.Entry)it.next();
			ArrayList<Integer> key=(ArrayList<Integer>)entry.getKey();
			int value=(int)entry.getValue();
			if(labelMap.containsKey(key))
			{
				int labelValue=labelMap.get(key);
				if(labelValue==value)	shareNoneZero++;
				else	diffNoneZero++;
			}
		}


		long total=v*(v-1)/2;  //here v mast be long, otherwise total will be negative, ex: int v=52675
		long shareZero=total-expMap.size()-(labelMap.size()-shareNoneZero-diffNoneZero);
		omega=1.0*(shareZero+shareNoneZero)/(v*v);
		System.out.println("share="+(shareNoneZero+shareZero)+", #nodes="+v+", shareNoneZero="+shareNoneZero+", #exp="+expMap.size()+", #label="+labelMap.size()+", shareZero="+shareZero+", total="+total+", long.max="+Long.MAX_VALUE);
		System.out.println("omega index("+dsName+")="+omega);
		return omega;
	}
	
	//normalized mutual information
	//TODO: check different version
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
