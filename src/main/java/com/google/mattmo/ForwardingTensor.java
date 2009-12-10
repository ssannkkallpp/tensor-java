package com.google.mattmo;

import java.util.Collection;
import java.util.Iterator;

public class ForwardingTensor<E> implements Tensor<E>
{
  private final Tensor<E> _tensor;

  public ForwardingTensor(Tensor<E> _tensor)
  {
    this._tensor = _tensor;
  }

  public Tensor<E> subTensor(Integer... tensorIndex)
  {
    return _tensor.subTensor(tensorIndex);
  }

  public E get(int... tensorIndex)
  {
    return _tensor.get(tensorIndex);
  }

  public int size()
  {
    return _tensor.size();
  }

  public boolean isEmpty()
  {
    return _tensor.isEmpty();
  }

  public boolean contains(Object o)
  {
    return _tensor.contains(o);
  }

  public int[] indexSizes()
  {
    return _tensor.indexSizes();
  }

  public int order()
  {
    return _tensor.order();
  }

  public Iterator<E> iterator()
  {
    return _tensor.iterator();
  }

  public Object[] toArray()
  {
    return _tensor.toArray();
  }

  public <T> T[] toArray(T[] a)
  {
    return _tensor.toArray(a);
  }

  public boolean add(E e)
  {
    return _tensor.add(e);
  }

  public boolean remove(Object o)
  {
    return _tensor.remove(o);
  }

  public boolean containsAll(Collection<?> c)
  {
    return _tensor.containsAll(c);
  }

  public boolean addAll(Collection<? extends E> c)
  {
    return _tensor.addAll(c);
  }

  public boolean removeAll(Collection<?> c)
  {
    return _tensor.removeAll(c);
  }

  public boolean retainAll(Collection<?> c)
  {
    return _tensor.retainAll(c);
  }

  public void clear()
  {
    _tensor.clear();
  }

  public boolean equals(Object o)
  {
    return _tensor.equals(o);
  }

  public int hashCode()
  {
    return _tensor.hashCode();
  }

  public Iterable<Tensor<E>> subTensorIterable(int... fixedIndexPositions)
  {
    return _tensor.subTensorIterable(fixedIndexPositions);
  }
}
