package com.google.mattmo;

/**
 * Created by IntelliJ IDEA.
 * User: matt
 * Date: Dec 9, 2009
 * Time: 2:09:25 AM
 * To change this template use File | Settings | File Templates.
 */
final class IndexInfo
{
  private final int[] indexValues;
  private final int[] indexPositions;

  public IndexInfo(int[] indexPositions, int[] indexValues)
  {
    this.indexValues = indexValues;
    this.indexPositions = indexPositions;
  }

  public int[] indexValues()
  {
    return indexValues;
  }

  public int[] indexPositions()
  {
    return indexPositions;
  }


}
