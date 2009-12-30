package com.google.mattmo;

import com.google.common.base.Function;
import com.markit.mtk.collections.func.BinaryFunction;

import java.util.Collection;
import java.util.Iterator;

public class ForwardingTensor<E> implements Tensor<E>
{
  private final Tensor<E> _tensor;

  public ForwardingTensor(Tensor<E> _tensor)
  {
    this._tensor = _tensor;
  }

  @Override
  public int[] indexSizes()
  {
    return _tensor.indexSizes();
  }

  @Override
  public int order()
  {
    return _tensor.order();
  }
  @Override
  public Tensor<E> contract(int[] fixedPos, int[] fixedVal)
  {
    return _tensor.contract(fixedPos, fixedVal);
  }
  @Override
  public <P extends Tensor<E>> Iterable<P> contractedTensorIterable(int... iteratingIndexPositions)
  {
    return _tensor.contractedTensorIterable(iteratingIndexPositions);
  }
  @Override
  public Tensor<E> project(int[] projectionIndexPositions, int[] projectionIndexSizes)
  {
    return _tensor.project(projectionIndexPositions, projectionIndexSizes);
  }
  @Override
  public Tensor<E> apply(Function<E, E> eeFunction)
  {
    return _tensor.apply(eeFunction);
  }
  @Override
  public Tensor<E> apply(Tensor<E> aTensor, BinaryFunction<E, E, E> eeeBinaryFunction)
  {
    return _tensor.apply(aTensor, eeeBinaryFunction);
  }
  @Override
  public Tensor<E> apply(E value, BinaryFunction<E, E, E> eeeBinaryFunction)
  {
    return _tensor.apply(value, eeeBinaryFunction);
  }
  @Override
  public int size()
  {
    return _tensor.size();
  }
  @Override
  public boolean isEmpty()
  {
    return _tensor.isEmpty();
  }
  @Override
  public boolean contains(Object o)
  {
    return _tensor.contains(o);
  }

  public Iterator<E> iterator()
  {
    return _tensor.iterator();
  }
  @Override
  public Object[] toArray()
  {
    return _tensor.toArray();
  }
  @Override
  public <T> T[] toArray(T[] a)
  {
    return _tensor.toArray(a);
  }
  @Override
  public boolean add(E e)
  {
    return _tensor.add(e);
  }
  @Override
  public boolean remove(Object o)
  {
    return _tensor.remove(o);
  }
  @Override
  public boolean containsAll(Collection<?> c)
  {
    return _tensor.containsAll(c);
  }
  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    return _tensor.addAll(c);
  }
  @Override
  public boolean removeAll(Collection<?> c)
  {
    return _tensor.removeAll(c);
  }
  @Override
  public boolean retainAll(Collection<?> c)
  {
    return _tensor.retainAll(c);
  }
  @Override
  public void clear()
  {
    _tensor.clear();
  }
  @Override
  public boolean equals(Object o)
  {
    return _tensor.equals(o);
  }
  @Override
  public int hashCode()
  {
    return _tensor.hashCode();
  }
  @Override
  public E get(int... index)
  {
    return _tensor.get(index);
  }
}
