package com.google.mattmo;

import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;


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
  private final Tensor<Integer> tensor = DefaultImmutableTensor.create(new int[]{2, 2}, 5, 6, 7, 8);


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

    Assert.assertEquals(DefaultImmutableTensor.create(scalarIndexSizes, 5), tensor.contract(0, 0));
    Assert.assertEquals(DefaultImmutableTensor.create(scalarIndexSizes, 6), tensor.contract(0, 1));
    Assert.assertEquals(DefaultImmutableTensor.create(scalarIndexSizes, 7), tensor.contract(1, 0));
    Assert.assertEquals(DefaultImmutableTensor.create(scalarIndexSizes, 8), tensor.contract(1, 1));
  }

  @Test
  public void testVectorSubTensors()
  {
    int[] vectorIndexSizes = new int[]{2};

    Assert.assertEquals(DefaultImmutableTensor.create(vectorIndexSizes, 5, 6), tensor.contract(0, null));
    Assert.assertEquals(DefaultImmutableTensor.create(vectorIndexSizes, 7, 8), tensor.contract(1, null));
    Assert.assertEquals(DefaultImmutableTensor.create(vectorIndexSizes, 5, 7), tensor.contract(null, 0));
    Assert.assertEquals(DefaultImmutableTensor.create(vectorIndexSizes, 6, 8), tensor.contract(null, 1));
  }

  @Test
  public void testSelfSubTensor()
  {
    Assert.assertEquals(tensor, tensor.contract(null, null));
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

  @Test
  public void testProject()
  {
    Tensor<Integer> projectedTensor = tensor.project(new int[]{0},new int[]{2});

    Assert.assertEquals(5, projectedTensor.get(0,0,0).intValue());
    Assert.assertEquals(6, projectedTensor.get(0,0,1).intValue());
    Assert.assertEquals(7, projectedTensor.get(0,1,0).intValue());
    Assert.assertEquals(8, projectedTensor.get(0,1,1).intValue());
    Assert.assertEquals(5, projectedTensor.get(1,0,0).intValue());
    Assert.assertEquals(6, projectedTensor.get(1,0,1).intValue());
    Assert.assertEquals(7, projectedTensor.get(1,1,0).intValue());
    Assert.assertEquals(8, projectedTensor.get(1,1,1).intValue());

  }


  @Test
  public void testIterable()
  {
    Iterable<Integer> iterable = Arrays.asList(5,6,7,8);
        
    Assert.assertTrue(Iterables.elementsEqual(iterable,tensor));
    Assert.assertTrue(Iterables.elementsEqual(iterable,tensor));

  }
}
