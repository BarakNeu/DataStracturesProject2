

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {

	public HeapNode first;
	public HeapNode min;
	public int size;
	public int rootsNum;
	public int markedNum;
	public static int linksNum = 0;
	public static int cutsNum = 0;

	public FibonacciHeap() {
		this.min = null;
		this.size = 0;
		this.rootsNum = 0;
		this.markedNum = 0;
		this.first = null;
	}

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *
    */
    public boolean isEmpty()
    {
    	return this.min == null;
    }

   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.
    *
    * Returns the newly created node.
    */
    public HeapNode insert(int key) {
		return insert(new HeapNode(key));
    } //O(1)

    public HeapNode insert(HeapNode toInsert) { //O(1)
    	if (this.first == null) { //empty heap
    		this.first = toInsert;
    		this.min = toInsert;
    		toInsert.left = toInsert;
    		toInsert.right = toInsert;
    	}
    	else {
    		HeapNode tmpL = this.first.left;
    		toInsert.right = this.first;
    		this.first.left = toInsert;
    		tmpL.right = toInsert;
    		toInsert.left = tmpL;
    		this.first = toInsert;
    	}
    	if (toInsert.key < this.min.key) { //updating min
    		this.min = toInsert;
    	}
    	this.size++;
    	this.rootsNum++;
    	return toInsert;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin() //O(logn)
    {
    	if (this.isEmpty()) {
    		return;
    	}
    	// min node has children
    	if (this.min.child != null) {
    	    this.size--;
    		this.rootsNum--;
    		HeapNode minChild = this.min.child;
    		HeapNode n = this.min.child;
    		do {
    			if (n.mark) {
    				n.mark = false;
    				this.markedNum--;
    			}
				n.parent = null;
    			n = n.right;
    		} while (n != minChild);
            // in the case of a singular root, the root must be the min by rules of min heap
            // we don't need to do horizontal edge changes if there is only one root, since the children should
            // already be set up (need to add rank to rootsNum and remove parent/child relationship)
            if (rootsNum > 0) {
        		HeapNode tmpL = minChild.left;
        		minChild.left = this.min.left;
        		this.min.left.right = minChild;
        		tmpL.right = this.min.right;
        		this.min.right.left = tmpL;
            }
    		this.rootsNum += this.min.rank;
    		// if min is deleted and is also first node, switch first to a child node
    		if (this.min == this.first) {
    			this.first = this.min.child;
    		}
		    this.min.child = null;
		    minChild.parent = null;
    	} else {
    	    // min node is only node
    		if (this.size == 1) {
    			this.first = null;
    			this.min = null;
    			this.rootsNum = 0;
    			this.markedNum = 0;
    			this.size = 0;
    			return;
    		}
    		this.size--;
    		this.rootsNum--;
            // min is a singular node
    		this.min.right.left = this.min.left;
    		this.min.left.right = this.min.right;
    		// switch first node to the right
    		if (this.min == this.first) {
    			this.first = this.first.right;
    		}
    	}
    	consolidate();
    }

    public void consolidate() { //O(logn)
    	HeapNode[] treeRankArray = new HeapNode[2 * (int) (Math.ceil(Math.log(size) / Math.log(2)) + 1)];
    	HeapNode curr = this.first;
    	if (curr == null) {
    		return;
    	}
        // heap is not empty
    	int stopper = rootsNum;
    	for (int i = 0; i < stopper; i++) {
    		HeapNode n = curr.right;
			while (treeRankArray[curr.rank] != null) {
			    HeapNode tmp = treeRankArray[curr.rank];
				// set box as null
				treeRankArray[curr.rank] = null;
				// merge trees
				curr = link(tmp, curr);
				// number of roots has gone down by one
				rootsNum--;
			};
    		treeRankArray[curr.rank] = curr;
    		curr = n;
    	}
    	this.min = null;
    	this.first = null;
		this.rootsNum = 0;
		for (int i = treeRankArray.length-1; i >= 0; i--) {
			HeapNode heapNode = treeRankArray[i];
			// Placing the trees from the Array in the heap
			if (heapNode != null) {
				insert(heapNode);
				this.size--;
			}
		}
    }

    public HeapNode link(HeapNode root1, HeapNode root2) { //O(1)
    	HeapNode a;
    	HeapNode b;
    	// Setting a as the lower value root.
    	if (root1.getKey() > root2.getKey()) {
    		a = root2;
    		b = root1;
    	}
    	else {
    		a = root1;
    		b = root2;
    	}
    	b.left.right = b.right;
    	b.right.left = b.left;
    	if (a.child == null) {
    		b.left = b;
    		b.right = b;
    	}
    	else {
    		HeapNode tmpL = a.child.left;
    		a.child.left = b;
    		b.right = a.child;
    		tmpL.right = b;
    		b.left = tmpL;
    	}
    	a.child = b;
		b.parent = a;
    	// a has gained a child
		if (a.mark){
			markedNum--;
		}
		a.mark = false;
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
    public HeapNode findMin() {
		return this.min;
    } //O(1)
    
    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
     public void meld (FibonacciHeap heap2) { //O(1)
		 if (heap2.isEmpty()){ // If heap2 is empty we do nothing.
			 return;
		 }
		 if (this.isEmpty()){  // If our heap is empty we "become" heap2.
			 this.size = heap2.size();
			 this.markedNum = heap2.markedNum;
			 this.rootsNum = heap2.rootsNum;
			 this.min = heap2.min;
			 this.first = heap2.first;
			 return;
		 }
		 // Neither are empty.
		 HeapNode myRootsTail = this.first.left;
		 HeapNode myRootsHead = this.first;
		 HeapNode newHeapHead = heap2.first;
		 HeapNode newHeapTail = heap2.first.left;
		 //Connecting the tails and heads of the two heaps to create the new roots cycled chain.
		 myRootsTail.right = newHeapHead;
		 newHeapHead.left = myRootsTail;
		 newHeapTail.right = myRootsHead;
		 myRootsHead.left = newHeapTail;
		 this.size += heap2.size;
		 this.rootsNum += heap2.rootsNum;
		 this.markedNum += heap2.markedNum;
		 if (heap2.min.key < this.min.key){
			 this.min = heap2.min;
		 }
     }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *   
     */
     public int size() {
     	return this.size;
     } //O(1)
     	
     /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * (Note: The size of of the array depends on the maximum order of a tree.)  
     * 
     */
     public int[] countersRep() { // O(logn)
		 if (this.isEmpty()){
			 return new int[0];
		 }
		 int upper  = (int) Math.ceil(Math.log(this.size) / Math.log(2)) + 1;
		 int[] ans = new int[upper];
		 if (this.isEmpty()){
			 return new int[0];
		 }
		 HeapNode currNode = first;
		 for (int i = 0; i < rootsNum; i++){
			 ans[currNode.rank]++;
			 currNode = currNode.right;
		 }
		 int len = 0;
		 for (int i = ans.length - 1; i >= 0; i--){
			 if (ans[i] != 0){
				 len = i+1;
				 break;
			 }
		 }
		 int[] counters = new int[len];
		 for (int i = 0; i<counters.length; i++){
			 counters[i] = ans[i];
		 }
    	return counters;
     }
 	
    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
 	* It is assumed that x indeed belongs to the heap.
     *
     */
     public void delete(HeapNode x) { //O(logn)
     	 this.decreaseKey(x, x.key-Integer.MIN_VALUE);
		 deleteMin();
     }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
     public void decreaseKey(HeapNode x, int delta) { // O(logn)
		 x.key -= delta;
		 if (x.key < this.min.key){ //updating the min if necessary
			 this.min = x;
		 }
		 if (x.parent == null){ // heap rule not broken
		 }
		 else {
			 if (x.key > x.parent.key){
			 }
			 else {
				 cascading_cuts(x, x.parent);
			 }
		 }
		// updating the minimum happens inside cut
     }
	 public void cut(HeapNode x, HeapNode y){ //cutting x from its parent y, O(1)
		 x.parent = null;
		 if (x.mark){
			 markedNum--;
		 }
		 x.mark = false;
		 y.rank--;
		 if (x.right == x){ //if x was the last son of y
			 y.child = null;
		 }
		 else {
			 y.child = x.right;
			 skipNode(x);
		 }
		 this.insert(x);
		 this.size--; //here I'm using which increases size, and I don't wanna do that
		 cutsNum++;
	 }
	 public void cascading_cuts(HeapNode x, HeapNode y){ //O(1)
		 cut(x,y);
		 if (y.parent != null){ // if y is not a root
			 if (y.mark == false){ // if it's not marked, make is so
				 y.mark = true;
				 markedNum++;
			 }
			 else {
				 cascading_cuts(y, y.parent);
			 }
		 }
	 }
	 public void skipNode(HeapNode toDelete){ // used to skip over a node O(1)
		 toDelete.left.right = toDelete.right;
		 toDelete.right.left = toDelete.left;
		 toDelete.right = toDelete;
		 toDelete.left = toDelete;
	 }


    /**
     * public int nonMarked() 
     *
     * This function returns the current number of non-marked items in the heap
     */
     public int nonMarked() //O(1)
     {    
         return this.size - markedNum;
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
     public int potential() {
         return rootsNum + 2 * markedNum;
     } //O(1)

    /**
     * public static int totalLinks() 
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
     public static int totalLinks() { //O(1)
     	return linksNum ;
     }

    /**
     * public static int totalCuts() 
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods). 
     */
     public static int totalCuts() { //O(1)
     	return cutsNum ;
     }

      /**
     * public static int[] kMin(FibonacciHeap H, int k) 
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *  
     * ###CRITICAL### : you are NOT allowed to change H.
     */
     public static int[] kMin(FibonacciHeap H, int k) { //O(klog(deg(H))
		 if (H.isEmpty()){ // empty case
			 return new int[0];
		 }
		 FibonacciHeap helping_heap = new FibonacciHeap();
		 HeapNode to_insert = new HeapNode(H.min.key);
		 to_insert.realNode = H.min;
		 helping_heap.insert(to_insert); // inserting the root
		 int[] minimal_k = new int[k];
		 for (int i = 0; i < k; i++){ // k-1 items to insert
			 HeapNode h_min = helping_heap.findMin();
			 minimal_k[i] = h_min.key;
			 helping_heap.deleteMin();
			 if (h_min.realNode.child != null) { // checking if there are children to inserst
				 HeapNode originalChild = h_min.realNode.child;
				 HeapNode to_insert1 = new HeapNode(originalChild.key);
				 to_insert1.realNode = originalChild;
				 helping_heap.insert(to_insert1); // inserting the first child
				 HeapNode currNode = originalChild.right;
				 while (currNode != originalChild) { // inserting the rest
					 HeapNode to_insert2 = new HeapNode(currNode.key);
					 to_insert2.realNode = currNode;
					 helping_heap.insert(to_insert2);
					 currNode = currNode.right;
				 }
			 }
		 }
      return minimal_k;
     }
	 public HeapNode getFirst(){ //returns the first root, O(1)
		 return this.first;
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
		public HeapNode realNode;
    	public HeapNode(int key) { //HeapNode constructor
    		this.key = key;
    		this.left = this;
    		this.right = this;
    		this.parent = null;
    		this.child = null;
    	}
		// getters, used for testing, O(1)
    	public int getKey() {
    		return this.key;
    	}
		public HeapNode getParent(){return this.parent;}
	   public HeapNode getChild(){return this.child;}
	   public int getRank(){return this.rank;}
	   public HeapNode getRight(){return this.right;}
	   public HeapNode getLeft() {return this.left;}
	   public boolean isMarked(){return this.mark;}
   }
}