package com.google.mattmo;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 */
final class DefaultTensor<E> implements Tensor<E>
{
  private final E[] _internalTensorRepresentation;
  private final int[] _indexSizes;
  private final int order;
  private final int[] _fixedIndexPositions;
  private final int[] _fixedIndexValues;

  DefaultTensor(E[] internalTensorRepresentation,
                int[] indexSizes,
                int order,
                int[] fixedIndexPositions,
                int[] fixedIndexValues)
  {
    this._internalTensorRepresentation = internalTensorRepresentation;
    this._indexSizes = indexSizes;
    this.order = order;
    this._fixedIndexPositions = fixedIndexPositions;
    this._fixedIndexValues = fixedIndexValues;
  }

  private void checkIndex(int... index)
  {
    int[] indexSizes = indexSizes();

    if (index.length != indexSizes.length)
      throw new IllegalArgumentException("Index of incorrect length");

    int ind;
    for (int i = 0; i < index.length; i++)
    {
      ind = index[i];
      if (ind < 0 && ind >= indexSizes[i])
        throw new IndexOutOfBoundsException();
    }
  }

  private void checkIndex(Integer... index)
  {
    int[] indexSizes = indexSizes();
    if (index.length != indexSizes.length)
      throw new IllegalArgumentException("Index of incorrect length");

    Integer ind;
    for (int i = 0; i < index.length; i++)
    {
      ind = index[i];
      if (ind != null && ind < 0 && ind >= indexSizes[i])
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public E get(int... tensorIndex)
  {
    checkIndex(tensorIndex);
    int[] basis = IndexUtils.createBasis(_indexSizes);
    int[] fullIndex = IndexUtils.generateFullIndex(tensorIndex, _indexSizes.length, _fixedIndexPositions, _fixedIndexValues);
    int flattenedIndex = IndexUtils.flattenTensorIndex(fullIndex, basis);
    return _internalTensorRepresentation[flattenedIndex];
  }

  @Override
  public Iterator<E> iterator()
  {
    final int[] basis = IndexUtils.createBasis(indexSizes());
    final int numElements = size();

    return new AbstractIterator<E>()
    {
      private int count = 0;

      @Override
      protected E computeNext()
      {
        if (count < numElements)
          return get(IndexUtils.projectIndex(count++, basis));
        return endOfData();
      }
    };
  }

  @Override
  public Object[] toArray()
  {
    return IndexUtils.newArray(this, this.size());
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    return (T[]) IndexUtils.newArray(this, this.size());
  }

  @Override
  public boolean add(E e)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    for (E e : this)
    {
      if (c.contains(e))
        c.remove(e);

      if (c.isEmpty())
        return true;
    }

    return false;
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Tensor<E> subTensor(Integer... tensorIndex)
  {
    checkIndex(tensorIndex);

    FixedIndexInfo newFixedIndexInfo = IndexUtils.generateFixedIndicesInfo(tensorIndex);

    return subTensor(newFixedIndexInfo.indexPositions(), newFixedIndexInfo.indexValues());
  }

  private Tensor<E> subTensor(int[] fixedIndexPositions, int[] fixedIndexValues)
  {
    FixedIndexInfo fixedIndicesInfo = IndexUtils.mergeFixedIndices(_fixedIndexPositions, _fixedIndexValues,
        fixedIndexPositions, fixedIndexValues);

    return new DefaultTensor<E>(_internalTensorRepresentation, _indexSizes, order, fixedIndicesInfo.indexPositions(),
        fixedIndicesInfo.indexValues());
  }

  @Override
  public Iterable<Tensor<E>> subTensorIterable(final int... iteratingIndexPositions)
  {
    if (iteratingIndexPositions.length >= _indexSizes.length)
      throw new IllegalArgumentException();

    final int[] iteratingIndexSizes = IndexUtils.retain(_indexSizes, iteratingIndexPositions);
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
              return subTensor(iteratingIndexPositions, IndexUtils.projectIndex(count++, basis));
            return endOfData();
          }
        };
      }
    };
  }

  @Override
  public int size()
  {
    return IndexUtils.calcNumElements(indexSizes());
  }

  @Override
  public boolean isEmpty()
  {
    return Iterables.isEmpty(this);
  }

  @Override
  public boolean contains(Object o)
  {
    return Iterables.contains(this, o);
  }

  @Override
  public int[] indexSizes()
  {
    return IndexUtils.remove(_indexSizes, _fixedIndexPositions);
  }

  @Override
  public int order()
  {
    return _indexSizes.length - _fixedIndexPositions.length;
  }

  @Override
  public String toString()
  {

    E[] internalRep = IndexUtils.newArray(this, this.size());

    return "DefaultTensor{" +
        "_internalTensorRepresentation=" + (Arrays.asList(internalRep)) +
        ", _indexSizes=" + Arrays.toString(indexSizes()) +
        '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof DefaultTensor)) return false;

    DefaultTensor<?> that = (DefaultTensor<?>) o;

    E[] thisArray = IndexUtils.newArray(this, this.size());

    Object[] thatArray = IndexUtils.newArray(that, that.size());

    if (!Arrays.equals(this.indexSizes(), that.indexSizes())) return false;
    if (!Arrays.equals(thisArray, thatArray)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    E[] elements = IndexUtils.newArray(this, this.size());
    int result = Arrays.hashCode(elements);
    result = 31 * result + Arrays.hashCode(this.indexSizes());
    return result;
  }


  public Tensor<E> cache()
  {
    E[] internalRep = IndexUtils.newArray(this, this.size());
    @SuppressWarnings("unchecked")
    Tensor<E> retVal = DefaultTensor.create(this.indexSizes(), internalRep);
    return retVal;
  }


  /**
   * ****************
   * Factory Methods *
   * *****************
   */


  public static <E> Tensor<E> create(int[] indexSizes, E... values)
  {
    E[] internalRep = Arrays.copyOf(values, values.length);
    return internalCreate(indexSizes, internalRep);
  }

  public static <E> Tensor<E> create(int[] indexSizes, Iterable<? extends E> elements)
  {
    @SuppressWarnings("unchecked")
    List<E> list;

    if (elements instanceof ImmutableList)
    {
      list = (List<E>) elements;
    }
    else
    {
      list = Lists.newArrayList(elements);
    }
    @SuppressWarnings("unchecked")
    Tensor<E> retVal = internalCreate(indexSizes, list.toArray((E[]) new Object[list.size()]));
    return retVal;
  }

  private static <E> Tensor<E> internalCreate(int[] indexSizes, E[] internalRep)
  {
    return new DefaultTensor<E>(internalRep, indexSizes, indexSizes.length, new int[0], new int[0]);
  }
}


