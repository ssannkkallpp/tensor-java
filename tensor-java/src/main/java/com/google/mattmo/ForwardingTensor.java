package com.google.mattmo;

import com.google.common.base.Function;

import java.util.Collection;
import java.util.Iterator;

/**
 * Delegates all methods to the tensor supplied in the constructor.
 */
public class ForwardingTensor<E> implements Tensor<E>
{
  private final Tensor<E> _tensor;

  public ForwardingTensor(Tensor<E> aTensor)
  {
    _tensor = aTensor;
  }

  @Override
  public E get(int... tensorIndex) {return _tensor.get(tensorIndex);}
  @Override
  public int[] indexSizes() {return _tensor.indexSizes();}
  @Override
  public int order() {return _tensor.order();}
  @Override
  public boolean add(E o) {return _tensor.add(o);}
  @Override
  public boolean addAll(Collection<? extends E> c) {return _tensor.addAll(c);}
  @Override
  public void clear() {_tensor.clear();}
  @Override
  public boolean contains(Object o) {return _tensor.contains(o);}
  @Override
  public boolean containsAll(Collection<?> c) {return _tensor.containsAll(c);}
  @Override
  public boolean equals(Object o) {return _tensor.equals(o);}
  @Override
  public int hashCode() {return _tensor.hashCode();}
  @Override
  public boolean isEmpty() {return _tensor.isEmpty();}
  @Override
  public Iterator<E> iterator() {return _tensor.iterator();}
  @Override
  public boolean remove(Object o) {return _tensor.remove(o);}
  @Override
  public boolean removeAll(Collection<?> c) {return _tensor.removeAll(c);}
  @Override
  public boolean retainAll(Collection<?> c) {return _tensor.retainAll(c);}
  @Override
  public int size() {return _tensor.size();}
  @Override
  public Object[] toArray() {return _tensor.toArray();}
  @Override
  public <T> T[] toArray(T[] a) {return _tensor.toArray(a);}
}