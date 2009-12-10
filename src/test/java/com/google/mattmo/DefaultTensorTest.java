package com.google.mattmo;

import org.junit.Assert;
import org.junit.Test;


public class DefaultTensorTest
{
  /**
   * 5    6
   * <p/>
   * 7    8
   * <p/>
   * varying second index goes left to right
   * varying first index goes up to down.
   */
  private final Tensor<Integer> tensor = DefaultTensor.create(new int[]{2, 2}, 5, 6, 7, 8);


  @Test
  public void testGetValue()
  {
    Assert.assertEquals(5, tensor.get(0, 0).intValue());
    Assert.assertEquals(6, tensor.get(0, 1).intValue());
    Assert.assertEquals(7, tensor.get(1, 0).intValue());
    Assert.assertEquals(8, tensor.get(1, 1).intValue());

  }

  @Test
  public void testScalarSubTensors()
  {
    int[] scalarIndexSizes = new int[0];

    Assert.assertEquals(DefaultTensor.create(scalarIndexSizes, 5), tensor.subTensor(0, 0));
    Assert.assertEquals(DefaultTensor.create(scalarIndexSizes, 6), tensor.subTensor(0, 1));
    Assert.assertEquals(DefaultTensor.create(scalarIndexSizes, 7), tensor.subTensor(1, 0));
    Assert.assertEquals(DefaultTensor.create(scalarIndexSizes, 8), tensor.subTensor(1, 1));
  }

  @Test
  public void testVectorSubTensors()
  {
    int[] vectorIndexSizes = new int[]{2};

    Assert.assertEquals(DefaultTensor.create(vectorIndexSizes, 5, 6), tensor.subTensor(0, null));
    Assert.assertEquals(DefaultTensor.create(vectorIndexSizes, 7, 8), tensor.subTensor(1, null));
    Assert.assertEquals(DefaultTensor.create(vectorIndexSizes, 5, 7), tensor.subTensor(null, 0));
    Assert.assertEquals(DefaultTensor.create(vectorIndexSizes, 6, 8), tensor.subTensor(null, 1));
  }

  @Test
  public void testSelfSubTensor()
  {
    Assert.assertEquals(tensor, tensor.subTensor(null, null));
  }

  @Test
  public void testNumElements()
  {
    Assert.assertEquals(4, tensor.size());
  }

  @Test
  public void testIndexSizes()
  {
    Assert.assertArrayEquals(new int[]{2, 2}, tensor.indexSizes());
  }

  @Test
  public void testOrder()
  {
    Assert.assertEquals(2, tensor.order());
  }
}
