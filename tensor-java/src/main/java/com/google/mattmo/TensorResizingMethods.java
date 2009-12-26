package com.google.mattmo;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

/**
 * TensorResizingMethods:
 *
 * All these methods delegate to the tensor that is passed to the method, a caching strategy should be supplied
 * as a static method on the tensor implementation.
 *
 * @author matthew.gretton
 */
public final class TensorResizingMethods
{
  private TensorResizingMethods(){};
  
  public static <E> Tensor<E> contract(final Tensor<E> aTensor, final int[] fixedPos,final int[] fixedVal)
  {
    final int[] subIndexSizes = IndexUtils.remove(aTensor.indexSizes(),fixedPos);

    return DefaultTensor.create(new IndexedGetter<E>()
    {
      @Override
      public E get(int... index)
      {
         int[] fullIndex = IndexUtils.add(index,fixedPos,fixedVal);
        return aTensor.get(fullIndex);
      }
    }, subIndexSizes);
  }

  public static <E> Iterable<Tensor<E>> contractedTensorIterable(final Tensor<E> aTensor, final int... iteratingIndexPositions)
  {
   if (iteratingIndexPositions.length >= aTensor.indexSizes().length)
      throw new IllegalArgumentException();

    final int[] iteratingIndexSizes = IndexUtils.retain(aTensor.indexSizes(), iteratingIndexPositions);
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
              return contract(aTensor,iteratingIndexPositions, IndexUtils.projectIndex(count++, basis));
            return endOfData();
          }
        };
      }
    };
  }

  public static <E> Tensor<E> project(final Tensor<E> aTensor,final int[] projectionIndexPositions, final int[] projectionIndexSizes)
  {
    final int[] newIndexSizes = IndexUtils.add(aTensor.indexSizes(),projectionIndexPositions,projectionIndexSizes);

    return DefaultTensor.create(new IndexedGetter<E>()
    {
      @Override
      public E get(int... index)
      {
         int[] innerIndex = IndexUtils.remove(index,projectionIndexPositions);
        return aTensor.get(innerIndex);
      }
    }, newIndexSizes);
  } 
}
