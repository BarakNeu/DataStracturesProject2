import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	public LinkedList roots_list = new LinkedList();
	public HeapNode min;
	public int size;
	public int treesNum;
	public int markedNum;
	public static int linksNum;
	public static int cutsNum;
	
	//constructor
	public FibonacciHeap (FibonacciHeap.HeapNode heapRoot) {
		this.min = heapRoot;
		this.size = 1;
		this.treesNum = 1;
		this.markedNum = 0;
		this.roots_list.insertAtStart(heapRoot);
	}

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() {
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
    	FibonacciHeap new_heap = new FibonacciHeap(node);
		this.meld(new_heap);
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
    	if (this.isEmpty()){
			return;
		}
    	HeapNode n = this.min.child_list.head;
    	HeapNode limiter = this.min.child_list.head;
    	//checking if the node we are removing is marked for total marked count
    	if (this.min.mark) this.markedNum--;
    	
    	if (n != null) { //if the min node has children
    		this.size--;
    		this.treesNum--;
    		//Handling the chaining between different trees in the heap
    		if(this.min.right != this.min) {
    			this.min.right.left = this.min.left;
    			this.min.left.right = this.min.right;
    		}
    		//if he has no neighbours then his children will be the new roots
    		else this.roots_list = this.min.child_list;
    		
    		//adding the children of the deleted node to the roots_list
    		do { HeapNode tmp = n.right;
    			n.parent = null;
    			if (n.mark) {
    				n.mark = false;
    				this.markedNum--;
    			}
    			this.roots_list.insertAtStart(n);
    			n = tmp;
    		}while(n != limiter);	
    	}
    	else { //if the min node doesn't have children
    		if (this.size == 1) {
    			this.min = null;
    			this.size = 0;
    			this.treesNum = 0;
    			this.markedNum = 0;
    			return;
    		}
    		else {
    			this.roots_list.Delete(min);
    			this.size--;
    			this.treesNum--;
    		}
    	}
    	//fixing the heap
    	consolidate();
    	findNewMin();
     	return; // should be replaced by student code
    }
    
    public void findNewMin() {
    	//updating the new min of the heap
    	this.min = roots_list.head;
    	HeapNode node = roots_list.head.right;
    	HeapNode limit = roots_list.head;
    	while(node != limit) if(node.key < this.min.key)this.min = node;
    }
    
    public void consolidate() {
    	HeapNode[] treeRankArray = new HeapNode[(int) (Math.log(size) / Math.log(1.86)) + 1];
    	HeapNode n = this.roots_list.head;
    	if (n == null) return;
    	do {
    		n.left = null;
    		n.right = null;
    		if (treeRankArray[n.rank] == null) treeRankArray[n.rank] = n;
    		else {
    			do {
    				HeapNode tmp = treeRankArray[n.rank];
    				treeRankArray[n.rank] = null; 
    				n = link(tmp, n);
    			}while (treeRankArray[n.rank] != null);
    			treeRankArray[n.rank] = n;
    		}
    		n = n.right;
    	}while (n != roots_list.head);
    	
    }
    
    public HeapNode link (HeapNode root1, HeapNode root2) {
    	//we will use this only on roots of the same rank
    	HeapNode a;
    	HeapNode b;
    	
    	if (root2.getKey() >= root1.getKey()) {
    		a = root2;
    		b = root1;
    	}
    	else {
    		a = root1;
    		b = root2;
    	}
    	if (a.child_list.head == null) {
    		a.child_list.head = b;
    		b.parent = a;
    		b.right = b;
    		b.left = b;
    	}
    	else {
    		b.parent = a;
    		HeapNode next = a.child_list.head.right;
    		a.child_list.head.right = b;
    		b.left = a.child_list.head;
    		next.left = b;
    		b.right = next;
    	}
    	a.parent = null;
    	a.left = null;
    	a.rank++;
    	linksNum++;
    	return a;
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
    	if (heap2.isEmpty()){
			return;
		}
    	//if our heap is empty we become heap2 and thus update the fields
    	if (this.isEmpty()) {
    		this.size = heap2.size();
    		this.markedNum = heap2.markedNum;
    		this.treesNum = heap2.treesNum;
    		this.min = heap2.min;
			this.roots_list = heap2.roots_list;
    		return;
    	}
    	//neither is empty
    	heap2.roots_list.insertAtStart(this.roots_list.head);
    	//updates the min to be the min of both heaps
    	if (this.min.getKey() > heap2.min.getKey()){
			this.min = heap2.min;
		}
    	//after the linking update the combined sizes accordingly
    	this.markedNum += heap2.markedNum;
    	this.size += heap2.size;
    	this.markedNum += heap2.markedNum;
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
    	int upper = (int) Math.ceil(Math.log(this.size) / Math.log(1.816)) + 1;
    	
    	int[] arr = new int[upper];
    	
    	//handling the empty case
    	if(this.isEmpty()) return new int[0];
    	
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
    public void delete(HeapNode x) {
		this.decreaseKey(x, Integer.MIN_VALUE);
		deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
		x.key -= delta;
		if (x.key > x.parent.key){
			return;
		}
		else {
			cascading_cuts(x, x.parent);
		}
		if (x.key < this.min.key){
			this.min = x;
		}
    	return; // should be replaced by student code
    }
	public void cut(HeapNode x, HeapNode y){
		x.parent = null;
		if (x.mark == true){
			markedNum -= 1;
		}
		x.mark = false;
		y.rank -= 1;
		y.child_list.Delete(x);
		this.roots_list.insertAtStart(x);
		treesNum++;
		cutsNum++;
	}
	public void cascading_cuts(HeapNode x, HeapNode y ){
		cut(x,y);
		if (y.parent != null){
			if (y.mark == false){
				y.mark = true;
				markedNum ++;
			}
			else {
				cascading_cuts(y, y.parent);
			}
		}
	}
   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {
        return this.size - this.markedNum; // should be replaced by student code
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
        return this.treesNum + 2*this.markedNum; // should be replaced by student code
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
    	return linksNum; // should be replaced by student code
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
    	return cutsNum; // should be replaced by student code
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
		FibonacciHeap helping_heap = new FibonacciHeap(H.min);
        int[] minimal_k = new int[k];
		for (int i = 0; i < k-1; k++){
			HeapNode node = H.findMin();
			minimal_k[i] = node.key;
			HeapNode original_child = node.child_list.head;
			HeapNode curr_node = original_child;
			helping_heap.deleteMin();
			while (curr_node != original_child){
				helping_heap.insert(curr_node.key);
				curr_node = curr_node.right;
			}
		}
        return minimal_k; // should be replaced by student code
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
    	public LinkedList child_list = new LinkedList();
    	public HeapNode right;
    	public HeapNode left;
    	public int rank = 0;
    	public boolean mark = false;

    	public int key;

    	public HeapNode(int key) {
    		//default constractor for the nodes in the heap
    		this.key = key;
			this.right = this;
			this.left = this;
    	}
    	public int getKey() {
    		return this.key;
    	}
    }

	public static class LinkedList{
		public HeapNode head;
		public int size;
		public LinkedList() {
			this.head = null;
		}
		public void insertAtStart(HeapNode to_insert){
			if (this.size == 0){
				this.head = to_insert;
			}
			else {
				to_insert.left.right = this.head;
				this.head.left = to_insert.left;
				to_insert.left = this.head.left;
				this.head.left.right = to_insert;
				this.head = to_insert;
			}
			size++;
		}
		public void Delete(HeapNode to_delete){ // no need to handle edge cases, im the only one using this
			if (this.size == 1){
				this.head = null;
				size--;
				return;
			}
			if (this.head == to_delete){
				this.head = this.head.right;
			}
			to_delete.left.right = to_delete.right;
			to_delete.right.left = to_delete.left;
			to_delete.right = null;
			to_delete.left = null;
			size--;
		}
	}
}
