package com.google.mattmo;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Matthew Gretton
 * @param <E>
 */
public interface Tensor<E> extends Collection<E>
{
  //Tensor methods
  public E get(final int... tensorIndex);
  public int[] indexSizes();
  public int order();
  public Tensor<E> contract(final int[] fixedIndexPositions, int[] fixedIndexValues);
  public Tensor<E> project(final int[] projectionIndexPositions, final int[] projectionIndexSizes);
  public Iterable<Tensor<E>> contractedTensorIterable(final int... fixedIndexPositions);

  //object methods
  @Override
  public boolean equals(final Object o);
  @Override
  public int hashCode();

  //iterator methods
  @Override
  public Iterator<E> iterator();

  //Collections methods
  @Override
  public int size();
  @Override
  public boolean isEmpty();
  @Override
  public boolean contains(final Object o);
  @Override
  public E[] toArray();
  @Override
  public <T> T[] toArray(final T[] a);
  @Override
  public boolean add(final E e);
  @Override
  public boolean remove(final Object o);
  @Override
  public boolean containsAll(final Collection<?> c);
  @Override
  public boolean addAll(final Collection<? extends E> c);
  @Override
  public boolean removeAll(final Collection<?> c);
  @Override
  public boolean retainAll(final Collection<?> c);
  @Override
  public void clear();
}




