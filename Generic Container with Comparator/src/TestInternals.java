import edu.uwm.cs351.SortedSequence;


public class TestInternals extends SortedSequence.TestInternals {
	
	// Locked tests:
	
	@SuppressWarnings("unused")
	public void test() {
		// If a sequence has 4 elements in it, e.g. [3,5,7,9]
		Integer a1 = Ti(1135362951); // how many nodes are present ?
		Integer a2 = Ti(1609560358); // how many null pointers are present ?
		Integer a3 = Ti(114470815); // how many links between nodes are there ?
		Integer a4 = Ti(962993746); // What is the value of dummy.prev.prev.data ?
		// Assume there is no current element
		Integer a5 = Ti(1541530335); // what is the value of cursor.next.data ? (-1 if error)		
	}
	
	public void test9() {
		new SortedSequence<String>();
	}
}
