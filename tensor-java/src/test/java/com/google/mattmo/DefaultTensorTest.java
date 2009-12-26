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
  private final Tensor<Integer> tensor = DefaultTensor.create(new IndexedGetter<Integer>()
   {
     @Override
     public Integer get(int... index)
     {
       int i = index[0];
       int j = index[1];
       if(i==0 && j==0)
         return 5;
       else if(i==0 && j==1)
         return 6;
       else if(i==1 && j==0)
         return 7;
       else if(i==1 && j==1)
         return 8;
       else
        throw new IllegalArgumentException();
     }
   },new int[]{2,2});


  @Test
  public void testGetValue()
  {
    Assert.assertEquals(5, tensor.get(0, 0).intValue());
    Assert.assertEquals(6, tensor.get(0, 1).intValue());
    Assert.assertEquals(7, tensor.get(1, 0).intValue());
    Assert.assertEquals(8, tensor.get(1, 1).intValue());

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
  public void testIterable()
  {
    Iterable<Integer> iterable = Arrays.asList(5,6,7,8);
        
    Assert.assertTrue(Iterables.elementsEqual(iterable,tensor));
    Assert.assertTrue(Iterables.elementsEqual(iterable,tensor));

  }
}
