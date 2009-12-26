package com.google.mattmo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import static com.google.mattmo.TensorResizingMethods.contractedTensorIterable;

public final class MatrixTensor extends ForwardingTensor<Double>
{
  private MatrixTensor(Tensor<Double> aDoubles)
  {
    super(aDoubles);
  }

  public Double get(int row,int col)
  {
    return get(new int[]{row,col});
  }

  public static MatrixTensor create(Double[][] matrixRep)
  {
    return new MatrixTensor(DefaultTensor.create(new MatrixGetter(matrixRep.clone()),new int[]{2,2}));
  }

  public static MatrixTensor create(Tensor<Double> aDoubleTensor)
  {
    int[] indexSizes = aDoubleTensor.indexSizes();

    Double[][] matrixRep = new Double[indexSizes[1]][indexSizes[0]];

    Iterable<Double[]> iter = Iterables.transform(contractedTensorIterable(aDoubleTensor,1),new Function<Tensor<Double>, Double[]>()
    {
      @Override
      public Double[] apply(Tensor<Double> aDoubleTensor)
      {
        return aDoubleTensor.toArray(new Double[aDoubleTensor.size()]);
      }
    });

    int i = 0;

    for (Double[] doubles : iter)
      matrixRep[i++] = doubles;

    return create(matrixRep);
  }

  public final static class MatrixGetter implements IndexedGetter<Double>
  {
    private final Double[][] arrayRepresentation;

    private MatrixGetter(Double[][] aArrayRepresentation)
    {
      arrayRepresentation = aArrayRepresentation;
    }

    @Override
    public Double get(int... index)
    {
      return arrayRepresentation[index[0]][index[1]];
    }

    public static Builder getBuilder(int rowSize,int colSize)
    {
      return new Builder(new Double[colSize][rowSize]);
    }

    public final static class Builder
    {
      private final Double[][] _arrayRepresentation;

      public Builder(Double[][] aArrayRepresentation)
      {
        _arrayRepresentation = aArrayRepresentation;
      }

      public Builder set(double d,int row,int col)
      {
        _arrayRepresentation[col][row] = d;
        return this;
      }

      MatrixGetter build()
      {
        return new MatrixGetter(_arrayRepresentation);
      }
    }
  }
}




