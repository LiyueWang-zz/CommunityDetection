# -*- coding: utf-8 -*-

from __future__ import division

import sys, codecs,string,re,copy,math,datetime

from scipy.sparse import csr_matrix, coo_matrix

import scipy

import numpy as np

from sklearn.cluster import k_means

from sklearn import random_projection

 

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





def Kmeans(X,k_cluster,fcluster):

    l=X.shape[0]

    print l

    clusters=[[] for x in range(k_cluster)]

    print 

    print

    graph=X

    centers,labels,inertia = k_means(graph, n_clusters=k_cluster)

    

    print "centers:",centers
    print "inertia:",inertia
    print

    print "labels:"

    print labels

    fcluster.write("Nodes: "+str(l)+"\n")  #write node_num in the first line

    for i in range(l):

        clusters[labels[i]].append(i)

    for j in range(k_cluster):

        #fcluster.write("cluster-"+str(j)+": ")

        for node in clusters[j]:

            fcluster.write(str(node+1)+"	")

        fcluster.write('\n')

  

	

if __name__ == '__main__':

    if len(sys.argv) >2:   

    #datafile="test1.txt"

	starttime=datetime.datetime.now()

	print "starttime=",starttime



    	datafile=sys.argv[1]

        k_cluster=string.atoi(sys.argv[2])

        #num_nodes=string.atoi(sys.argv[3])

        clusterfile=datafile[0:-4]+"_cs_"+sys.argv[2]+"_GRPkmeans.txt"

        fcluster=open(clusterfile,'w')



	#Step-1: get the similarity matrix from file

    	#cosinesm=getSimilarity(datafile,num_nodes)

	cosinesm=getSimilarityV3(datafile)



	endtime1=datetime.datetime.now()

        runtime1=(endtime1-starttime).seconds

        print "runtime for get simiMatrix=",runtime1,"s"

	print "cosinesm.shape: ",cosinesm.shape[0],cosinesm.shape[1]

		

	#Step-2: Dimensional Reduction by Random Projection

	transformer=random_projection.GaussianRandomProjection(eps=0.6)

	cosinesm_new = transformer.fit_transform(cosinesm)

	print "cosinesm_new.shape: ",cosinesm_new.shape[0],cosinesm_new.shape[1]

		

		

	#Step-3: Kmeans

        #Kmeans(cosinesm_new,k_cluster,fcluster)

        fcluster.close()



        endtime=datetime.datetime.now()

        runtime=(endtime-starttime).seconds

        print "runtime=",runtime,"s"



    else:

        print >>sys.stderr,"argv err! use: Kmeans.py *pageCont*.txt k_cluster"
