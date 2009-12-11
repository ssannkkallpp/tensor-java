package com.google.mattmo;

import com.google.common.collect.Iterables;

import java.util.Arrays;

/**
 *
 *
 */
public final class DefaultImmutableTensor<E> extends AbstractImmutableTensor<E>
{
  private final E[] _spurlanatedTensor;

  DefaultImmutableTensor(E[] spurlanatedTensor, int[] indexSizes)
  {
    super(indexSizes);
    _spurlanatedTensor = spurlanatedTensor;
  }

  @Override
  public E get(int... tensorIndex)
  {
    checkIndex(tensorIndex);
    int[] basis = IndexUtils.createBasis(indexSizes());
    int flattenedIndex = IndexUtils.flattenTensorIndex(tensorIndex, basis);
    return _spurlanatedTensor[flattenedIndex];
  }

  @SuppressWarnings("unchecked")
  public Tensor<E> cacheTensor(Tensor<E> tensor)
  {
    return DefaultImmutableTensor.create(tensor.indexSizes(),tensor);
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
  

  //Factory Methods
  public static <E> DefaultImmutableTensor<E> create(int[] indexSizes, E value)
  {
    int size = IndexUtils.calcNumElements(indexSizes);
    @SuppressWarnings("unchecked")
    E[] flattenedTensor = (E[]) new Object[size];
    Arrays.fill(flattenedTensor,value);
    return new DefaultImmutableTensor<E>(flattenedTensor,indexSizes);
  }


  public static <E> DefaultImmutableTensor<E> create(int[] indexSizes, E ... values)
  {
    E[] flattenedTensor = Arrays.copyOf(values,values.length);
    return new DefaultImmutableTensor<E>(flattenedTensor,indexSizes);
  }

  public static <E> DefaultImmutableTensor<E> create(int[] indexSizes, Iterable<E> values)
  {
    E[] flattenedTensor = ArrayUtils.newArray(values,Iterables.size(values));
    return new DefaultImmutableTensor<E>(flattenedTensor,indexSizes);
  }

  public static class Builder<E>
  {
    private final int[] _indexSizes;
    private final E[] _pancakeTensor;

    @SuppressWarnings("unchecked")
    public Builder(int[] indexSizes)
    {
      _indexSizes = indexSizes;


      int size = IndexUtils.calcNumElements(_indexSizes);

       _pancakeTensor = (E[]) new Object[size];
    }

    Tensor<E> build()
    {
      return new DefaultImmutableTensor<E>(_pancakeTensor,_indexSizes);
    }

    Builder<E> set(E element,int... index)
    {
      _pancakeTensor[IndexUtils.flattenTensorIndex(index,_indexSizes)] = element;
      return this;
    }
  }
}



