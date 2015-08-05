from __future__ import division
import sys, codecs,string,re,copy,math,datetime
from numpy import log2
import scipy
import numpy
from sklearn.cluster import spectral_clustering
from sklearn.preprocessing import scale
from sklearn import mixture
 
# get similarity matrix from double[][] version
def getSimilarity(datafile,num_nodes):
    #step-1: read pagecm from datafile
    fdata=open(datafile)
    cosinesm=[[] for x in range(num_nodes)]   
    ispagecm=False 
    index=0

    for line in fdata:
        parts=line.split()
        for part in parts:
            num=float(part.strip())
            cosinesm[index].append(num)
        index+=1     
        
    return cosinesm

# get similarity matrix from sparsematrix version
def getSimilarityV2(datafile):
    fdata=open(datafile)
    first_line=fdata.readline()
    num_nodes=string.atoi(first_line.strip().split()[1])

    cosinesm=[[0 for col in range(num_nodes)] for row in range(num_nodes)]
    #ispagecm=False
    index=0
    for line in fdata:
        parts=line.split()
        for part in parts:
            num=float(part.strip().split(':')[1])
            cosinesm[index][int(part.strip().split(':')[0])]=num
            cosinesm[int(part.strip().split(':')[0])][index]=num
        index+=1

    return cosinesm	

def GMMCluster(cosinesm,k_cluster,fgmm,fgmm2):
    print "\nGMM cluster begin..."
    l=len(cosinesm)
    clusters=[[] for x in range(k_cluster)]
    print "\ncosinematrix size="+str(l)
    g=mixture.GMM(n_components=k_cluster)
    g.fit(cosinesm)
    cluster=g.predict(cosinesm)
    print cluster
    cluster_prob=g.predict_proba(cosinesm)
    print "\n\n",cluster_prob

    fgmm.write("Nodes: "+str(l)+"\n")  #write node_num in the first line
    fgmm2.write("Nodes: "+str(l)+"\n")
    for i in range(l):
        clusters[cluster[i]].append(i)
    for j in range(k_cluster):
        for node in clusters[j]:
            fgmm.write(str(node)+"	")
            fgmm2.write(str(node)+"	")
        fgmm.write('\n')
        fgmm2.write('\n')
    fgmm.write('\n\n')
    fgmm2.write('\n\n')

    fgmm2.write("probability matrix:\n")
    node=1
    for probs in cluster_prob:
        fgmm2.write(str(node).ljust(5))
        node+=1
        for prob in probs:
            fgmm2.write(str(prob).ljust(25))
        fgmm2.write('\n')

    print "GMM cluster sucess"
  
	
if __name__ == '__main__':
    if len(sys.argv) >2:   
    #datafile="test1.txt"
	starttime=datetime.datetime.now()
		
    	datafile=sys.argv[1]
        k_cluster=string.atoi(sys.argv[2])
    	#num_nodes=string.atoi(sys.argv[3])

        clusterfile=datafile[0:-4]+"_cs_"+sys.argv[2]+"_gmmcluster.txt"
        clusterfile2=datafile[0:-4]+"_cs_"+sys.argv[2]+"_gmmcluster_withp.txt"

        fcluster=open(clusterfile,'w')
        fcluster2=open(clusterfile2,'w')

    	#cosinesm=getSimilarity(datafile,num_nodes)
	cosinesm=getSimilarityV2(datafile)
        GMMCluster(cosinesm,k_cluster,fcluster,fcluster2)

        fcluster.close()
	fcluster2.close()
	
	endtime=datetime.datetime.now()
        runtime=(endtime-starttime).seconds
        print "runtime=",runtime,"s"

    else:
        print >>sys.stderr,"argv err! use: gmmCluster.py *simiMatrix*.txt k_cluster"
