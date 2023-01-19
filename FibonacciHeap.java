

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{

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
    }

    public HeapNode insert(HeapNode toInsert) {
    	if (this.first == null) {
    		this.first = toInsert;
    		this.min = toInsert;

    		// todo: maybe necessary?
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
    	if (toInsert.key < this.min.key) {
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
    public void deleteMin()
    {
    	if (this.isEmpty()) {
    	   // System.out.println("this.isEmpty() " + String.valueOf(this.isEmpty()));
    		return;
    	}
        // System.out.println("delete min: " + String.valueOf(this.min.key));

    	// min node has children
    	if (this.min.child != null) {
    	    this.size--;
    		this.rootsNum--;
    	   // System.out.println("has children: " + String.valueOf(this.size));
    	   // System.out.println("min left key: " + String.valueOf(min.left.key));
            // System.out.println("min right key: " + String.valueOf(min.right.key));
    		HeapNode minChild = this.min.child;
    		HeapNode n = this.min.child;

    		// todo: need to change this in buck's version
    		do {
    			if (n.mark) {
    				n.mark = false;
    				this.markedNum--;
    			}
    			n = n.right;
    		} while (n != minChild);

            // todo: change this in the case where there is only one root
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

    		// todo: add removal of parent on min child
		    this.min.child = null;
		    minChild.parent = null;

    	} else {
    	    // min node is only node
    		if (this.size == 1) {
    		  //  System.out.println("only node " + String.valueOf(this.size));
    			this.first = null;
    			this.min = null;
    			this.rootsNum = 0;
    			this.markedNum = 0;
    			this.size = 0;
    			return;
    		}

    		this.size--;
    		// todo: add this otherwise stopper is incorrect value for certain cases
    		this.rootsNum--;

    // 		System.out.println("no children: " + String.valueOf(this.size));

            // min is a singular node
    		this.min.right.left = this.min.left;
    		this.min.left.right = this.min.right;

    		// switch first node to the right
    		if (this.min == this.first) {
    			this.first = this.first.right;
    		}
    	}

    	consolidate();

    	// debugging
        // System.out.println("new minimum: " + String.valueOf(this.min.key));
    // 	System.out.println("fake tree: " + String.valueOf(this.first.key) + String.valueOf(this.first.child.key) + String.valueOf(this.first.child.right.key));
     	return;

    }

    public void consolidate() {
    	HeapNode[] treeRankArray = new HeapNode[(int) (Math.log(size) / Math.log(1.86)) + 1];
    	HeapNode curr = this.first;
    	if (curr == null) {
    		return;
    	}

    // 	System.out.println("first node of consolidate: " + String.valueOf(curr.key));

        // heap is not empty
    	int stopper = rootsNum;
    // 	System.out.println("rootsNum: " + String.valueOf(rootsNum));
    	for (int i = 0; i < stopper; i++) {
    		HeapNode n = curr.right;
    // 		System.out.println("node value: " + String.valueOf(curr.key));
    // 		System.out.println("next node value: " + String.valueOf(n.key));
    // 		System.out.println("node rank: " + String.valueOf(curr.rank));

    		// todo: do while --> while
			while (treeRankArray[curr.rank] != null) {
			    HeapNode tmp = treeRankArray[curr.rank];
			 //   System.out.println("existing tree: " + String.valueOf(tmp.key));
	    	  //  System.out.println("existing tree rank: " + String.valueOf(tmp.rank));
				// set box as null
				treeRankArray[curr.rank] = null;
				// merge trees
				curr = link(tmp, curr);
				// number of roots has gone down by one
				rootsNum--;
			};
// 			System.out.println("new tree: " + String.valueOf(curr.key));
    		treeRankArray[curr.rank] = curr;
    		curr = n;
    	}
    	// debugging
    // 	for (int i = 0; i < treeRankArray.length; i++ ) {
    // 	    System.out.println("treeRank: " + String.valueOf(i));
    // 	    if (treeRankArray[i] == null) {
    // 	        System.out.println("values: empty");
    // 	    } else {
    // 	    System.out.println("values: " + String.valueOf(treeRankArray[i].key));
    // 	    }
    // 	}
    	this.min = null;
    	this.first = null;
		this.rootsNum = 0;
		//for (HeapNode heapNode : treeRankArray) {
		for (int i = treeRankArray.length-1; i >= 0; i--) {
			HeapNode heapNode = treeRankArray[i];
			// Placing the trees from the Array in the heap
			if (heapNode != null) {
				insert(heapNode);
				// todo: don't want to add a size because of consolidate?
				this.size--;
			}
		}
    }

    public HeapNode link(HeapNode root1, HeapNode root2) {
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
    		// todo: changed
    		b.right = a.child;
    		tmpL.right = b;
    		b.left = tmpL;
    	}

    	a.child = b;
		b.parent = a;
    	// a has gained a child
    	a.rank++;
    	linksNum++;
    	a.mark = false;
    // 	System.out.println("new node key: " + String.valueOf(a.key));
    // 	System.out.println("new node rank: " + String.valueOf(a.rank));
    // 	System.out.println("new node right: " + String.valueOf(a.right.key));
    // 	System.out.println("new node left: " + String.valueOf(a.left.key));
    // 	System.out.println("new node child: " + String.valueOf(a.child.key));
    // 	System.out.println("new node child right: " + String.valueOf(a.child.right.key));
    // 	System.out.println("new node child left: " + String.valueOf(a.child.left.key));
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
    }
    
    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
     public void meld (FibonacciHeap heap2) {
		 if (heap2.isEmpty()){ // If heap2 is empty we do nothing.
			 return;
		 }
		 if (this.isEmpty()){  // If our heap is empty we "become" heap2.
			 this.size = heap2.size();
			 this.markedNum = heap2.markedNum;
			 this.rootsNum = heap2.rootsNum;
			 this.min = heap2.min;
			 this.first = heap2.first;
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
     }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *   
     */
     public int size() {
     	return this.size;
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
     	int[] arr = new int[100];
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
     	this.decreaseKey(x, Integer.MAX_VALUE - x.key);
		 deleteMin();
     }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
     public void decreaseKey(HeapNode x, int delta) {
		 x.key -= delta;
		 if (x.key < this.min.key){
			 this.min = x;
		 }
		 if (x.parent == null || x.key > x.parent.key){
			 return;
		 }
		 else {
			 cascading_cuts(x, x.parent);
		 }
		// updating the minimum happens inside cut
     }
	 public void cut(HeapNode x, HeapNode y){ //cutting x from its parent y
		 x.parent = null;
		 if (x.mark){
			 markedNum--;
		 }
		 x.mark = false;
		 y.rank--;
		 if (y.rank == 0){ //if x was the last son of y
			 y.child = null;
		 }
		 else {
			 y.child = x.right;
		 }
		 skipNode(x);
		 this.insert(x);
		 this.size--; //here I'm using which increases size, and I don't wanna do that
		 cutsNum++;
	 }
	 public void cascading_cuts(HeapNode x, HeapNode y){
		 cut(x,y);
		 if (y.parent != null){
			 if (y.mark == false){
				 y.mark = true;
				 markedNum++;
			 }
			 else {
				 cascading_cuts(y, y.parent);
			 }
		 }
	 }
	 public void skipNode(HeapNode toDelete){ // used to skip over a node
		 toDelete.left.right = toDelete.right;
		 toDelete.right.left = toDelete.left;
		 toDelete.right = null;
		 toDelete.left = null;
	 }


    /**
     * public int nonMarked() 
     *
     * This function returns the current number of non-marked items in the heap
     */
     public int nonMarked() 
     {    
         return this.size - markedNum; // should be replaced by student code
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
         return rootsNum + 2 * markedNum;
     }

    /**
     * public static int totalLinks() 
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
     public static int totalLinks() {
     	return linksNum; // should be replaced by student code
     }

    /**
     * public static int totalCuts() 
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods). 
     */
     public static int totalCuts() {
     	return cutsNum;
     }

      /**
     * public static int[] kMin(FibonacciHeap H, int k) 
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *  
     * ###CRITICAL### : you are NOT allowed to change H. 
     */
     public static int[] kMin(FibonacciHeap H, int k) {
		 FibonacciHeap helping_heap = new FibonacciHeap();
		 helping_heap.insert(H.min);
		 int[] minimal_k = new int[k];
		 for (int i = 0; i < k-1; k++){
			 HeapNode h_min = H.findMin();
			 minimal_k[i] = h_min.key;
			 HeapNode original_child = h_min.child;
			 HeapNode curr_node = original_child;
			 helping_heap.deleteMin();
			 while (curr_node != original_child){
				 helping_heap.insert(curr_node.key);
				 curr_node = curr_node.right;
			 }
		 }
      return minimal_k;
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
    		this.key = key;
    		this.left = this;
    		this.right = this;
    		this.parent = null;
    		this.child = null;
    	}

    	public int getKey() {
    		return this.key;
    	}
    }
}
