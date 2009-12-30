package com.google.mattmo;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.mattmo.utils.IndexUtils;
import com.markit.mtk.collections.func.Actions;
import com.markit.mtk.collections.func.BinaryFunction;
import com.markit.mtk.collections.func.Reducer;
import com.markit.mtk.collections.func.SimpleReducer;

import java.util.*;
import static com.google.mattmo.utils.ArrayUtils.newArray;

/**
 *
 * Abstract implementation of a Tensor, the only methods that need to be implemented are the get method and the indexSizes
 * Method.
 */
public final class DefaultTensor<E> implements Tensor<E>
{
  private final IndexedGetter<E> _indexedGetter;
  private final ImmutableList<Integer> _indexSizes;

  private DefaultTensor(IndexedGetter<E> aIndexedGetter,
                        ImmutableList<Integer> aIndexSizes)
  {
    _indexedGetter = aIndexedGetter;
    _indexSizes = aIndexSizes;
  }

  @Override
  public E get(Integer... tensorIndex)
  {
    return _indexedGetter.get(tensorIndex);
  }

  @Override
  public ImmutableList<Integer> indexSizes()
  {
    return _indexSizes;
  }

  private int size()
  {
    return Actions.reduce(_indexSizes,new SimpleReducer<Integer>()
    {
      @Override
      public Integer apply(Integer leftItem, Integer rightItem)
      {
        return leftItem*rightItem;
      }
    });
  }

  @Override
  public Iterator<E> iterator()
  {
    final int[] basis = IndexUtils.createBasis(_indexSizes);
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

  @Override
  public String toString()
  {

    return "DefaultTensor{" +
        "flattenedTensor=" + Iterables.toString(this) +
        ", indexSizes=" + _indexSizes +
        '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof Tensor)) return false;

    DefaultTensor<?> that = (DefaultTensor<?>) o;

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

  public static <E> DefaultTensor<E> create(IndexedGetter<E> aIndexedGetter,int[] aIndexSizes)

  {return new DefaultTensor<E>(aIndexedGetter, aIndexSizes);}

  public final static <E> DefaultTensor<E> create(Tensor<E> aTensor,Class<E> clazz)
  {
    E[] tensorRep = Iterables.toArray(aTensor,clazz);

    int[] indexSizes = aTensor.indexSizes();
    return DefaultTensor.create(new DefaultIndexGetter<E>(tensorRep,IndexUtils.createBasis(indexSizes)),indexSizes);
  }
}

