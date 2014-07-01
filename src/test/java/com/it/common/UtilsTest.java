package com.it.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.it.model.Interval;

public class UtilsTest {
  @Test
  public void toJson() {
    // Given
    Interval interval =
        new Interval("127.0.0.1", 1010, "desc", 100, 1000, (short) 0, (short) 1, "running");

    // When
    String json = Utils.toJson(interval);

    // Then
    assertThat(
        json,
        is("{\"host\":\"127.0.0.1\",\"port\":1010,\"desc\":\"desc\",\"sentTps\":100,\"totalSent\":1000,\"masterPriority\":0,\"priorityPoint\":1,\"status\":\"running\"}"));
  }
}
