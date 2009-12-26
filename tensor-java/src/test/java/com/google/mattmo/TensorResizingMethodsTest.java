package com.google.mattmo;

import org.junit.Assert;
import org.junit.Test;

import static com.google.mattmo.TensorResizingMethods.project;

/**
 * TensorResizingMethodsTest
 *
 * @author matthew.gretton
 */
public class TensorResizingMethodsTest
{
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
   }, new int[]{2,2});

  @Test
  public void testProject()
  {
    Tensor<Integer> projectedTensor = project(tensor,new int[]{0},new int[]{2});

    Assert.assertEquals(5, projectedTensor.get(0,0,0).intValue());
    Assert.assertEquals(6, projectedTensor.get(0,0,1).intValue());
    Assert.assertEquals(7, projectedTensor.get(0,1,0).intValue());
    Assert.assertEquals(8, projectedTensor.get(0,1,1).intValue());
    Assert.assertEquals(5, projectedTensor.get(1,0,0).intValue());
    Assert.assertEquals(6, projectedTensor.get(1,0,1).intValue());
    Assert.assertEquals(7, projectedTensor.get(1,1,0).intValue());
    Assert.assertEquals(8, projectedTensor.get(1,1,1).intValue());

  }



}
