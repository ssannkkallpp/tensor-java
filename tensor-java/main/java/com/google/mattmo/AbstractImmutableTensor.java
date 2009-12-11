package com.google.mattmo;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.markit.mtk.collections.func.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.google.mattmo.ArrayUtils.newArray;

/**
 *
 * Abstract implementation of a Tensor, the only methods that need to be implemented are the get method and the indexSizes
 * Method.
 */
public abstract class AbstractImmutableTensor<E> implements Tensor<E>
{
  private final int[] _indexSizes;
  private final int _order;
  private final int _size;

  protected AbstractImmutableTensor(int... indexSizes)
  {
    _indexSizes = Arrays.copyOf(indexSizes,indexSizes.length); //take a defensive copy...
    _order = _indexSizes.length;
    _size = IndexUtils.calcNumElements(_indexSizes);
  }

  @Override
  public abstract E get(int... tensorIndex);

  @Override
  public int[] indexSizes()
  {
    return _indexSizes;
  }

  @Override
  public int order()
  {
    return _order;
  }

  @Override
  public int size()
  {
    return _size;
  }

  @Override
  public Iterator<E> iterator()
  {
    final int[] basis = IndexUtils.createBasis(indexSizes());
    final int size = size();

    return new AbstractIterator<E>()
    {
      private int count = 0;

      @Override
      protected E computeNext()
      {
        if (count < size)
          return get(IndexUtils.projectIndex(count++, basis));
        return endOfData();
      }
    };
  }

//  @Override
//  public Tensor<E> contract(Integer... index)
//  {
//    if(index.length!=indexSizes().length)
//      throw new IllegalArgumentException("Index must have length = "+index.length);
//
//    IndexInfo indexInfo = IndexUtils.nonNullIndexInfo(index);
//
//    return subTensor(indexInfo.indexPositions(), indexInfo.indexValues());
//  }

  @Override
  public Tensor<E> contract(final int[] fixedPos,final int[] fixedVal)
  {
    final int[] subIndexSizes = IndexUtils.remove(indexSizes(),fixedPos);
    return new AbstractImmutableTensor<E>(subIndexSizes)
    {
      @Override
      public E get(int... tensorIndex)
      {
        int[] fullIndex = IndexUtils.add(tensorIndex,fixedPos,fixedVal);
        return AbstractImmutableTensor.this.get(fullIndex);
      }
    };
  }

  @Override
  public Iterable<Tensor<E>> contractedTensorIterable(final int... iteratingIndexPositions)
  {
    if (iteratingIndexPositions.length >= indexSizes().length)
      throw new IllegalArgumentException();

    final int[] iteratingIndexSizes = IndexUtils.retain(indexSizes(), iteratingIndexPositions);
    final int[] basis = IndexUtils.createBasis(iteratingIndexSizes);
    final int numElements = IndexUtils.calcNumElements(iteratingIndexSizes);

    return new Iterable<Tensor<E>>()
    {
      @Override
      public Iterator<Tensor<E>> iterator()
      {
        return new AbstractIterator<Tensor<E>>()
        {
          private int count = 0;

          @Override
          protected Tensor<E> computeNext()
          {
            if (count < numElements)
              return contract(iteratingIndexPositions, IndexUtils.projectIndex(count++, basis));
            return endOfData();
          }
        };
      }
    };
  }



  @Override
  public String toString()
  {

    E[] internalRep = newArray(this, this.size());

    return "DefaultImmutableTensor{" +
        "flattenedTensor =" + (Arrays.asList(internalRep)) +
        ", index sizes =" + Arrays.toString(indexSizes()) +
        '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof AbstractImmutableTensor)) return false;

    AbstractImmutableTensor<?> that = (AbstractImmutableTensor<?>) o;

    E[] thisArray = newArray(this, this.size());

    Object[] thatArray = newArray(that, that.size());

    if (!Arrays.equals(this.indexSizes(), that.indexSizes())) return false;
    if (!Arrays.equals(thisArray, thatArray)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    E[] elements = newArray(this, this.size());
    int result = Arrays.hashCode(elements);
    result = 31 * result + Arrays.hashCode(this.indexSizes());
    return result;
  }

  @Override
  public Tensor<E> project(final int[] projectionIndexPositions, final int[] projectionIndexSizes)
  {

    final int[] newIndexSizes = IndexUtils.add(indexSizes(),projectionIndexPositions,projectionIndexSizes);

    return new AbstractImmutableTensor<E>(newIndexSizes)
    {
      @Override
      public E get(int... tensorIndex)
      {
        int[] innerIndex = IndexUtils.remove(tensorIndex,projectionIndexPositions);
        return AbstractImmutableTensor.this.get(innerIndex);
      }
    };
  }

  @Override
  public boolean isEmpty()
  {
    return Iterables.isEmpty(this);
  }

  @Override
  public boolean contains(Object o)
  {
    return Iterables.contains(this,o);
  }

  @Override
  public boolean containsAll(final Collection<?> c)
  {
    final Collection<Object> bob = new ArrayList<Object>();

    Actions.<Collection<Object>,Object>addToCollectionWhere(this,bob,new Predicate<Object>()
    {
      @Override
      public boolean apply(Object o)
      {
        return !bob.contains(o) && c.contains(o);
      }
    });

    return bob.size()==c.size();
  }

  @Override
  public E[] toArray()
  {
    return newArray(this,size());
  }

  @Override
  public <T> T[] toArray(T[] a)
  {

    return (T[]) newArray(this,size());
  }

  @Override
  public boolean add(E e)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }
}
