package com.nabiki.corona.ta;

import java.util.Collection;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;

public class Series<E> extends Vector<E> {
	private static final long serialVersionUID = 1L;
	private Object sync = new Object();

    /**
     * Construct an empty Series.
     */
    public Series() {
        super();
    }

    /**
     * Construct a Series containing the elements in the specified collection,
     * in order they are returned by collection's iterator.
     *
     * @param toCopy the collection whose elements are to be placed into this vector
     * @throws NullPointerException if the specified collection is null
     */
    public Series(Collection<E> toCopy) {
        super(toCopy);
    }

    /**
     * Get element of the given Series at specified position in reserved order.
     * The method calls {@code ref(index)} of the given Series.
     *
     * @param series Series to inspect on
     * @param index position of the element to be retrieved
     * @return element at specified position
     */
    public static Object ref(@SuppressWarnings("rawtypes") Series series, int index) {
        return series.ref(index);
    }

    /**
     * Thread-safe add an element to the end of this Series.
     *
     * @param e element to be appended to this Series.
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        synchronized (this.sync) {
            return super.add(e);
        }
    }

    /**
     * Get element of the given Series at specified position in reserved order. For example, the latest element in Series
     * is obtained with index of zero.
     * <p>Position parameter is checked by {@code Objects.checkIndex(index, size)}.
     * <p>The method is thread-safe.
     *
     * @param index specified position of the element
     * @return element at specified position
     */
    public E ref(int index) {
        Objects.checkIndex(index, super.size());
        synchronized (this.sync) {
            return super.elementAt(size() - 1 - index);
        }
    }

    /**
     * Get number of elements in this Series. The method is thread-safe.
     *
     * @return number of elements in this Series
     */
    @Override
    public int size() {
        synchronized (this.sync) {
            return super.size();
        }
    }

    /**
     * Call the specified consumer on each of latest elements to the specified count, in direction from head to tail.
     * <p>The specified count can't be larger than size of the Series.
     * <p>The method is thread-safe.
     *
     * @param action consumer on the elements
     * @param count the number of latest elements to be accessed
     * @throws IndexOutOfBoundsException if the specified count exceeds the size of this Series
     */
    public void visit(Consumer<? super E> action, int count) {
        if (count > 0)
            visit(action, size() - count, size());
    }

    /**
     * Call the specified consumer in the range of [from, to).
     * <p>Indexes are checked by {@code Objects.checkIndex(from, to, length)}.
     * <p>The method is thread-safe.
     *
     * @param action consumer on the elements
     * @param from starting index, inclusive
     * @param to ending index, exclusive
     * @throws IndexOutOfBoundsException if the sub-range is out of bounds
     */
    public void visit(Consumer<? super E> action, int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        synchronized (this.sync) {
            for (; from < to; ++from)
                action.accept(super.elementAt(from));
        }
    }

    /**
     * Thread-safe version of for-each.
     *
     * @param action consumer on the elements
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        if (action == null)
            throw new NullPointerException("Consumer null pointer.");
        visit(action, super.size());
    }
}
