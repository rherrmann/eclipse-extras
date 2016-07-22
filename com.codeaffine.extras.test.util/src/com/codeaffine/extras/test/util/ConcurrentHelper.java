package com.codeaffine.extras.test.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConcurrentHelper {

  public static Thread startThread( Runnable runnable ) throws InterruptedException {
    RunnableWrapper runnableWrapper = new RunnableWrapper( runnable );
    Thread result = startDaemonThread( runnableWrapper );
    runnableWrapper.awaitRunning();
    return result;
  }

  public static void runInThread( final Runnable runnable ) {
    final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
    Runnable exceptionGuard = new Runnable() {
      @Override
      public void run() {
        try {
          runnable.run();
        } catch( Throwable thr ) {
          exception.set( thr );
        }
      }
    };
    run( exceptionGuard );
    if( exception.get() != null ) {
      throw new RuntimeException( "Caught exception in thread", exception.get() );
    }
  }

  private static void run( Runnable runnable ) {
    Thread thread = startDaemonThread( runnable );
    try {
      thread.join();
    } catch( InterruptedException ie ) {
      throw new RuntimeException( ie );
    }
  }

  private static Thread startDaemonThread( Runnable runnable ) {
    Thread result = new Thread( runnable );
    result.setDaemon( true );
    result.start();
    return result;
  }

  private ConcurrentHelper() {}

  private static class RunnableWrapper implements Runnable {
    private final Runnable runnable;
    private final Lock lock;
    private final Condition running;

    RunnableWrapper( Runnable runnable ) {
      this.runnable = runnable;
      lock = new ReentrantLock();
      running = lock.newCondition();
    }

    @Override
    public void run() {
      signalRunning();
      runnable.run();
    }

    void awaitRunning() throws InterruptedException {
      lock.lock();
      try {
        running.await();
      } finally {
        lock.unlock();
      }
    }

    private void signalRunning() {
      lock.lock();
      try {
        running.signal();
      } finally {
        lock.unlock();
      }
    }
  }
}
