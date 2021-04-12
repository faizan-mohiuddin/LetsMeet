package com.LetsMeet.LetsMeet.Root.Core.Model;

/**
 * Simple tuple of two types as this is not offered by standard Java
 */
public class LetsMeetTuple<X, Y> {
    public final X x;
    public final Y y;

    public LetsMeetTuple(X x, Y y) { 
      this.x = x; 
      this.y = y; 
    }
}
