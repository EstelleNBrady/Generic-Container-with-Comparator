import java.util.Comparator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import edu.uwm.cs.random.AbstractRandomTest;
import edu.uwm.cs.random.Command;
import edu.uwm.cs351.SortedSequence;
import edu.uwm.cs351.test.ArraySequence;


public class RandomTest extends AbstractRandomTest<ArraySequence<Integer>,SortedSequence<Integer>> {

	private static final int MAX_TESTS = 1_000_000;
	private static final int DEFAULT_MAX_TEST_LENGTH = 1_000;
		
	public static final int NUM_INTEGERS = 20;
	private static Comparator<Integer> myComparator = (o1,o2) -> o1/2-o2/2;
		
	@SuppressWarnings("unchecked")
	private static Class<SortedSequence<Integer>> sutClass = (Class<SortedSequence<Integer>>)(Class<?>)SortedSequence.class;

	@SuppressWarnings("unchecked")
	private static Class<ArraySequence<Integer>> refClass = (Class<ArraySequence<Integer>>)(Class<?>)ArraySequence.class;

	protected RandomTest() {
		super(refClass, sutClass, "SortedSequence<Integer>", "ss", MAX_TESTS, DEFAULT_MAX_TEST_LENGTH);
	}
	
	protected Integer randomInteger(Random r) {
		int n = r.nextInt(NUM_INTEGERS) - 1;
		if (n < 0) return null;
		return n;
	}
	
	@Override
	public String toString(Object x) {
		if (x instanceof Comparator<?>) return "myComparator";
		// if (x instanceof Integer) return "Integer.valueOf("+x+")";
		else return super.toString(x);
	}
	

	//private Command<?> newSequenceCommand = newCommand();
	private Function<Comparator<Integer>,Command<?>> newSequenceCommand = create(mainClass, (c) -> new ArraySequence<>(c), (c) -> new SortedSequence<>(c));
    private Function<Integer,Command<?>> sizeCommand = build(lift(ArraySequence<Integer>::size), lift(SortedSequence<Integer>::size), "size"); 
    private Function<Integer, Command<?>> startCommand = build(lift(ArraySequence<Integer>::start), lift(SortedSequence<Integer>::start), "start");
    private Function<Integer, Command<?>> isCurrentCommand = build(lift(ArraySequence<Integer>::isCurrent), lift(SortedSequence<Integer>::isCurrent), "isCurrent");
    private Function<Integer, Command<?>> getCurrentCommand = build(lift(ArraySequence<Integer>::getCurrent), lift(SortedSequence<Integer>::getCurrent), "getCurrent");
    private Function<Integer, Command<?>> advanceCommand = build(lift(ArraySequence<Integer>::advance), lift(SortedSequence<Integer>::advance), "advance");
    private Function<Integer, Command<?>> removeCurrentCommand = build(lift(ArraySequence<Integer>::removeCurrent), lift(SortedSequence<Integer>::removeCurrent), "removeCurrent");
    private BiFunction<Integer, Integer, Command<?>> setCurrentCommand = build(lift(ArraySequence<Integer>::setCurrent), lift(SortedSequence<Integer>::setCurrent), "setCurrent");
    private BiFunction<Integer, Integer, Command<?>> insertCommand = build(lift(ArraySequence<Integer>::insert), lift(SortedSequence<Integer>::insert), "insert");
    private BiFunction<Integer, Integer, Command<?>> insertAllCommand = build(lift(ArraySequence<Integer>::insertAll), lift(SortedSequence<Integer>::insertAll), mainClass, "insertAll");
    private Function<Integer,Command<?>> cloneCommand = clone(ArraySequence<Integer>::clone, SortedSequence<Integer>::clone, "clone");

    @Override
    protected Command<?> randomCommand(Random r) {
    	int n = mainClass.size();
    	if (n == 0) return newSequenceCommand.apply(myComparator);
    	int w = r.nextInt(n);

    	switch (r.nextInt(20)) {
    	default:
    	case 0:
    		return newSequenceCommand.apply(myComparator);
    	case 1:
    		return cloneCommand.apply(w);
    	case 2:
    		return startCommand.apply(w);
    	case 3:
    		return insertAllCommand.apply(w, r.nextInt(n));
    	case 4:
    	case 5:
    		return insertCommand.apply(w,randomInteger(r));
    	case 6:
    	case 7:
    		return removeCurrentCommand.apply(w);
    	case 8:
    	case 9:
    	case 10:
    		return getCurrentCommand.apply(w);
    	case 11:
    	case 12:
    		return sizeCommand.apply(w);
    	case 13:
    	case 14:
    	case 15:
    		return isCurrentCommand.apply(w);
    	case 16:
    	case 17:
    		return advanceCommand.apply(w);
    	case 18:
    	case 19:
    		return setCurrentCommand.apply(w, randomInteger(r));
    	}
    }


	@Override
	public void printImports() {	
		super.printImports();
		System.out.println("import java.util.Comparator;\n");
		System.out.println("import edu.uwm.cs351.SortedSequence;");	
	}
	
	@Override
	public void printHelperMethods() {
		super.printHelperMethods();
		System.out.println("\tComparator<Integer> myComparator = (i1,i2) -> i1/2 - i2/2;\n");
	}
	
	public static void main(String[] args) {
		RandomTest rt = new RandomTest();
		rt.run();
	}
}
