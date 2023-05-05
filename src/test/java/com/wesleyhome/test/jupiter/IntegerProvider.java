package com.wesleyhome.test.jupiter;

import java.util.List;

@SuppressWarnings("ALL")
public class IntegerProvider {

  public static List<Integer> provider() {
    return List.of(1, 2, 3, 4, 5, 6, 7, 8);
  }
}
