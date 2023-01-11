/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	public HeapNode min;
	public int treesNum;
	public int markedNum;
	public static int linksNum;
	public static int cutsNum;
	
	//contructor
	public FibonacciHeap (FibonacciHeap.HeapNode heapRoot, int size) {
		this.min = heapRoot;
		this.treesNum = 1;
	}

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return this.min == null; // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {    
    	this.size++;
    	this.treesNum++;
    	HeapNode node = new HeapNode(key);
    	
    	//handling the case of an empty heap
    	if (this.isEmpty()) {
    		this.min = node;
    		return node;
    	}
    	//if the heap isn't empty we will inset the node as the neighbour
    	//of the min node as we are guranteed it is a root.
    	node.right = min.right;
    	node.left = min;
    	min.right = node;
    	min.right.left = node;
    	
    	//testing ig the inserted is a new min
    	if (key < this.min.key) {
    		this.min = node;
    	}
    	return node; // should be replaced by student code
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	//handling the case of an empty heap
    	if (this.isEmpty()) return;
    	
    	HeapNode minChild = this.min.child;
    	
    	//checking if the node we are removing is marked for total marked count
    	if (this.min.mark) this.markedNum--;
    	
    	if (minChild != null) {
    		this.size--;
    		this.treesNum--;
    		//handlnig the chainning between diffrent trees in the heap
    		if(this.min.right != this.min) {
    			this.min.right.left = this.min.left;
    			this.min.left.right = this.min.right;
    			this.min = this.min.right;
    		}
    		//if he has no neighbours then his child has to be the new min
    		else this.min = this.min.child;
    		
    		
    		
    		
    	}
     	return; // should be replaced by student code
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	//if heaps2 is empty we do nothing
    	if (heap2.isEmpty()) return;
    	
    	//if our heap is empty we become heap2 and thus update the fields
    	if (this.isEmpty()) {
    		this.size = heap2.size();
    		this.markedNum = heap2.markedNum;
    		this.treesNum = heap2.treesNum;
    		this.min = heap2.min;
    		return;
    	}
    	//the "classic case in which niether is empty
    	heap2.min.left.right = min.right;
    	this.min.right.left = heap2.min.left;
    	this.min.right = heap2.min;
    	heap2.min.left = this.min;
    	
    	//updates the min to be the min of both heaps
    	if (this.min.getKey() > heap2.min.getKey()) this.min = heap2.min;
    	
    	//after the linking update the combined sizes acoordingly
    	this.markedNum += heap2.markedNum;
    	this.size += heap2.size;
    	this.markedNum += heap2.markedNum;
    	return; // should be replaced by student code   		
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    public int[] countersRep()
    {
    	//upper number of diffrent ranks in a heap according to the lectures
    	int upper = (int) Math.ceil(Math.log(this.size) / Math.log(1.816)) + 1);
    	
    	int[] arr = new int[upper];
    	
    	//handling the empty case
    	if(this.isEMpty()) return new int[0];
    	
    	HeapNode next = this.min.right;
    	arr[this.min.rank] = 1; 
    	
    	while (this.min != next) {
    		arr[next.rank] += 1;
    		next = next.right;
    	}
        return arr; //	 to be replaced by student code
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {    
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	return; // should be replaced by student code
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return -232; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return -234; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return -345; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return -456; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
    	
    	public HeapNode parent;
    	public HeapNode child;
    	public HeapNode right;
    	public HeapNode left;
    	public int rank = 0;
    	public boolean mark = false;

    	public int key;

    	public HeapNode(int key) {
    		//default constractor for the nodes in the heap
    		this.key = key;
    	}

    	public int getKey() {
    		return this.key;
    	}
    }
}
