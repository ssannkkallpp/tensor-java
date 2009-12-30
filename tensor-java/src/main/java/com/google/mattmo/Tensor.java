package com.google.mattmo;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.markit.mtk.collections.func.BinaryFunction;

import java.util.Collection;
import java.util.List;

public interface Tensor<E> extends Iterable<E>
{
  ImmutableList<Integer> indexSizes();
  E get(Integer... indices);
}

