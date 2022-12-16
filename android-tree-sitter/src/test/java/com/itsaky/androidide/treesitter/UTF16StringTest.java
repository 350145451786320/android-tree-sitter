package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Akash Yadav
 */
@RunWith(JUnit4.class)
public class UTF16StringTest extends TreeSitterTest {

  @Before
  public void testFunctionality() {
    System.out.println("testFunc");
    final var str = UTF16String.newInstance("Hello");
    final var str2 = UTF16String.newInstance("World");

    assertThat(str.toString()).isEqualTo("Hello");
    assertThat(str.length()).isEqualTo(5);
    assertThat(str.byteLength()).isEqualTo(10);

    str.append(" World!");
    assertThat(str.toString()).isEqualTo("Hello World!");
    assertThat(str.length()).isEqualTo(12);
    assertThat(str.byteLength()).isEqualTo(24);
    str.close();
  }

  @Test
  public void testEmoji() {
    System.out.println("testEmoji");
    final var str = UTF16String.newInstance("😍");

    str.append("\n\n");
    str.append("😍");

    assertThat(str.length()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D".length());
    assertThat(str.byteLength()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D".length() * 2);
    assertThat(str.toString()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D");
    str.close();
  }

  @Test
  public void testContinuousStringCreation() {
    final var arr = new UTF16String[100];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = UTF16String.newInstance("Item #" + i);
    }
    for (UTF16String utf16String : arr) {
      utf16String.close();
    }
  }

  @Test
  public void testMultithreading() throws InterruptedException {
    final var threads = new Thread[20];
    for (int i = 0; i < threads.length; i++) {
      threads[i] =
          new Thread(
              () -> {
                final var strs = new UTF16String[100];
                for (int j = 0; j < strs.length; j++) {
                  strs[j] =
                      UTF16String.newInstance(
                          "UTF16String from " + Thread.currentThread().getName());
                  try {
                    Thread.sleep(10);
                  } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                  }
                }
                for (UTF16String str : strs) {
                  str.close();
                }
              },
              "StringThread #" + i);
    }

    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }
}
