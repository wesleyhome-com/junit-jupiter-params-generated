package com.wesleyhome.test.jupiter;

import java.util.Arrays;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.platform.commons.util.ToStringBuilder;

class MyArguments implements Arguments {

  private final Object[] arguments;

  MyArguments(Object[] arguments) {
    this.arguments = arguments;
  }

  @Override
  public Object[] get() {
    return arguments;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Arguments) {
      Arguments that = (Arguments) obj;
      return Arrays.equals(this.get(), that.get());
    }
    return false;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(MyArguments.class).append("arguments", arguments).toString();
  }
}
