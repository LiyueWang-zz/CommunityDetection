# -*- coding: utf-8 -*-
from __future__ import division
import sys, codecs,string,re,copy,math,datetime
from numpy import log2
from scipy.sparse import csr_matrix, coo_matrix
import scipy
import numpy
from sklearn.cluster import spectral_clustering,SpectralClustering
from sklearn.preprocessing import scale
from sklearn import mixture
 
def consineSimilarity(vec1,vec2):
    l=len(vec1)
    dotp=0.0
    sum1=0.0
    sum2=0.0
    for i in range(l):
        dotp+=vec1[i]*vec2[i]
        sum1+=vec1[i]*vec1[i]
        sum2+=vec2[i]*vec2[i]
    sum1=pow(sum1,0.5)
    sum2=pow(sum2,0.5)
    similarity=dotp/(sum1*sum2)
    return similarity

# get similarity matrix from double[][] version, return double[][]
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

# get similarity matrix from sparsematrix version, return double[][]
def getSimilarityV2(datafile):
    fdata=open(datafile)
    first_line=fdata.readline()
    num_nodes=string.atoi(first_line.strip().split()[1])

    cosinesm=scipy.zeros((num_nodes,num_nodes)) 
    #cosinesm=[[0 for col in range(num_nodes)] for row in range(num_nodes)]
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

# get similarity matrix from sparsematrix version, return scipy.sparse csr_matrix
def getSimilarityV3(datafile):
    fdata=open(datafile)
    first_line=fdata.readline()
    num_nodes=string.atoi(first_line.strip().split()[1])

    #cosinesm=scipy.zeros((num_nodes,num_nodes)) 
    data=[]
    row=[]
    col=[]
    index=0
    for line in fdata:
        parts=line.split()
        for part in parts:
            value=float(part.strip().split(':')[1])
            column=int(part.strip().split(':')[0])
            data.append(value)
            row.append(index)
	    col.append(column)
            data.append(value)
            row.append(column)
	    col.append(index)
        index+=1
    coo=coo_matrix((data,(row,col)),shape=(num_nodes,num_nodes))
    return csr_matrix(coo)


def spectralCluster(cosinesm,k_cluster,fcluster):
    l=cosinesm.shape[0]
    print l
    clusters=[[] for x in range(k_cluster)]
    print 
    print
    #print "cosinesm:"
    #print cosinesm.shape
    #graph=SpectralClustering.fit(cosinesm)
    graph=cosinesm
    #labels = spectral_clustering(graph, n_clusters=k_cluster, eigen_solver='arpack')
    labels = spectral_clustering(graph, n_clusters=k_cluster, n_components=500, eigen_solver='lobpcg')
    print 

    print
    print "labels:"
    print labels
    fcluster.write("Nodes: "+str(l)+"\n")  #write node_num in the first line
    for i in range(l):
        clusters[labels[i]].append(i)
    for j in range(k_cluster):
        #fcluster.write("cluster-"+str(j)+": ")
        for node in clusters[j]:
            fcluster.write(str(node)+"	")
        fcluster.write('\n')
  
	
if __name__ == '__main__':
    if len(sys.argv) >2:   
    #datafile="test1.txt"
	starttime=datetime.datetime.now()
	print "starttime=",starttime

    	datafile=sys.argv[1]
        k_cluster=string.atoi(sys.argv[2])
        #num_nodes=string.atoi(sys.argv[3])
        clusterfile=datafile[0:-4]+"_cs_"+sys.argv[2]+"_specluster.txt"
        fcluster=open(clusterfile,'w')

    	#cosinesm=getSimilarity(datafile,num_nodes)
	cosinesm=getSimilarityV3(datafile)

	endtime1=datetime.datetime.now()
        runtime1=(endtime1-starttime).seconds
        print "runtime for get simiMatrix=",runtime1,"s"

        spectralCluster(cosinesm,k_cluster,fcluster)
        fcluster.close()

        endtime=datetime.datetime.now()
        runtime=(endtime-starttime).seconds
        print "runtime=",runtime,"s"

    else:
        print >>sys.stderr,"argv err! use: spectralCluster.py *pageCont*.txt k_cluster"
