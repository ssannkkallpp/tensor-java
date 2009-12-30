package com.google.mattmo;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.mattmo.utils.IndexUtils;
import com.markit.mtk.collections.func.BinaryFunction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Tensors
{
  private Tensors(){ }

  public static <A,B,C> Tensor<C> combine(final Tensor<A> leftTensor, final Tensor<B> rightTensor, final BinaryFunction<A, B, C> binaryFunction)
  {
     return DefaultTensor.create(new IndexedGetter<C>()
    {
      @Override
      public C get(int... index)
      {
        return binaryFunction.apply(leftTensor.get(index),rightTensor.get(index));
      }
    },leftTensor.indexSizes());
  }

  public static <A,B> Tensor<B> transform(final Tensor<A> tensor, final Function<A, B> function)
  {
    return DefaultTensor.create(new IndexedGetter<B>()
    {
      @Override
      public B get(int... index)
      {
        return function.apply(tensor.get(index));
      }
    },tensor.indexSizes());
  }

  public static <A,B,C,T extends Tensor<C>> T combine(final Tensor<A> leftTensor, final Tensor<B> rightTensor, final BinaryFunction<A, B, C> binaryFunction,final Function<Tensor<C>,T> tensorExtender)
  {
     return tensorExtender.apply(DefaultTensor.create(new IndexedGetter<C>()
    {
      @Override
      public C get(int... index)
      {
        return binaryFunction.apply(leftTensor.get(index),rightTensor.get(index));
      }
    },leftTensor.indexSizes()));
  }

  public static <A,B,T extends Tensor<B>> T transform(final Tensor<A> tensor, final Function<A, B> function, final Function<Tensor<B>,T> tensorExtender )
  {
    return tensorExtender.apply(DefaultTensor.create(new IndexedGetter<B>()
    {
      @Override
      public B get(int... index)
      {
        return function.apply(tensor.get(index));
      }
    },tensor.indexSizes()));
  }

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

final class Contractor<E> implements Function<Tensor<E>,Tensor<E>>
{
  private final Map<Integer,Integer> internalMap = new HashMap<Integer, Integer>();

  public Contractor<E> contract(int i, int j)
  {
    internalMap.put(i,j);
    return this;
  }

  @Override
  public Tensor<E> apply(final Tensor<E> aTensor)
  {

    final int[] fixedPos = new int[internalMap.size()];
    final int[] fixedVal = new int[internalMap.size()];
    int i = 0;
    for (Map.Entry<Integer, Integer> entry : internalMap.entrySet())
    {
      fixedPos[i]=entry.getKey();
      fixedVal[i]=entry.getValue();
      i++;
    }

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

  public static <E> Contractor<E> create()
  {
    return new Contractor<E>();
  }

  public static void main(String[] args)
  {
    Tensor<Integer> bob;

    new Contractor<Integer>().contract(0,1).contract(1,2).apply(bob);
  }
}


