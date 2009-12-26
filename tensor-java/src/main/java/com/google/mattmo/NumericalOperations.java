package com.google.mattmo;

import com.google.common.base.Function;
import com.markit.mtk.collections.Pair;
import com.markit.mtk.collections.Tuple;
import com.markit.mtk.collections.func.*;

import static com.google.mattmo.Tensors.combine;
import static com.google.mattmo.Tensors.transform;
import static com.google.mattmo.TensorResizingMethods.*;


public final class NumericalOperations
{
  private NumericalOperations(){};

  public static Tensor<Double> add(final Tensor<Double> leftTensor, final Tensor<Double> rightTensor)
  {
    return combine(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
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
    return combine(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
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
    return combine(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
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
    return combine(leftTensor, rightTensor, new BinaryFunction<Double, Double, Double>()
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
    return transform(tensor,new Function<Double, Double>()
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
    return transform(tensor, new Function<Double, Double>()
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
    Iterable<Tensor<Double>> tensorIterable = contractedTensorIterable(tensor,averagingIndices);

    int[] retIndexSizes = IndexUtils.remove(tensor.indexSizes(),averagingIndices);

    Tensor<Double> initialTensor = DefaultTensor.create(new IndexedGetter<Double>()
    {
      @Override
      public Double get(int... index)
      {
        return 0.0;
      }
    }, retIndexSizes);

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
    return transform(tensor,new Function<Double, Double>()
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

    final Tensor<Double> projectedAverage = project(mean,averagingIndices,averagingIndicesSizes);


    Iterable<Tensor<Double>> tensorIterable = contractedTensorIterable(tensor,averagingIndices);

    int[] retIndexSizes = IndexUtils.remove(tensor.indexSizes(),averagingIndices);

    Tensor<Double> initialTensor =  DefaultTensor.create(new IndexedGetter<Double>()
    {
      @Override
      public Double get(int... index)
      {
        return 0.0;
      }
    }, retIndexSizes);

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

    return DefaultTensor.create(new IndexedGetter<Double>()
    {
      @Override
      public Double get(int... index)
      {
        int[] leftFixedIndexPositions = removeFromIndexPositions(leftMultiplyingIndices,leftTensor.indexSizes().length);//not the multiplying ones.
        int[] rightFixedIndexPositions = removeFromIndexPositions(rightMultiplyingIndices,rightTensor.indexSizes().length);//not the multiplying ones.

        Tensor<Double> contractedLeftTensor = contract(leftTensor,leftFixedIndexPositions,index);
        Tensor<Double> contractedRightTensor = contract(rightTensor,rightFixedIndexPositions,index);

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
    }, indexSizes);
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
}
