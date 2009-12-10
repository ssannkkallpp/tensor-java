package com.google.mattmo;

import com.google.common.base.Function;

public final class TensorOperations
{
  private TensorOperations()
  {
  }

  public static <A> Tensor<A> applyBinaryFunction(final Tensor<A> leftTensor, final Tensor<A> rightTensor, final BinaryFunction<A, A, A> function)
  {
    return new ForwardingTensor<A>(leftTensor)
    {
      @Override
      public A get(int... tensorIndex)
      {
        return function.apply(super.get(tensorIndex), rightTensor.get(tensorIndex));
      }
    };
  }

  public static <A> Tensor<A> applyFunction(final Tensor<A> tensor, final Function<A, A> function)
  {
    return new ForwardingTensor<A>(tensor)
    {
      @Override
      public A get(int... tensorIndex)
      {
        return function.apply(super.get(tensorIndex));
      }
    };
  }
}


