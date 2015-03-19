
import java.io.*;
import java.math.BigDecimal;
import java.nio.*;
import java.util.*;


@SuppressWarnings("unchecked")	//To avoid warning messages on LinkedList<...>[].
public class SparseMatrix
{
	int n_rows, n_cols;
	LinkedList<SparseMatrixEntry>[] rows;  //suppose in one row, the entries are ordered from small col_index to large col_index
	LinkedList<SparseMatrixEntry>[] cols;  //suppose ordered too

	public SparseMatrix(LinkedList<SparseMatrixEntry>[] rows, LinkedList<SparseMatrixEntry>[] cols)
	{
		this.rows = rows;
		this.cols = cols;
		this.n_rows = rows.length;
		this.n_cols = cols.length;
	}

	//TODO: check correct 
	public SparseMatrix(SparseMatrix matrix)
	{
		this.n_rows = matrix.rows.length;
		this.n_cols = matrix.cols.length;
		this.rows = new LinkedList[n_rows];
		this.cols = new LinkedList[n_cols];
		for(int i = 0; i < n_rows; i++)
		{
			this.rows[i] = new LinkedList<SparseMatrixEntry>();
			for(Iterator<SparseMatrixEntry> iter = matrix.rows[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
				this.rows[i].add(new SparseMatrixEntry(entry.value,entry.index));
			}
		}
		for(int i = 0; i < n_cols; i++)
		{
			this.cols[i] = new LinkedList<SparseMatrixEntry>(); 
			for(Iterator<SparseMatrixEntry> iter = matrix.cols[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
				this.cols[i].add(new SparseMatrixEntry(entry.value,entry.index));
			}
		}
	}
	
	public static SparseMatrix create_from_2d_array(double[][] m)
	{
		SparseMatrix res = create_zero_matrix(m.length, m[0].length);

		for(int row = 0; row < m.length; row++)
			for(int col = 0; col < m[row].length; col++)
			{
				res.rows[row].add( new SparseMatrixEntry(m[row][col], col) );
				res.cols[col].add( new SparseMatrixEntry(m[row][col], row) );
			}

		return res;
	}

	public static SparseMatrix create_zero_matrix(int n_rows, int n_cols)
	{
		LinkedList<SparseMatrixEntry>[] rows = new LinkedList[n_rows];
		LinkedList<SparseMatrixEntry>[] cols = new LinkedList[n_cols];
		for(int i = 0; i < n_rows; i++)
			rows[i] = new LinkedList<SparseMatrixEntry>();
		for(int i = 0; i < n_cols; i++)
			cols[i] = new LinkedList<SparseMatrixEntry>(); 

		return new SparseMatrix(rows, cols);
	}

	public static SparseMatrix create_identity_matrix(int n_rows, int n_cols)
	{
		LinkedList<SparseMatrixEntry>[] rows = new LinkedList[n_rows];
		LinkedList<SparseMatrixEntry>[] cols = new LinkedList[n_cols];
		for(int i = 0; i < n_rows; i++)
		{
			rows[i] = new LinkedList<SparseMatrixEntry>();
			rows[i].add( new SparseMatrixEntry(1., i) );
		}
		for(int i = 0; i < n_cols; i++)
		{
			cols[i] = new LinkedList<SparseMatrixEntry>(); 
			cols[i].add( new SparseMatrixEntry(1., i) );
		}

		return new SparseMatrix(rows, cols);
	}

	public static SparseMatrix matrix_multiply(SparseMatrix M_1, SparseMatrix M_2) throws Exception 
	{
		LinkedList<SparseMatrixEntry>[] res_rows = new LinkedList[M_1.n_rows];
		LinkedList<SparseMatrixEntry>[] res_cols = new LinkedList[M_2.n_cols];
		for(int i = 0; i < M_1.n_rows; i++)
			res_rows[i] = new LinkedList<SparseMatrixEntry>();
		for(int i = 0; i < M_2.n_cols; i++)
			res_cols[i] = new LinkedList<SparseMatrixEntry>(); 

		for(int r = 0; r < M_1.n_rows; r++)
		{
			for(int c = 0; c < M_2.n_cols; c++)
			{	
				double new_entry = 0;
				Iterator<SparseMatrixEntry> iter1 = M_1.rows[r].iterator();
				Iterator<SparseMatrixEntry> iter2 = M_2.cols[c].iterator();

				if(!iter1.hasNext() || !iter2.hasNext())
					break;

				SparseMatrixEntry m_entry1 = (SparseMatrixEntry) iter1.next();
				SparseMatrixEntry m_entry2 = (SparseMatrixEntry) iter2.next();
				while(true)
				{
					if(m_entry1.index == m_entry2.index)
					{
						new_entry += m_entry1.value * m_entry2.value;
						if(!iter1.hasNext() || !iter2.hasNext())
							break;
						m_entry1 = (SparseMatrixEntry)iter1.next();
						m_entry2 = (SparseMatrixEntry)iter2.next();
					}
					else if(m_entry1.index < m_entry2.index)
					{
						if(!iter1.hasNext())
							break;
						m_entry1 = (SparseMatrixEntry) iter1.next();
					}
					else //m_entry1.index > m_entry2.index
					{
						if(!iter2.hasNext())
							break;
						m_entry2 = (SparseMatrixEntry) iter2.next();
					}
				}

				if (new_entry != 0)
				{
					res_rows[r].add(new SparseMatrixEntry(new_entry, c));
					res_cols[c].add(new SparseMatrixEntry(new_entry, r));
				}	
			}
		}

		return new SparseMatrix(res_rows, res_cols);		
	}

	public static SparseMatrix matrix_add(SparseMatrix M_1, SparseMatrix M_2) throws Exception 
	{
		if(M_1.n_rows != M_2.n_rows || M_1.n_cols != M_2.n_cols)
			throw new Exception("Matrix dimensions mismatch!");

		LinkedList<SparseMatrixEntry>[] res_rows = new LinkedList[M_1.n_rows];
		LinkedList<SparseMatrixEntry>[] res_cols = new LinkedList[M_1.n_cols];
		for(int i = 0; i < M_1.n_rows; i++)
			res_rows[i] = new LinkedList<SparseMatrixEntry>();
		for(int i = 0; i < M_1.n_cols; i++)
			res_cols[i] = new LinkedList<SparseMatrixEntry>(); 

		for(int row = 0; row < M_1.n_rows; row++)
		{
			Iterator<SparseMatrixEntry> iter1 = M_1.rows[row].iterator();
			Iterator<SparseMatrixEntry> iter2 = M_2.rows[row].iterator();

			SparseMatrixEntry entry1 = (iter1.hasNext() ? iter1.next() : null);
			SparseMatrixEntry entry2 = (iter2.hasNext() ? iter2.next() : null);

			while(true)
			{
				SparseMatrixEntry new_row_entry;
				//entry1 == null means iter1 reached the end of list (similarly for entry2).
				if(entry1 != null && entry2 != null)
				{
					if(entry1.index == entry2.index)
					{
						new_row_entry = new SparseMatrixEntry(entry1.value + entry2.value, entry1.index);
						entry1 = (iter1.hasNext() ? iter1.next() : null);
						entry2 = (iter2.hasNext() ? iter2.next() : null);
					}
					else if(entry1.index < entry2.index)
					{
						new_row_entry = new SparseMatrixEntry(entry1.value, entry1.index);
						entry1 = (iter1.hasNext() ? iter1.next() : null);
					}
					else	//entry1.index > entry2.index
					{
						new_row_entry = new SparseMatrixEntry(entry2.value, entry2.index);
						entry2 = (iter2.hasNext() ? iter2.next() : null);
					}
				}
				else if(entry1 != null)	//entry2 == null
				{
					new_row_entry = new SparseMatrixEntry(entry1.value, entry1.index);
					entry1 = (iter1.hasNext() ? iter1.next() : null);
				}
				else if(entry2 != null)	//entry1 == null
				{
					new_row_entry = new SparseMatrixEntry(entry2.value, entry2.index);
					entry2 = (iter2.hasNext() ? iter2.next() : null);
				}
				else	//entry1 == entry2 == null
					break;

				int col = new_row_entry.index;
				SparseMatrixEntry new_col_entry = new SparseMatrixEntry(new_row_entry.value, row);

				res_rows[row].add(new_row_entry);
				res_cols[col].add(new_col_entry);
			}
		}

		return new SparseMatrix(res_rows, res_cols);		
	}

	// parameter reference causes problem, have to new a SparseMatrix to return
	public static SparseMatrix matrix_multiply_scalar(SparseMatrix M_1,double x)
	{	
		/**
		 * Version 1: 
		 */
		/*
		SparseMatrix matrix=new SparseMatrix(M_1);
		for(int i = 0; i < M_1.n_rows; i++)
		{
			
			for(Iterator<SparseMatrixEntry> iter = matrix.rows[i].iterator(); iter.hasNext(); )
			{
				iter.next().value*=x;
			}
		}
		for(int i = 0; i < M_1.n_cols; i++)
		{ 
			for(Iterator<SparseMatrixEntry> iter = matrix.cols[i].iterator(); iter.hasNext(); )
			{
				iter.next().value*=x;
			}
		}
		return matrix;
		*/

		/**
		 * Version 2
		 */
		LinkedList<SparseMatrixEntry>[] res_rows = new LinkedList[M_1.n_rows];
		LinkedList<SparseMatrixEntry>[] res_cols = new LinkedList[M_1.n_cols];
		for(int i = 0; i < M_1.n_rows; i++)
		{
			res_rows[i] = new LinkedList<SparseMatrixEntry>();
			for(Iterator<SparseMatrixEntry> iter = M_1.rows[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
				res_rows[i].add(new SparseMatrixEntry(entry.value * x,entry.index));
			}
		}
		for(int i = 0; i < M_1.n_cols; i++)
		{
			res_cols[i] = new LinkedList<SparseMatrixEntry>(); 
			for(Iterator<SparseMatrixEntry> iter = M_1.cols[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
				res_cols[i].add(new SparseMatrixEntry(entry.value * x,entry.index));
			}
		}
		
		return new SparseMatrix(res_rows,res_cols);	
	}

	SparseMatrixEntry getRowEntry(int index_i,int index_j)
	{
		for(Iterator<SparseMatrixEntry> iter = this.rows[index_i].iterator(); iter.hasNext(); )
		{
			SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
			if(entry.index==index_j)
				return entry;
		}
		return null;
	}
	
	SparseMatrixEntry getColEntry(int index_i,int index_j)
	{
		for(Iterator<SparseMatrixEntry> iter = this.cols[index_j].iterator(); iter.hasNext(); )
		{
			SparseMatrixEntry entry=(SparseMatrixEntry)iter.next();
			if(entry.index==index_i)
				return entry;
		}
		return null;
	}
	
	void addEntry(int index_i,int index_j,double value)
	{
		//before add, should check whether exist first
		this.rows[index_i].add(new SparseMatrixEntry(value,index_j));
		this.cols[index_j].add(new SparseMatrixEntry(value,index_i));
		return;
	}
	
	void setEntry(int index_i,int index_j,double value)
	{
		this.rows[index_i].remove(this.getRowEntry(index_i, index_j));
		this.cols[index_j].remove(this.getColEntry(index_i, index_j));
		this.rows[index_i].add(new SparseMatrixEntry(value,index_j));
		this.cols[index_j].add(new SparseMatrixEntry(value,index_i));
		return;
	}
	
	void multiply_scalar(double x)
	{
		for(int i = 0; i < this.n_rows; i++)
			for(Iterator<SparseMatrixEntry> iter = this.rows[i].iterator(); iter.hasNext(); )
				iter.next().value *= x;

		for(int i = 0; i < this.n_cols; i++)
			for(Iterator<SparseMatrixEntry> iter = this.cols[i].iterator(); iter.hasNext(); )
				iter.next().value *= x;
	}
	
	//sort each row according the col_index,sort each col according the row_index
	void keep_sort()
	{
		for(int i = 0; i < this.n_rows; i++)
			Collections.sort(this.rows[i], new SparseMatrixComparatorIndex());

		for(int i = 0; i < this.n_cols; i++)
			Collections.sort(this.cols[i], new SparseMatrixComparatorIndex());
	}

	public void save_to_file(String path) throws Exception
	{
		FileWriter fw=new FileWriter(path);
		//write first line: #rows #cols
		fw.write(""+this.n_rows+" "+this.n_cols+"\n");
		//write rows: "col:value col:value ...col:value "
		for(int i=0;i<this.n_rows;i++)
		{
			for(Iterator it=this.rows[i].iterator();it.hasNext();)
			{
				SparseMatrixEntry entry=(SparseMatrixEntry)it.next();
				BigDecimal b=new   BigDecimal(entry.value);
				double value=b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				fw.write(entry.index+":"+value+" "); //\s
			}
			fw.write("\n");
		}
		fw.close();
		System.out.println("Save the SparseMatrix to "+path+" Successfully!");
	}
/////////////////////////// Useless  //////////////////////////////////////////////////////////	
	public void check_consistency() throws Exception
	{
		double[][] m1 = new double[this.n_rows][this.n_cols];
		double[][] m2 = new double[this.n_rows][this.n_cols];

		for(int i = 0; i < this.n_rows; i++)
			for(int j = 0; j < this.n_cols; j++)
				m1[i][j] = m2[i][j] = 0;

		for(int i = 0; i < this.n_rows; i++)
		{
			for(Iterator<SparseMatrixEntry> iter = this.rows[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry = iter.next();
				m1[i][entry.index] = entry.value;
			}
		}
		for(int i = 0; i < this.n_cols; i++)
		{
			for(Iterator<SparseMatrixEntry> iter = this.cols[i].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry = iter.next();
				m2[entry.index][i] = entry.value;
			}
		}

		for(int i = 0; i < this.n_rows; i++)
			for(int j = 0; j < this.n_cols; j++)
				if(m1[i][j] != m2[i][j])
					throw new Exception("Error!");
	}

	double get_energy()	//Note: energies greater than 1 are cut to 1.
	{
		double energy = 0;
		for(int i = 0; i < this.n_rows; i++)
			for(Iterator<SparseMatrixEntry> iter = this.rows[i].iterator(); iter.hasNext(); )
			{
				energy += iter.next().value;
				if(energy > 1)
					return energy;
			}
		return energy;
	}
	

	
	//first line: #rows #cols
	//row: col:value col:value ...
	public static SparseMatrix load_from_file(String path) throws Exception
	{
		BufferedReader reader = new BufferedReader( new FileReader(path) );

		String s = reader.readLine();	//Dimensions
		StringTokenizer tokenizer = new StringTokenizer(s);
		int n_rows = Integer.parseInt( tokenizer.nextToken() );
		int n_cols = Integer.parseInt( tokenizer.nextToken() );

		if(tokenizer.hasMoreTokens())
			throw new Exception("Invalid first line in matrix file.");

		LinkedList<SparseMatrixEntry>[] rows = new LinkedList[n_rows];
		LinkedList<SparseMatrixEntry>[] cols = new LinkedList[n_cols];
		for(int i = 0; i < n_rows; i++)
			rows[i] = new LinkedList<SparseMatrixEntry>();
		for(int i = 0; i < n_cols; i++)
			cols[i] = new LinkedList<SparseMatrixEntry>(); 

		SparseMatrix matrix = new SparseMatrix(rows, cols);

		for(int row = 0; row < n_rows; row++)
		{
			s = reader.readLine();	//row: col:value col:value ...
			tokenizer = new StringTokenizer(s);

			String temp = tokenizer.nextToken();
			if(temp.charAt(temp.length() - 1) != ':')
				throw new Exception("Invalid line in matrix file; row: " + (row + 1));
			if( Integer.parseInt( temp.substring(0, temp.length() - 1) ) != row )
				throw new Exception("Invalid line in matrix file; row: " + (row + 1));

			while(true)
			{
				if(!tokenizer.hasMoreTokens())
					throw new Exception("Missing $ at the end of line " + row);

				temp = tokenizer.nextToken();
				if(temp.equals("$"))
					break;

				int colon_index = temp.indexOf(':');
				int col = Integer.parseInt( temp.substring(0, colon_index) );
				double value = Double.parseDouble( temp.substring(colon_index + 1) );

				matrix.rows[row].add( new SparseMatrixEntry(value, col) );
				matrix.cols[col].add( new SparseMatrixEntry(value, row) );
			}
		}

		System.out.println("Loaded matrix from" + path);
		return matrix;
	}
	
/*
	public static void main(String[] args) throws Exception
	{
		LinkedList<MatrixEntry> [] row_ex_matrix = new LinkedList [3];
		LinkedList<MatrixEntry> [] column_ex_matrix = new LinkedList [3];
		row_ex_matrix[0] = new LinkedList<MatrixEntry>();
		row_ex_matrix[1] = new LinkedList<MatrixEntry>();
		row_ex_matrix[2] = new LinkedList<MatrixEntry>();
		column_ex_matrix[0] = new LinkedList<MatrixEntry>();
		column_ex_matrix[1] = new LinkedList<MatrixEntry>();
		column_ex_matrix[2] = new LinkedList<MatrixEntry>();
		row_ex_matrix[0].add(new MatrixEntry(1.0/2, 1));
		row_ex_matrix[0].add(new MatrixEntry(1.0/2, 2));
		row_ex_matrix[1].add(new MatrixEntry(1.0, 2));
		row_ex_matrix[2].add(new MatrixEntry(1.0, 0));
		column_ex_matrix[0].add(new MatrixEntry(1, 2));
		column_ex_matrix[1].add(new MatrixEntry(1.0/2, 0));
		column_ex_matrix[2].add(new MatrixEntry(1.0/2, 0));
		column_ex_matrix[2].add(new MatrixEntry(1, 1));
		SparseMatrix ex_M = new SparseMatrix (row_ex_matrix, column_ex_matrix);
		SparseMatrix ex_result = matrix_product (ex_M, ex_M);
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < ex_result.rows[i].size(); j++){
				System.out.println(ex_result.rows[i].get(j).entry);
			}
		}
	}
*/

	/* This function saves the page contribution matrix for future use.
	 * It prunes those entries [i][j] of the matrix where the contribution
	 * of page i to page j (or vice versa) divided by the PageRank of page i
	 * (or page j) is less than con_ratio_thresh; so a more sparse matrix
	 * has to be stored (instead of a full 100000x100000 matrix).
	 */
	public void save_page_cont_matrix( double[] org_pagerank,
					double cont_ratio_thresh, String outfile ) throws Exception
	{
		if(this.n_rows != org_pagerank.length)
			throw new Exception("!!!!");

		PrintStream out = new PrintStream(new FileOutputStream(outfile));
		out.println(this.n_rows + " " + this.n_cols);	//Write dimensions

		for(int row = 0; row < this.n_rows; row++)
		{
			out.print(row + ": ");
			if(org_pagerank[row] == 0)
				throw new Exception("PageRank of page " + row + " is 0!");
			for(Iterator<SparseMatrixEntry> iter = this.rows[row].iterator(); iter.hasNext(); )
			{
				SparseMatrixEntry entry = iter.next();
				if(entry.value / org_pagerank[row] > cont_ratio_thresh)
					out.print(" " + entry.index + ":" + entry.value + " ");
			}
			out.println(" $");
		}

		out.close();
		System.out.println("Saved matrix to " + outfile);
	}

	static void delete_file(String path) throws Exception
	{
		File f = new File(path);
		if(f.exists())
			if(f.delete() == false)
				throw new Exception("Could not delete " + path);
	}

	public static void save_2d_matrix_to_file(double[][] m, String path) throws Exception
	{
		int SIZE_OF_LONG_AS_BYTES = 8;
		int n_rows = m.length;
		int n_cols = m[0].length;

		delete_file(path);
		RandomAccessFile fp = new RandomAccessFile(path, "rw");
		fp.writeInt(n_rows);
		fp.writeInt(n_cols);

		for(int i = 0; i < n_rows; i++)
		{
			ByteBuffer buff = ByteBuffer.allocate(n_cols * SIZE_OF_LONG_AS_BYTES);
			for(int j = 0; j < n_cols; j++)
				buff.putDouble(m[i][j]);

			byte[] b = buff.array();
			fp.write(b);
		}

		fp.close();
		System.out.println("Wrote matrix to " + path);
	}

	public static double[][] load_2d_matrix_from_file(String path) throws Exception
	{
		int SIZE_OF_LONG_AS_BYTES = 8;

		RandomAccessFile fp = new RandomAccessFile(path, "r");
		int n_rows = fp.readInt();
		int n_cols = fp.readInt();

		double[][] m = new double[n_rows][n_cols];
		for(int i = 0; i < n_rows; i++)
		{
			byte[] b = new byte[n_cols * SIZE_OF_LONG_AS_BYTES];
			int n_read = fp.read(b);

			if(n_read != b.length)	//just some error checking
				throw new Exception("!!!!");

			ByteBuffer buff = ByteBuffer.wrap(b);
			for(int j = 0; j < n_cols; j++)
				m[i][j] = buff.getDouble();
		}

		fp.close();
		System.out.println("Successfully read matrix from " + path);

		return m;
	}


}

class SparseMatrixEntry
{
	double value;
	int index;
	public SparseMatrixEntry(double value, int index)
	{
		this.value = value;
		this.index = index;
	}
	
		
}

class SparseMatrixComparatorValue implements Comparator<SparseMatrixEntry>
{
	public int compare(SparseMatrixEntry entry1, SparseMatrixEntry entry2){
		if (entry1.value > entry2.value){
			return 1;
		}
		if (entry1.value < entry2.value){
			return -1;
		}	
		else
			return 0;
		
		
	}
}

class SparseMatrixComparatorIndex implements Comparator<SparseMatrixEntry>
{
	public int compare(SparseMatrixEntry entry1, SparseMatrixEntry entry2){
		if (entry1.index > entry2.index){
			return 1;
		}
		if (entry1.index < entry2.index){
			return -1;
		}	
		else
			return 0;
		
		
	}
}
