package com.google.mattmo;

import com.google.common.base.Function;
import com.markit.mtk.collections.Pair;
import com.markit.mtk.collections.Tuple;
import com.markit.mtk.collections.func.*;


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

  public static Tensor<Double> pow(final Tensor<Double> tensor,final double factor)
  {
    return TensorOperations.applyFunction(tensor,new Function<Double, Double>()
    {
      @Override
      public Double apply(Double aDouble)
      {
        return Math.pow(aDouble,factor);
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

  public static Tensor<Double> mean(final Tensor<Double> tensor, int... averagingIndices)
  {
    Iterable<Tensor<Double>> tensorIterable = tensor.contractedTensorIterable(averagingIndices);

    int[] retIndexSizes = IndexUtils.remove(tensor.indexSizes(),averagingIndices);

    Tensor<Double> initialTensor = DefaultImmutableTensor.create(retIndexSizes, 0.0);
    Tuple<Tensor<Double>,Integer> initialTuple = Pair.create(initialTensor,0);

    Tuple<Tensor<Double>,Integer> sum =  Actions.reduce(tensorIterable,new BinaryFunction<Tuple<Tensor<Double>, Integer>, Tensor<Double>, Tuple<Tensor<Double>, Integer>>()
    {
      @Override
      public Tuple<Tensor<Double>, Integer> apply(Tuple<Tensor<Double>, Integer> leftItem, Tensor<Double> rightItem)
      {
        return Pair.create(add(leftItem.getFirst(),rightItem),leftItem.getSecond()+1);
      }
    },initialTuple);

    return scale(sum.getFirst(),1/sum.getSecond());     
  }

  public static Tensor<Double> sqrt(Tensor<Double> tensor)
  {
    return TensorOperations.applyFunction(tensor,new Function<Double, Double>()
    {
      @Override
      public Double apply(Double aDouble)
      {
        return Math.sqrt(aDouble);
      }
    });
  }

  public static Tensor<Double> var(Tensor<Double> tensor, int... averagingIndices)
  {
    return nthMoment(2,tensor,averagingIndices);
  }

  public static Tensor<Double> stDev(Tensor<Double> tensor, int... averagingIndices)
  {
    return sqrt(var(tensor,averagingIndices));
  }


  public static Tensor<Double> nthMoment(final int n, final Tensor<Double> tensor, final int... averagingIndices)
  {
    final Tensor<Double> mean = mean(tensor,averagingIndices);

    final int[] indexSizes = tensor.indexSizes();
    final int[] averagingIndicesSizes = IndexUtils.retain(indexSizes,averagingIndices);

    final Tensor<Double> projectedAverage = mean.project(averagingIndices,averagingIndicesSizes);


    Iterable<Tensor<Double>> tensorIterable = tensor.contractedTensorIterable(averagingIndices);

    int[] retIndexSizes = IndexUtils.remove(tensor.indexSizes(),averagingIndices);

    Tensor<Double> initialTensor = DefaultImmutableTensor.create(retIndexSizes, 0.0);
    Tuple<Tensor<Double>,Integer> initialTuple = Pair.create(initialTensor,0);

    Tuple<Tensor<Double>,Integer> sum = Actions.reduce(tensorIterable,new BinaryFunction<Tuple<Tensor<Double>, Integer>, Tensor<Double>, Tuple<Tensor<Double>, Integer>>()
    {
      @Override
      public Tuple<Tensor<Double>, Integer> apply(Tuple<Tensor<Double>, Integer> leftItem, Tensor<Double> rightTensor)
      {
        return Pair.create(add(leftItem.getFirst(), pow(subtract(rightTensor,projectedAverage),n)),leftItem.getSecond()+1);
      }
    },initialTuple);


    return scale(sum.getFirst(),1/sum.getSecond());
  }

  public static Tensor<Double> tensorMult(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor,final int[] leftMultiplyingIndices,final int[] rightMultiplyingIndices)
  {
    if(rightMultiplyingIndices.length!=leftMultiplyingIndices.length)
      throw new IllegalArgumentException("left and right multplying indices must be the same length.");

    for (int i = 0 ;i<leftMultiplyingIndices.length;i++)
    {
      if(leftTensor.indexSizes()[leftMultiplyingIndices[i]]!=rightTensor.indexSizes()[i])
        throw new IllegalArgumentException("index sizes specified by the multiplying indices are not the same in each tensor");

      if(leftTensor.indexSizes()[rightMultiplyingIndices[i]]!=rightTensor.indexSizes()[i])
        throw new IllegalArgumentException("index sizes specified by the multiplying indices are not the same in each tensor");
    }

    final int[] indexSizes = IndexUtils.remove(leftTensor.indexSizes(),leftMultiplyingIndices);

    return new AbstractImmutableTensor<Double>(indexSizes)
    {
      @Override
      public Double get(int... tensorIndex)
      {
        int[] leftFixedIndexPositions = removeFromIndexPositions(leftMultiplyingIndices,indexSizes().length);//not the multiplying ones.
        int[] rightFixedIndexPositions = removeFromIndexPositions(rightMultiplyingIndices,indexSizes().length);//not the multiplying ones.

        Tensor<Double> contractedLeftTensor = leftTensor.contract(leftFixedIndexPositions,tensorIndex);
        Tensor<Double> contractedRightTensor = rightTensor.contract(rightFixedIndexPositions,tensorIndex);

        Tensor<Double> elementWiseMult = multiply(contractedLeftTensor,contractedRightTensor);

        return Actions.reduce(elementWiseMult,new SimpleReducer<Double>()
        {
          @Override
          public Double apply(Double leftItem, Double rightItem)
          {
            return leftItem + rightItem;
          }
        });
      }
    };
  }

  private static int[] removeFromIndexPositions(int[] indicesToRemove, int length)
  {
    int[] retVal = new int[length-indicesToRemove.length];

    for(int i = 0,j=0,k=0;i<length;i++)
    {
      if(i==indicesToRemove[j])
        j++;
      else
        retVal[k++] = i;
    }

    return retVal;
  }

  public static void main(String[] args)
  {
    Tensor<Double> bob = new AbstractImmutableTensor<Double>(2,2)
    {
      @Override
      public Double get(int... tensorIndex)
      {
        if(tensorIndex[0]==tensorIndex[1])
          return 1.0;
        else
          return 0.0;
      }
    };

    Tensor<Double> bobbob = tensorMult(bob,bob,new int[]{0},new int[]{1});
    bobbob.get(0,0);

    int y = 8;
  }
}
