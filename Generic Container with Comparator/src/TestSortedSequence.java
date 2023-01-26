import java.util.function.Supplier;

import edu.uwm.cs351.SortedSequence;
import junit.framework.TestCase;


public class TestSortedSequence extends TestCase {
	String e1 = "a";
	String e2 = "b";
	String e3 = "c";
	String e4 = "d";
	String e5 = "e";
	
	SortedSequence<String> s;
	
	@Override
	protected void setUp() {
		try {
			assert 1/s.size() == 42 : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (NullPointerException ex) {
			// muffle exception
		}
		s = new SortedSequence<>(String.CASE_INSENSITIVE_ORDER);
	}

	protected <T> void assertException(Class<?> excClass, Supplier<T> producer) {
		try {
			T result = producer.get();
			assertFalse("Should have thrown an exception, not returned " + result,true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	protected <T> void assertException(Runnable f, Class<?> excClass) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	/**
	 * Return the appointment as an integer
	 * <dl>
	 * <dt>-1<dd><i>(an exception was thrown)
	 * <dt>0<dd>null
	 * <dt>1<dd>e1
	 * <dt>2<dd>e2
	 * <dt>3<dd>e3
	 * <dt>4<dd>e4
	 * <dt>5<dd>e5
	 * </dl>
	 * @return integer encoding of appointment supplied
	 */
	protected int asInt(Supplier<String> g) {
		try {
			String n = g.get();
			if (n == null) return 0;
			return (n.charAt(0))-'a'+1;
		} catch (RuntimeException ex) {
			return -1;
		}
	}

	public void test00() {
		// Nothing inserted yet:
		assertEquals(0,s.size());
		assertFalse(s.isCurrent());
		s.start();
		assertFalse(s.isCurrent());
	}
	
	public void test01() {
		assertException(IllegalStateException.class, () -> s.getCurrent());
		s.insert(e1);
		assertFalse(s.isCurrent());
		s.start();
		assertSame(e1, s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
	}
	
	public void test02() {
		// Initially empty.
		s.insert(e5);
		s.insert(e4);
		assertException(IllegalStateException.class, () -> s.getCurrent());
		s.start();
		assertSame(e4, s.getCurrent());
		s.advance();
		assertSame(e5, s.getCurrent());
		s.advance();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}
	
	public void test03() {
		// Initially empty
		s.insert(e3);
		s.start();
		s.insert(e2);
		// -1 for error, 0 for null, 1 for e1, 2 for e2 ...
		assertSame(e3, s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		s.start();
		assertSame(e2, s.getCurrent());
	}
	
	public void test04() {
		// Initial empty
		s.insert(e1);
		s.start();
		s.insert(e5);
		// -1 for error, 0 for null, 1 for e1, 2 for e2 ...
		assertSame(e1, s.getCurrent());
		s.advance();
		assertSame(e5, s.getCurrent());
		s.insert(e3);
		assertSame(e5, s.getCurrent());
		s.advance();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}
	
	public void test05() {
		assertException(() -> s.insert(null), IllegalArgumentException.class);
		assertEquals(0,s.size());
	}
	
	public void test06() {
		s.insert(e1);
		s.insert(e2);
		s.start();
		s.advance();
		assertSame(e2,s.getCurrent());
		SortedSequence<String> s2 = new SortedSequence<>();
		s2.insert(e4);
		s.insertAll(s2);
		assertEquals(3,s.size());
		assertSame(e2, s.getCurrent());
		s.advance();
		assertSame(e4, s.getCurrent());
		s.advance();
		assertEquals(false, s.isCurrent());
	}
	
	public void test07() {
		s.start();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}
	
	public void test08() {
		s.start();
		assertException(() -> s.removeCurrent(), IllegalStateException.class);
	}
	
	public void test09() {
		assertException(() -> s.advance(), IllegalStateException.class);
	}
	
	public void test10() {
		s.insert(e1);
		assertEquals(1,s.size());
		assertFalse(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertEquals(1,s.size());
		assertFalse(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertEquals(1,s.size());
	}

	public void test11() {
		s.insert(e1);
		s.start();
		s.removeCurrent();
		assertFalse(s.isCurrent());
		assertEquals(0,s.size());	
		s.insert(e2);
		s.start();
		assertSame(e2,s.getCurrent());
		assertEquals(1,s.size());
	}
	
	public void test12() {
		s.insert(e2);
		s.start();
		s.advance();
		try {
			s.advance();
			assertFalse("s.advance() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex,ex instanceof IllegalStateException);
		}
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
	}


	public void test13() {
		s.insert(e2);
		try {
			s.removeCurrent();
			assertFalse("s.removeCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex,ex instanceof IllegalStateException);
		}
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
	}

	public void test14() {
		s.insert(e2);
		try {
			s.getCurrent();
			assertFalse("s.getCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex,ex instanceof IllegalStateException);
		}
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
	}

	public void test16() {
		s.insert(e4);
		s.setCurrent(e3);
		assertSame(e4, s.getCurrent());
		// e4a sort the same as e4 except it's not the same object.
		String e4a = "D";
		s.setCurrent(e4a);
		assertSame(e4, s.getCurrent());
		s.setCurrent(e5);
		assertEquals(false, s.isCurrent());
	}
	
	public void test20() {
		s.insert(e1);
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.insert(e2);
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertEquals(2,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		s.start();
		assertSame(e1,s.getCurrent());
	}
	
	public void test21() {
		s.insert(e1);
		assertFalse(s.isCurrent());
		s.start();
		s.advance();
		s.insert(e2);
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
		assertTrue(s.isCurrent());
	}
	
	public void test22() {
		s.insert(e2);
		s.insert(e1);
		s.start();
		s.removeCurrent();
		assertTrue(s.isCurrent());
		assertEquals(1,s.size());
		assertEquals(e2,s.getCurrent());
		s.insert(e3);
		s.advance();
		assertTrue(s.isCurrent());
		assertEquals(2,s.size());
		assertSame(e3,s.getCurrent());
		s.start();
		assertSame(e2,s.getCurrent());
	}

	public void test23() {
		s.insert(e2);
		s.insert(e1);
		s.start();
		s.advance();
		s.removeCurrent();
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
		try {
			s.getCurrent();
			assertFalse("s.getCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex,ex instanceof IllegalStateException);
		}
		s.insert(e3);
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertEquals(e1,s.getCurrent());
		s.advance();
		assertEquals(e3,s.getCurrent());
	}
	
	public void test24() {
		s.insert(e1);
		assertFalse(s.isCurrent());
		s.start();
		s.insert(e1);
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e1,s.getCurrent());
	}
	
	public void test25() {
		// equivalent to e1:
		String e1a = "A";
		s.insert(e1);
		assertFalse(s.isCurrent());
		s.start();
		s.insert(e1a);
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e1a,s.getCurrent());
	}
	
	public void test27() {
		// equivalent to e1:
		String e1a = "A";
		s.insert(e1);
		s.insert(e1a);
		s.setCurrent(e1a);
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e1a,s.getCurrent());
	}
	
	public void test30() {
		s.insert(e3);
		s.insert(e1);
		s.insert(e2);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		s.start();
		assertSame(e1,s.getCurrent());
	}
	
	public void test31() {
		s.insert(e1);
		s.start();
		s.advance();
		s.insert(e4);
		s.insert(e3);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e4,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test32() {
		s.insert(e2);
		s.start();
		s.advance();
		s.insert(e3);
		s.start();
		s.insert(e1);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
	}
	
	public void test33() {
		s.insert(e3);
		s.insert(e2);
		s.insert(e1);
		s.start(); 
		assertEquals(e1,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
	}

	public void test34() {
		s.insert(e3);
		s.insert(e2);
		s.insert(e1);
		s.start(); // REDUNDANT
		s.advance();
		assertSame(e2,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
	}
	
	public void test35() {
		s.insert(e3);
		s.insert(e2);
		s.insert(e1);
		s.start();
		s.advance();
		s.advance();
		assertSame(e3,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertFalse(s.isCurrent());
		try {
			s.getCurrent();
			assertFalse("s.getCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex, ex instanceof IllegalStateException);
		}
		try {
			s.advance();
			assertFalse("s.advance() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex, ex instanceof IllegalStateException);
		}
		s.start();
		assertSame(e1,s.getCurrent());
	}
 
	public void test37() {
		s.insert(e1);
		s.insert(e1);
		s.start();
		s.advance();
		s.insert(e1);
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e1,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());
	}
	
	public void test39() {
		s.insert(e1);
		s.insert(e2);
		s.insert(e3);
		s.insert(e4);
		s.insert(e5);
		assertFalse(s.isCurrent());
		s.insert(e1);
		s.insert(e2);
		s.insert(e3);
		s.insert(e4);
		s.insert(e5);
		s.insert(e1);
		s.insert(e2);
		assertEquals(12,s.size());
		s.start();
		s.removeCurrent();
		assertTrue(s.isCurrent());
		s.start();
		s.removeCurrent();
		assertSame(e1,s.getCurrent());
		assertEquals(10,s.size());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e5,s.getCurrent()); s.advance();
		assertSame(e5,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());
	}
	
	public void test40() {
		SortedSequence<String> se = new SortedSequence<>();
		s.insertAll(se);
		assertFalse(s.isCurrent());
		assertEquals(0,s.size());
		s.insert(e1);
		s.start();
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertEquals(1,s.size());
		assertEquals(0,se.size());
		assertSame(e1,s.getCurrent());
		s.advance();
		s.insertAll(se);
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
		assertEquals(0,se.size());
		s.insert(e2);
		s.setCurrent(e2);
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		assertEquals(2,s.size());
		assertEquals(0,se.size());
		s.start();
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertEquals(2,s.size());
		assertEquals(0,se.size());
	}
	
	public void test41() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e1);
		se.start();
		s.insertAll(se);
		assertFalse(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertTrue(se.isCurrent());
		assertEquals(1,s.size());
		assertEquals(1,se.size());
	}
	
	public void test42() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e1);
		s.insert(e2);
		s.start();
		s.insertAll(se);
		assertEquals(e2,s.getCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertEquals(2,s.size());
		assertEquals(1,se.size());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
	}
	
	public void test43() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e3);
		s.insert(e2);
		s.start();
		s.advance();
		s.insertAll(se);
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		assertEquals(1,se.size());
		assertFalse(se.isCurrent());
		s.start();
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
	}
	
	public void test44() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e1);
		s.insert(e2);
		s.insert(e3);
		s.insertAll(se);
		assertFalse(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertEquals(3,s.size());
		assertEquals(1,se.size());
		assertFalse(se.isCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());	
	}
	
	public void test45() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e3);
		s.insert(e2);
		s.start();
		s.insert(e4);
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertEquals(3,s.size());
		assertEquals(1,se.size());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
		s.advance();
		assertSame(e4,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		s.start();
		assertSame(e2,s.getCurrent());
	}
	
	public void test46() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e5);
		se.start();
		s.insert(e2);
		s.start();
		s.insert(e3);
		s.advance();
		assertTrue(s.isCurrent());
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertEquals(e3,s.getCurrent());
		s.advance();
		assertSame(e5,s.getCurrent());
		assertEquals(3,s.size());
		assertEquals(1,se.size());
		assertSame(e5,se.getCurrent());
		s.start();
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
		s.advance();
		assertSame(e5,s.getCurrent());
	}

	public void test47() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e1);
		se.insert(e3);	
		s.insert(e2);
		s.insert(e4);
		s.insertAll(se);
		assertFalse(s.isCurrent());
		assertEquals(4,s.size());
		assertEquals(2,se.size());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());	
	}

	public void test48() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e2);
		se.insert(e1);
		se.start();
		se.advance();
		s.insert(e3);
		s.start();
		s.insert(e4);
		s.insertAll(se);
		assertTrue(s.isCurrent());
		assertEquals(4,s.size());
		assertEquals(2,se.size());
		assertSame(e2,se.getCurrent()); se.advance();
		assertFalse(se.isCurrent());
		// check s
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());	
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test49() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e4);
		se.insert(e1);
		se.start();
		se.advance();
		se.advance();
		s.insert(e3);
		s.start();
		s.advance();
		s.insert(e2);
		assertFalse(s.isCurrent());
		assertFalse(se.isCurrent());
		s.insertAll(se);
		assertFalse(s.isCurrent());
		assertFalse(se.isCurrent());
		assertEquals(4,s.size());
		assertEquals(2,se.size());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());	
	}

	public void test50() {
		SortedSequence<String> se = new SortedSequence<String>();
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		se.insert(e3);
		se.insert(e4);
		se.insert(e5);
		// se has 24 elements
		s.insert(e1);
		s.insert(e2);
		s.insertAll(se);
		assertEquals(26,s.size());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		s.insertAll(se);
		assertEquals(50,s.size());
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();		
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e5,s.getCurrent()); s.advance();
	}
	
	public void test51() {
		s.insertAll(s);
		assertFalse(s.isCurrent());
		assertEquals(0,s.size());
	}
	
	
	public void test52() {
		s.insert(e1);
		s.start();
		s.insertAll(s);
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
	}
	
	public void test53() {
		s.insert(e1);
		s.start();
		s.advance();
		s.insertAll(s);
		assertEquals(2,s.size());
		assertFalse(s.isCurrent());
	}
	
	public void test54() {
		s.insert(e1);
		s.start();
		s.removeCurrent();
		assertEquals(0,s.size());
		assertFalse(s.isCurrent());
	}
	
	public void test55() {
		s.insert(e2);
		s.insert(e1);
		s.start();
		s.insertAll(s);
		assertEquals(4,s.size());
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());		
	}
	
	public void test56() {
		s.insert(e1);
		s.start();
		s.insert(e2);
		s.advance();
		s.insertAll(s);
		assertEquals(4,s.size());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());	
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test57() {
		s.insert(e1);
		s.insert(e2);
		assertFalse(s.isCurrent());
		s.insertAll(s);
		assertFalse(s.isCurrent());
		assertEquals(4,s.size());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());		
	}

	public void test58() {
		s.insert(e1);
		s.start();
		s.insert(e2);
		s.insertAll(s);
		s.removeCurrent();
		s.insert(e3);
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());
		s.start();
		assertSame(e1,s.getCurrent()); s.advance();
		s.advance(); // one more advance!
		s.insertAll(s);
		assertEquals(8,s.size());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e2,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e3,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());		
	}
	
	public void test59() {
		SortedSequence<String> se = new SortedSequence<String>(String.CASE_INSENSITIVE_ORDER);
		String e1 = "Apple";
		String e1a = "apple";
		String e1b = "APPLE";
		se.insert(e1a);
		se.start();
		se.insert(e1b);	
		s.insert(e1);
		s.insert(e5);
		s.start();
		s.advance();
		s.insertAll(se); // s = e1 e1a e1b * e5
		s.insert(e4); // s = e1 e1a e1b e4 * e5
		assertTrue(s.isCurrent());
		assertSame(e5,s.getCurrent());
		assertEquals(5,s.size());
		s.start();
		assertSame(e1, s.getCurrent());
		s.advance();
		assertSame(e1a,  s.getCurrent());
		s.advance();
		assertSame(e1b, s.getCurrent());
		assertEquals(2,se.size());
		assertSame(e1a,se.getCurrent());
		se.advance();
		se.advance();
		assertFalse(se.isCurrent());
		se.start();
		assertSame(e1a,se.getCurrent());
	}
	
	
	/// testing clone
	
	public void test60() {
		SortedSequence<String> c = s.clone();
		assertFalse(c.isCurrent());
		assertEquals(0, c.size());
	}
	
	public void test61() {
		s.insert(e1);
		s.start();
		SortedSequence<String> c = s.clone();
		
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e1,c.getCurrent()); c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
	}
	
	public void test62() {
		s.insert(e1);
		SortedSequence<String> c = s.clone();
		
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
	}

	public void test63() {
		SortedSequence<String> c = s.clone();
		assertFalse(c.isCurrent());
		
		s.insert(e1);
		s.start();
		c = s.clone();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());
		
		s.insert(e2);
		s.advance();
		c = s.clone();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e2,s.getCurrent());
		assertSame(e2,c.getCurrent());
		s.advance();
		c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
		
		s.insert(e3);
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
		
		s.start();
		s.advance();
		s.advance();
		c = s.clone();
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());
		s.advance();
		c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
		s.start();
		c.start();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e2,s.getCurrent());
		assertSame(e2,c.getCurrent());
		
		s.start();
		c = s.clone();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e2,s.getCurrent());
		assertSame(e2,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());		
	}
	
	public void test64() {
		s.insert(e1);
		s.insert(e3);
		s.insert(e2);
		s.start();
		s.advance();
		s.removeCurrent();
		
		SortedSequence<String> c = s.clone();
		
		assertEquals(2,c.size());
		
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());
	}

	public void test65() {
		s.insert(e3);
		s.insert(e4);
		
		SortedSequence<String> c = s.clone();
		s.insert(e1);
		c.insert(e2);
		
		s.start();
		c.start();
		assertSame(e1,s.getCurrent());
		assertSame(e2,c.getCurrent());
		s.advance();
		c.advance();
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());
		s.advance();
		c.advance();
		assertSame(e4,s.getCurrent());
		assertSame(e4,c.getCurrent());
		s.advance();
		c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
	}
	
}
