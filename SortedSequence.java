// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.util.Comparator;
import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;
//Estelle Brady
//CS 351- section 401
//collaborated with tutor Matthew in library, Julian Morano, Jonny Leston

/******************************************************************************
 * This class is a homework assignment;
 * An SortedSequence is a sequence of values in sorted order.
 * It can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class SortedSequence<E> implements Cloneable {

	private Node <E> dummy;
	private Node <E> cursor;
	private Comparator<E> comparator;
	private int manyItems;


	// TODO: Declare the private static Node<T> class.
	// This will serve for our doubly-linked list. 
	// It should have two constructors but no methods.
	// 1. You should define a constructor with no parameters that
	//    sets up the node as a dummy node: setting prev/next/data
	//    as required.  You will need to use an "unchecked" cast, and should
	//    suppress the warning that it requires.
	// 2. You should define a constructor taking a valid data object
	//    which initializes the data and (by default) nulls the pointer fields.
	// 3. You may declare another constructor with two or more parameters.
	//    (Our solution does not use any other constructors.)
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.


	// TODO: Declare the private fields needed for our data structure:
	// a `manyItems` field, a dummy, a cursor, and a comparator.
	// NB: You must use generics correctly: no raw types!

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	/** nested constructor class using generics 
	 * nested with a specifying constructor with appointment argument
	 */
	private static class Node<T> {
		T data;
		Node<T> prev;
		Node<T> next;
		@SuppressWarnings("unchecked")
		Node(){
			data = (T)this;
			next = this;
			prev = this;
		}
		Node (T data){
			this.data = data;
		}
	}
	
	/**
	 * this method is an extra test within certain methods to check certain exceptions
	 *checks whether it is null, if there are at least as many items as in manyItems
	 *is any elements are null
	 *if they are in natural order
	 *if we never have data equally dummy when data is present
	 * @return report or true
	 */
	private boolean wellFormed() {
		// Check the invariant.

// 1. The comparator may not be null
		
		if(comparator == null) 
			return report ("comparator cannot be null");
		
// 2. The dummy may not be null 

		//this is because it must be set to a node object
		if(dummy == null) 
			return report("dummy cannot be null");
		

// 3. The dummy's data must refer to itself
//(This is an impossible value which will cause
//errors in comparisons to fail fast.)

		//we are solely checking if the data is referred to the data in the dummy node
		if(dummy.data != this.dummy) 
			return report("the dummy's data doesn't refer to itself");
		
// 4. The list must be cyclic linked from dummy back to dummy
// with each next link having the opposite prev link.
// In particular, no prev or next links may be null.
// If you do this check correctly, you will not need to
// use the Tortoise & Hare Algorithm

		//always set lag to dummy
		Node<E> lag2 = dummy;
		for(Node<E>p=dummy.next; p!=null && p!=dummy;lag2 = p, p=p.next) {
			//want to make sure if it is null first to then later
			if(dummy.prev == null)
				return report("this is not cyclic");
		}
		//if the cursor does not equal the tail at the end
		if(dummy.prev != lag2)	return report("this is not cyclic");


//5. The cursor may not be null, and must be a node in the list.
//first, make sure cursor is not null
		
		//cursor cannot be null if it is a node in the list
		if(cursor == null)	return report("cursor shouldn't be null");
		
		//if there is an element in the list do the loop
		//if(dummy.next != dummy) {
			Node<E>i;
			Node<E>lag = dummy;
			boolean valid = false;
			//must assure that p is still not null
			for(i = dummy.next; i!=null && i!=dummy; lag = i,i=i.next) {
				//if we don't find the node
				if(i.prev!=lag)	return report("the cursor does not exist in here");
				if(cursor ==i)	valid = true;	
			}
			if(i == null)	return report("links cannot be null in list");
			if (valid == false && cursor !=dummy)	return report("no cursor");

	

// 6. manyItems must refer to the actual number of values 
//in the sort sequence (obviously, not including the dummy data).
		
		int count = 0;
		for(Node<E>p = dummy.next; p!=dummy && p!=null; p=p.next) {
			count++;
			//increments count until we get to manyItems total
		}if(manyItems != count)
			return report("manyItems must refer to the actual number of values");

// 7. No node in the linked list may have a null “data” field.
	
		Node<E>lag3 = dummy;
		for(Node<E>p = dummy.next; p!=dummy;lag3 = p, p=p.next) {
			//check if any of the data is null - doesn't matter where you are it shouldn't be null
			if(lag3.data == null || lag3.next.data == null)
				return report("the data within the list cannot be null");
		}

// 8. The data fields of the linked list must be in increasing order according to the comparator.
		//must make sure that none of the data is null and end when p.next reaches because we are comparing
		for(Node<E>p = dummy.next; p.next!=dummy.prev.next && p.data !=null&& p.next.data != null ; p=p.next) {
			if(comparator.compare(p.data, p.next.data) > 0)
				return report("this is out of order");
		}
		// If no problems found, then return true:
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private SortedSequence(boolean testInvariant) { }

	/**
	 * Initialize an empty sorted sequence 
	 * using natural ordering (compareTo)
	 **/   
	public SortedSequence( )
	{
		this(null);
	}

	/**
	 * Initialize an empty sorted sequence
	 * @param comp comparator to use.  if null, then use
	 * natural ordering (compareTo).
	 */
	@SuppressWarnings("unchecked")// for the comparator creation
	public SortedSequence(Comparator<E> comp) {
		if (comp == null) {
			Comparator<E> comp2 = (s1,s2) -> ((Comparable<E>) s1).compareTo(s2);
			comparator = comp2;
		}else
			comparator = comp;
			dummy = new Node<E>();
			manyItems = 0;
			cursor = dummy;
			// TODO: create a new Comparator
			// in which you cast the first argument to Comparable<E>
			// so that you use compareTo.
			// (Lambda syntax will make the code shorter, but is not required.)
		assert wellFormed() : "invariant failed at end of constructor";	
		}
	
	/**
	 * @return manyItems
	 */
	public int size() {
		assert wellFormed() : "invariant failed at start of size";	
		return manyItems;
	}
	/**
	 * Accessor method to determine whether this book has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent() {
		//if it equals dummy and not dummy.next, it has not started
		assert wellFormed() : "invariant failed at start of isCurrent";	
		return cursor != dummy;
	}

	/**
	 * Set the current element at the front of this book.
	 * @postcondition
	 *   The front element of this book is now the current element (but 
	 *   if this book has no elements at all, then there is no current 
	 *   element).
	 **/ 
	public void start() {
		//starts when we are at head
		assert wellFormed() : "invariant failed at start of start";	
		cursor = dummy.next;
	}
	
	/**
	 * Move forward, so that the current element will be the next element in
	 * this book.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this book 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   advance may not be called.
	 * @exception NullPointerException
	 * 	indicated that the cursor is null, so advance cannot
	 * be called.
	 **/
	public void advance() {
		//moves cursor up
		assert wellFormed() : "invariant failed at start of advance";	
		//if(cursor==null)	throw new NullPointerException("cursor cannot be null");
		if(!isCurrent())	throw new IllegalStateException("advance error");
		cursor = cursor.next;
		assert wellFormed() : "invariant failed at end of advance";	
	}

	/**taken from last homework and then modified
	 * Remove the current element from this sequence
	 * @precondition
	 *   isCurrent() returns true.
	 * @postcondition
	 *   The current element has been removed from this book, and the 
	 *   following element (if there is one) is now the new current element. 
	 *   If there was no following element, then there is now no current 
	 *   element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   removeCurrent may not be called. 
	 **/
	public void removeCurrent() {
		assert wellFormed() : "invariant failed at start of removeCurrent";	
		if(isCurrent() == false)
			throw new IllegalStateException("there is no element to remove");

		if(cursor.prev == dummy) {
			dummy.next = cursor.next;
		}else {
			cursor.prev.next = cursor.next;
		}
		
		if(cursor.next == dummy) {
			dummy.prev = cursor.prev;
		}else {
			cursor.next.prev = cursor.prev;
		}
		--manyItems;
		cursor = cursor.next;
		assert wellFormed() : "invariant failed at end of removeCurrent";	
	}
	//taken from previous homework and then modified
	/** Set the current element to the first element that is equal
	 * or greater than the guide.
	 * @param guide element to compare against, must not be null.
	 */
	public void setCurrent(E place) {
		assert wellFormed() : "invariant failed at start of setCurrent";
		if (place == null) throw new NullPointerException("guide cannot be null");
		start();
		while (isCurrent() && comparator.compare(getCurrent(),place) < 0) {
			advance();
		}
		assert wellFormed() : "invariant failed at end of setCurrent";
	}
	
	//insert taken from homework 5 solution and then modified
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * If the new element would take this book beyond its current capacity,
	 * then the capacity is increased before adding the new element.
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException
	 *   indicates the parameter is null
	 **/
	public void insert(E element) {
		assert wellFormed(): "invariant failed at the start of insert";
		
		//first must check if element is null
		if(element == null)
			throw new IllegalArgumentException("element is null in insert");

		//create a lag
		Node<E>lag = dummy;
		Node<E> p;
		Node<E> nElement = new Node<E>(element);
		//we go backwards for efficiency
		for(p = dummy.prev;p!=dummy; p=p.prev) {
			//breaks if the element is greater than or equal to the data
			if(comparator.compare(p.data, element)<=0){
				break;
			}
			lag = p;
		}
		
		nElement.prev = p;
		nElement.next = lag;
		
		lag.prev = nElement;
		p.next = nElement;
			
		//we must always increment many items in insert
		++manyItems;
		assert wellFormed() : "invariant failed at end of insert";	
	}
	
	/**
	 * Accessor method to get the current element of this book. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this book
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public E getCurrent() {
		assert wellFormed() : "invariant failed at start of getCurrent";	
		if(isCurrent()==false)
			throw new IllegalStateException("is current is false");
		return cursor.data;
	}
	
	//homework 4 solution //modified
	/**
	 * Place all the data of another sequence into this book in order as in {@link #insert}.
	 * The elements should added one by one from the start.
	 * The elements are probably not going to be placed in a single block.
	 * @param addend
	 *   a sequence whose contents will be placed into this sequence
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current el;ement (if any) is
	 *   unchanged.
	 * @exception NullPointerException
	 *   Indicates that addend is null. 
	 **/
	public void insertAll(SortedSequence<E> addend) {
		assert wellFormed() : "invariant failed at start of insertAll";	
		if(addend == null)	throw new NullPointerException("this is null");
		if(addend.manyItems == 0)	return;
		if(addend == this)	addend = addend.clone();
		
		for(Node<E>p = addend.dummy.next; p !=addend.dummy; p=p.next) {
			if(p != addend.dummy) insert(p.data);
			
		}
		assert wellFormed() : "invariant failed at end of insertAll";	
	}
	
	// TODO:
	// Implement size/start/isCurrent/getCurrent/advance/removeCurrent/setCurrent
	// as well as insert/insertAll.
	//
	// You will need to document all methods.  You may copy text and from
	// previous assignments or their solutions from this semester,
	// but you will need update the declarations and the documentation
	// to handle our new generic situation.  In particular, don't refer to a
	// "book" in your documentation comments!
	//
	// Make sure to never use raw types!
	// Your code should not require any unchecked casts.
	//
	// You should avoid unneeded cases.
	// You may use "if" to check for error situation (throw)
	// and to check compareTo (and making sure you don't compare the dummy's data)
	// and finally for the special self-insertAll case, but otherwise
	// please avoid special cases.

	//worked on clone with tutor
	/**
	 * Generate a copy of this sorted sequence.
	 * @return
	 *   The return value is a copy of this sorted sequence. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	@SuppressWarnings("unchecked")
	public SortedSequence<E> clone() { 
		assert wellFormed() : "invariant failed at start of clone";
		SortedSequence<E> answer;

		try
		{
			answer = (SortedSequence<E>) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
	
		// Similar to Homework #4 but with fewer conditions.
		// (Create a new dummy node outside of loop).
		// TODO: Copy the list
		// (make sure cursor is updated too!)
		
		//create a new node so we can not change anything in our original data
		answer.dummy = new Node<E>();
		Node<E>last = answer.dummy;
		//we can loop through our original data since it does not change it
		for(Node<E>p = dummy.next; p!=dummy; p=p.next) {
			Node<E>newNode = new Node<E>(p.data);
			//setting the links
			newNode.prev = last;
			newNode.next = answer.dummy;
			last.next = newNode;
			answer.dummy.prev = newNode;
			if(p==cursor)	answer.cursor = newNode;
			last = newNode;
		}
		
		//make sure dummies are the same
		if(cursor == dummy) {
			answer.cursor = answer.dummy;
		}
		
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class TestInternals extends LockedTestCase {
		String e1 = "A";
		String e2 = "B";
		String e3 = "C";
		String e4 = "D";
		String e5 = "E";
		String e1a = "a";
		String e3a = "c";
		String e5a = "e";

		SortedSequence<String> hs;
		private static Comparator<String> ascending = (s1,s2) -> s1.compareTo(s2);
		private static Comparator<String> descending = (s1,s2) -> s2.compareTo(s1);
		private static Comparator<String> nondiscrimination = (s1,s2) -> 0;

		private int reports = 0;


		// We have to do these operations without causing
		// Java to realize that we are lying about the dummy's data.

		@SuppressWarnings("unchecked")
		private <X,Y> void setDummyData(Node<X> node, Y value) {
			node.data = (X)value;
		}

		private <X> void checkDummyData(Node<X> node) {
			assertSame(node,node.data);
		}


		private void assertWellFormed(boolean expected) {
			reports = 0;
			Consumer<String> savedReporter = reporter;
			try {
				reporter = (String message) -> {
					++reports;
					if (message == null || message.trim().isEmpty()) {
						assertFalse("Uninformative report is not acceptable", true);
					}
					if (expected) {
						assertFalse("Reported error incorrectly: " + message, true);
					}
				};
				assertEquals(expected, hs.wellFormed());
				if (!expected) {
					assertEquals("Expected exactly one invariant error to be reported", 1, reports);
				}
				reporter = null;
			} finally {
				reporter = savedReporter;
			}
		}

		public void testA() { // checking Node constructors
			Node<?> dummy = new Node<String>();
			checkDummyData(dummy);
			assertSame(dummy,dummy.prev);
			assertSame(dummy,dummy.next);

			dummy = new Node<Integer>();
			checkDummyData(dummy);
			assertSame(dummy,dummy.prev);
			assertSame(dummy,dummy.next);

			Object value = "hello!";
			Node<Object> test = new Node<>(value);
			assertSame(value, test.data);
			assertNull(test.prev);
			assertNull(test.next);

			value = Integer.valueOf(1776);
			test = new Node<>(value);
			assertSame(value, test.data);
			assertNull(test.prev);
			assertNull(test.next);
		}

		protected <X> Node<X> newNode(X a, Node<X> p, Node<X> n) {
			Node<X> result = new Node<>(a);
			result.data = a;
			result.prev = p;
			result.next = n;
			return result;
		}

		protected void setUp() {
			hs = new SortedSequence<>(false);
			hs.dummy = new Node<String>();
			hs.cursor = hs.dummy;
			hs.comparator = String.CASE_INSENSITIVE_ORDER;
			hs.manyItems = 0;
		}

		public void testB() {
			assertWellFormed(true);

			hs.comparator = null;
			assertWellFormed(false);
			hs.comparator = nondiscrimination;

			hs.dummy.data = null;
			assertWellFormed(false);
			setDummyData(hs.dummy,new Node<Integer>());
			assertWellFormed(false);
			hs.dummy = null;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);
			hs.dummy = new Node<>();
			hs.cursor = hs.dummy;
			assertWellFormed(true);
		}

		public void testC() {
			hs.manyItems = 1;
			assertWellFormed(false);
			hs.manyItems = 0;
			assertWellFormed(true);
			hs.manyItems = -1;
			assertWellFormed(false);
		}

		public void testD() {
			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = new Node<>();
			assertWellFormed(false);
			hs.cursor.prev = hs.dummy;
			hs.cursor.next = hs.dummy;
			setDummyData(hs.cursor,hs.dummy);
			assertWellFormed(false);
		}

		public void testE() {
			hs.manyItems = 1;
			hs.dummy.next = newNode(e1,hs.dummy,hs.dummy);
			assertWellFormed(false);
			hs.dummy.prev = hs.dummy.next;
			assertWellFormed(true);

			hs.manyItems = 2;
			assertWellFormed(false);
			hs.manyItems = 1;

			hs.comparator = null;
			assertWellFormed(false);
			hs.comparator = nondiscrimination;

			hs.dummy.next = null;
			assertWellFormed(false);
			hs.dummy.next = hs.dummy.prev;

			hs.dummy.prev.next = null;
			assertWellFormed(false);
			hs.dummy.prev.next = hs.dummy;

			assertWellFormed(true);
		}

		public void testF() {
			Node<String> n1 = newNode(e1,hs.dummy,hs.dummy);
			hs.dummy.next = hs.dummy.prev = n1;
			hs.manyItems = 1;
			hs.cursor = hs.dummy;
			assertWellFormed(true);

			hs.cursor = n1;
			assertWellFormed(true);

			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = new Node<>();
			assertWellFormed(false);
			hs.cursor.prev = n1;
			hs.cursor.next = n1;
			assertWellFormed(false);
			hs.cursor = newNode(e1,hs.dummy,hs.dummy);
			assertWellFormed(false);

			hs.dummy.prev = hs.dummy.next = hs.cursor;
			assertWellFormed(true);
			hs.comparator = ascending;
			assertWellFormed(true);
			hs.comparator = descending;
			assertWellFormed(true);
		}

		public void testG() {
			Node<String> n1 = newNode(e1,hs.dummy,hs.dummy);
			hs.dummy.next = hs.dummy.prev = n1;
			hs.manyItems = 1;
			hs.cursor = hs.dummy;
			assertWellFormed(true);

			Node<String> n1a = newNode(e1,hs.dummy,hs.dummy);
			Node<String> n0a = new Node<>();
			n0a.next = n0a.next = n1;

			hs.dummy.prev = null;
			assertWellFormed(false);
			hs.dummy.prev = n1a;
			assertWellFormed(false);
			hs.dummy.prev = hs.dummy;
			assertWellFormed(false);
			hs.dummy.prev = n1;

			n1.prev = null;
			assertWellFormed(false);
			n1.prev = n0a;
			assertWellFormed(false);
			n1.prev = hs.dummy;

			assertWellFormed(true);
		}

		public void testH() {
			Node<String> n1 = newNode(e1,hs.dummy,null);
			Node<String> n2 = newNode(e2,n1,hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			hs.dummy.prev = n2;
			hs.manyItems = 2;
			assertWellFormed(true);

			hs.manyItems = 1;
			assertWellFormed(false);
			hs.manyItems = 3;
			assertWellFormed(false);
			hs.manyItems = 0;
			assertWellFormed(false);
		}

		public void testI() {
			Node<String> n1 = newNode(e1,hs.dummy,null);
			Node<String> n2 = newNode(e2,n1,hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			hs.dummy.prev = n2;
			hs.manyItems = 2;
			assertWellFormed(true);

			hs.cursor = new Node<>();
			hs.cursor.prev = n2;
			hs.cursor.next = n1;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = newNode(e1,hs.dummy,n2);
			assertWellFormed(false);
			hs.cursor = newNode(e2,n1,hs.dummy);
			assertWellFormed(false);

			hs.cursor = n1;
			assertWellFormed(true);
			hs.cursor = n2;
			assertWellFormed(true);

			hs.manyItems = 3;
			assertWellFormed(false);
		}

		public void testJ() {
			Node<String> n1 = newNode(e1,hs.dummy,null);
			Node<String> n2 = newNode(e2,n1,hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			hs.dummy.prev = n2;
			hs.manyItems = 2;
			assertWellFormed(true);

			Node<String> n1a = newNode(e1,hs.dummy,n2);
			Node<String> n2a = newNode(e2,n1,hs.dummy);
			Node<String> n0a = newNode(null,n2,n1);
			setDummyData(n0a,hs.dummy);

			hs.dummy.prev = null;
			assertWellFormed(false);
			hs.dummy.prev = n1;
			assertWellFormed(false);
			hs.dummy.prev = hs.dummy;
			assertWellFormed(false);
			hs.dummy.prev = n2a;
			assertWellFormed(false);
			hs.dummy.prev = n2;

			n1.prev = n1;
			assertWellFormed(false);
			n1.prev = n2;
			assertWellFormed(false);
			n1.prev = n0a;
			assertWellFormed(false);
			n1.prev = null;
			assertWellFormed(false);
			n1.prev = hs.dummy;

			n2.prev = hs.dummy;
			assertWellFormed(false);
			n2.prev = n1a;
			assertWellFormed(false);
			n2.prev = n2;
			assertWellFormed(false);
			n2.prev = null;
			assertWellFormed(false);
			n2.prev = n1;

			assertWellFormed(true);
		}

		public void testK() {
			Node<String> n1 = newNode(e1,hs.dummy,null);
			Node<String> n2 = newNode(e2,n1,hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			hs.dummy.prev = n2;
			hs.manyItems = 2;
			assertWellFormed(true);

			hs.comparator = null;
			assertWellFormed(false);
			hs.comparator = descending;
			assertWellFormed(false);
			hs.comparator = ascending;
			assertWellFormed(true);
			hs.comparator = nondiscrimination;
			assertWellFormed(true);

			n2.data = null;
			assertWellFormed(false);
			n2.data = e2;
			n1.data = null;
			assertWellFormed(false);
		}

		public void testL() {
			Node<String> n1 = newNode(e1, hs.dummy, null);
			Node<String> n2 = newNode(e2, n1, null);
			Node<String> n3 = newNode(e3, n2, null);
			Node<String> n4 = newNode(e4, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.dummy.prev = n5;

			hs.manyItems = 1;
			assertWellFormed(false);
			hs.manyItems = 2;
			assertWellFormed(false);
			hs.manyItems = 3;
			assertWellFormed(false);
			hs.manyItems = 4;
			assertWellFormed(false);
			hs.manyItems = 0;
			assertWellFormed(false);
			hs.manyItems = 6;
			assertWellFormed(false);
			hs.manyItems = 5;
			assertWellFormed(true);
		}

		public void testM() {
			Node<String> n1 = newNode(e1, hs.dummy, null);
			Node<String> n2 = newNode(e2, n1, null);
			Node<String> n3 = newNode(e3, n2, null);
			Node<String> n4 = newNode(e4, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.manyItems = 5;
			hs.dummy.prev = n5;
			assertWellFormed(true);

			hs.cursor = n1;
			assertWellFormed(true);
			hs.cursor = n2;
			assertWellFormed(true);
			hs.cursor = n3;
			assertWellFormed(true);
			hs.cursor = n4;
			assertWellFormed(true);
			hs.cursor = n5;
			assertWellFormed(true);
		}

		public void testN() {
			Node<String> n1 = newNode(e1, hs.dummy, null);
			Node<String> n2 = newNode(e2, n1, null);
			Node<String> n3 = newNode(e3, n2, null);
			Node<String> n4 = newNode(e4, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.dummy.prev = n5;
			hs.manyItems = 5;
			assertWellFormed(true);


			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = new Node<>();
			assertWellFormed(false);
			hs.cursor = newNode(e1, hs.dummy, n2);
			assertWellFormed(false);
			hs.cursor = newNode(e2, n1, n3);
			assertWellFormed(false);
			hs.cursor = newNode(e3, n2, n4);
			assertWellFormed(false);
			hs.cursor = newNode(e4, n3, n5);
			assertWellFormed(false);
			hs.cursor = newNode(e5, n4, hs.dummy);
			assertWellFormed(false);
		}

		public void testO() {
			Node<String> n1 = newNode(e5, hs.dummy, null);
			Node<String> n2 = newNode(e5, n1, null);
			Node<String> n3 = newNode(e5, n2, null);
			Node<String> n4 = newNode(e5, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.dummy.prev = n5;
			hs.manyItems = 5;
			assertWellFormed(true);

			hs.comparator = ascending;
			assertWellFormed(true);
			hs.comparator = descending;
			assertWellFormed(true);
			hs.comparator = nondiscrimination;
			assertWellFormed(true);			
		}

		public void testP() {
			Node<String> n1 = newNode(e1, hs.dummy, null);
			Node<String> n2 = newNode(e2, n1, null);
			Node<String> n3 = newNode(e3, n2, null);
			Node<String> n4 = newNode(e4, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.dummy.prev = n5;
			hs.manyItems = 5;
			assertWellFormed(true);

			n5.data = null;
			assertWellFormed(false);

			n5.data = e3;
			assertWellFormed(false);

			n4.data = e3;
			assertWellFormed(true);

			n4.data = null;
			assertWellFormed(false);

			n4.data = e2;
			assertWellFormed(false);

			n3.data = e2;
			assertWellFormed(true);

			n3.data = null;
			assertWellFormed(false);

			n3.data = e1;
			assertWellFormed(false);

			n2.data = e1;
			assertWellFormed(true);

			n2.data = null;
			assertWellFormed(false);
		}

		public void testQ() {
			Node<String> n1 = newNode(e1, hs.dummy, null);
			Node<String> n2 = newNode(e2, n1, null);
			Node<String> n3 = newNode(e3, n2, null);
			Node<String> n4 = newNode(e4, n3, null);
			Node<String> n5 = newNode(e5, n4, hs.dummy);
			hs.dummy.next = n1;
			n1.next = n2;
			n2.next = n3;
			n3.next = n4;
			n4.next = n5;
			hs.dummy.prev = n5;
			hs.manyItems = 5;
			assertWellFormed(true);

			n1.data = e1a;
			assertWellFormed(true);
			n3.data = e3a;
			assertWellFormed(true);
			n5.data = e5a;
			assertWellFormed(true);

			hs.comparator = ascending;
			assertWellFormed(false);
			hs.comparator = descending;
			assertWellFormed(false);
			hs.comparator = nondiscrimination;
			assertWellFormed(true);
		}
	}


}

