package com.google.mattmo;

import com.google.common.base.Function;
import com.markit.mtk.collections.func.BinaryFunction;

public final class Tensors
{
  private Tensors(){ }

  public static <A> Tensor<A> combine(final Tensor<A> leftTensor, final Tensor<A> rightTensor, final BinaryFunction<A, A, A> binaryFunction)
  {
     return DefaultTensor.create(new IndexedGetter<A>()
    {
      @Override
      public A get(int... index)
      {
        return binaryFunction.apply(leftTensor.get(index),rightTensor.get(index));
      }
    },leftTensor.indexSizes());
  }

  public static <A> Tensor<A> transform(final Tensor<A> tensor, final Function<A, A> function)
  {
    return DefaultTensor.create(new IndexedGetter<A>()
    {
      @Override
      public A get(int... index)
      {
        return function.apply(tensor.get(index));
      }
    },tensor.indexSizes());
  }
}


