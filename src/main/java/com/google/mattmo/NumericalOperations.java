package com.google.mattmo;

import com.google.common.base.Function;


public final class NumericalOperations
{
  private NumericalOperations()
  {
  }

  ;

  public static Tensor<Double> add(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor)
  {
    return TensorOperations.applyBinaryFunction(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
    {
      @Override
      public Double apply(Double leftItem, Double rightItem)
      {
        return leftItem + rightItem;
      }
    });
  }

  public static Tensor<Double> subtract(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor)
  {
    return TensorOperations.applyBinaryFunction(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
    {
      @Override
      public Double apply(Double leftItem, Double rightItem)
      {
        return leftItem - rightItem;
      }
    });
  }

  public static Tensor<Double> multiply(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor)
  {
    return TensorOperations.applyBinaryFunction(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
    {
      @Override
      public Double apply(Double leftItem, Double rightItem)
      {
        return leftItem * rightItem;
      }
    });
  }

  public static Tensor<Double> divide(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor)
  {
    return TensorOperations.applyBinaryFunction(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
    {
      @Override
      public Double apply(Double leftItem, Double rightItem)
      {
        return leftItem / rightItem;
      }
    });
  }

  public static Tensor<Double> scale(final Tensor<Double> tensor, final double scalar)
  {
    return TensorOperations.applyFunction(tensor, new Function<Double, Double>()
    {
      @Override
      public Double apply(Double item)
      {
        return scalar * item;
      }
    });
  }


}
