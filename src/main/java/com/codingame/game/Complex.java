package com.codingame.game;

import java.util.StringTokenizer;

/**
 * Complex numbers. Basic operations.
 * @author Jaanus P&ouml;ial
 * @version 0.7
 */
public class Complex {

   /** numbers less than EPSILON are considered to be zero */
   private final static double EPSILON = 0.0000001;

   /** real part of the complex number */
   private double re = 0.0;

   /** imaginary part of the complex number */
   private double im = 0.0;

   /** Constructor from the pair of double values.
    * @param r real part
    * @param i imaginary part
    */
   public Complex(double r, double i) {
      this.re = r;
      this.im = i;
   }

   public Complex(int r, int i) {
      this.re = (double)r;
      this.im = (double)i;
   }

   public int getReInt() {
      return (int)getRe();
   }

   public int getImInt() {
      return (int)getIm();
   }

   /** Real part of the complex number.
    * @return real part
    */
   public double getRe() {
      return this.re;
   }

   /** Imaginary part of the complex number. 
    * @return imaginary part
    */
   public double getIm() {
      return this.im;
   }

   public void setRe(double re) {
      this.re = re;
   }

   public void setIm(double im) {
      this.im = im;
   }

   /** Test whether the real number is zero.
    * @param num real number
    * @return true if the number is close to zero
    *    (absolute value of num does not exceed EPSILON)
    */
   private static boolean isNaught (double num) {
      return Math.abs (num) <= Math.abs (EPSILON);
   }

   /** Conversion of the complex number to the string.
    * @return a string of form "a+bi", "-a+bi", "a-bi" or "-a-bi" 
    * (without any brackets)
    */
   @Override
   public String toString() {
      double a = this.getRe();
      double b = this.getIm();
      if (EPSILON > 0) {
         long jark = Math.round (1./EPSILON);
         a = Math.rint (a*jark)/jark;
         b = Math.rint (b*jark)/jark;
      }
      String rS = String.valueOf (a);
      String iS = String.valueOf (b);
      return rS + (iS.startsWith ("-") ? "" : "+") + iS + "i";
   }

   public String toGameString() {
      return getReInt() + " " + getImInt();
   }

   /** Conversion from the string to the complex number. 
    * Reverse to <code>toString</code> method.
    * @throws IllegalArgumentException if string s does not represent 
    *     a complex number (defined by the <code>toString</code> method)
    * @param s string of form produced by the <code>toString</code> method
    * @return a complex number represented by string s
    */
   public static Complex valueOf (String s) {
      double a = 0.0;
      double b = 0.0;
      StringTokenizer st = new StringTokenizer (s, "+-i", true);
      if (st.hasMoreTokens()) {
         String sa = st.nextToken().trim();
         if (st.hasMoreTokens()) {
            if (sa.equals ("+")) sa = st.nextToken().trim();
            if (sa.equals ("-")) sa = "-" + st.nextToken().trim();
            if (sa.equals ("i")) throw new IllegalArgumentException
               (s + " is not a complex number");
         } 
         a = Double.parseDouble (sa);
         if (st.hasMoreTokens()) {
            String sb = st.nextToken().trim();
            if (st.hasMoreTokens()) {
               if (sb.equals ("+")) sb = st.nextToken().trim();
               if (sb.equals ("-")) sb = "-" + st.nextToken().trim();
            }
            b = Double.parseDouble (sb);
         }
         if (st.hasMoreTokens()) {
            String si = st.nextToken().trim();
            if (!si.equals ("i"))
               throw new IllegalArgumentException
                  (s + " is not a complex number");
            if (st.hasMoreTokens())
               throw new IllegalArgumentException
                  (s + " is not a complex number");
         } else
            throw new IllegalArgumentException 
               (s + " is not a complex number");
      } else 
         throw new IllegalArgumentException (s + " is not a complex number");
      return new Complex(a, b);
   }

   /** Clone of the complex number.
    * @return independent clone of <code>this</code>
    */
   @Override
   public Complex clone() {
      return new Complex(getRe(), getIm());
   }

   /** Test whether the complex number is zero. 
    * @return true if the real part and the imaginary part 
    *    are both (close to) zero
    */
   public boolean isZero() {
      return isNaught (this.getRe()) && isNaught (this.getIm());
   }

   /** Conjugate of the complex number. Expressed by the formula 
    *     conjugate(a+bi) = a-bi
    * @return conjugate of <code>this</code>
    */
   public Complex conjugate() {
      return new Complex(this.getRe(), -this.getIm());
   }

   /** Opposite of the complex number. Expressed by the formula 
    *    opposite(a+bi) = -a-bi
    * @return complex number <code>-this</code>
    */
   public Complex opposite() {
      return new Complex(-this.getRe(), -this.getIm());
   }

   /** Sum of complex numbers. Expressed by the formula 
    *    (a+bi) + (c+di) = (a+c) + (b+d)i
    * @param k addend (c+di)
    * @return complex number <code>this+k</code>
    */
   public Complex plus (Complex k) {
      return new Complex(this.getRe()+k.getRe(),
         this.getIm()+k.getIm());
   }

   /** Product of complex numbers. Expressed by the formula
    *  (a+bi) * (c+di) = (ac-bd) + (ad+bc)i
    * @param k factor (c+di)
    * @return complex number <code>this*k</code>
    */
   public Complex times (Complex k) {
      return new Complex(this.getRe()*k.getRe()-this.getIm()*k.getIm(),
         this.getRe()*k.getIm()+this.getIm()*k.getRe());
   }

   public Complex times (double d) {
      return new Complex(this.getRe()*d,
              this.getIm()*d);
   }

   /** Inverse of the complex number. Expressed by the formula
    *     1/(a+bi) = a/(a*a+b*b) + ((-b)/(a*a+b*b))i
    * @return complex number <code>1/this</code>
    */
   public Complex inverse() {
      double rs = this.getRe()*this.getRe() + this.getIm()*this.getIm();
      if (isNaught (rs))
         throw new ArithmeticException ("/ by zero");
      return new Complex(this.getRe()/rs, -this.getIm()/rs);
   }

   /** Difference of complex numbers. Expressed as addition to the opposite.
    * @param k subtrahend
    * @return complex number <code>this-k</code>
    */
   public Complex minus (Complex k) {
      return this.plus (k.opposite());
   }



   /** Quotient of complex numbers. Expressed as multiplication to the inverse.
    * @param k divisor
    * @return complex number <code>this/k</code>
    */
   public Complex divideBy (Complex k) {
      return this.times (k.inverse());
   }

   public Complex divideBy (double d) {
      return this.times (new Complex(d, 0).inverse());
   }

   /** Equality test of complex numbers. Difference of equal numbers
    *     is (close to) zero.
    * @param ko second complex number
    * @return logical value of the expression <code>this.equals(ko)</code>
    */
   @Override
   public boolean equals (Object ko) {
      if (!(ko instanceof Complex))
         return false;
      return ((Complex)ko).minus (this).isZero();
   }

   /** Integer hashCode has to be the same for equal objects.
    * @return hashcode
    */
   @Override
   public int hashCode() {
      int v1 = (int) (Double.doubleToLongBits (getRe())>>32);
      int v2 = (int) (Double.doubleToLongBits (getIm())>>31);
      int v3 = (int) (v1^v2)>>1;
      return ((v3<0)?-v3:v3);
   }

   /** Module of the complex number. Expressed by the formula 
    *     module(a+bi) = Math.sqrt(a*a+b*b)
    * @return module of <code>this</code> (module is a real number)
    */
   public double module() {
      return Math.sqrt (this.getRe()*this.getRe() + this.getIm()*this.getIm());
   }

   /** Polar angle of the complex number (principal value) in radians.
    * Defined by the connection  a+bi = m*cos(p) + (m*sin(p))i ,
    *    where m is the module and p is the polar angle of complex number a+bi.
    *    In Java p=Math.atan2(b,a)
    * @return polar angle p of <code>this</code> (in radians)
    */
   public double angle() {
      if (this.isZero())
         throw new ArithmeticException ("angle is undefined for zero");
      double a = this.getRe();
      double b = this.getIm();
      return Math.atan2 (b,a);
   }

   public double angleAbs() {
      if (this.isZero())
         throw new ArithmeticException ("angle is undefined for zero");
      double a = this.getRe();
      double b = this.getIm();
      double ret = Math.atan2 (b,a);
      if (ret < 0) {
         ret = 2 * Math.PI + ret;
      }
      return ret % (2*Math.PI);
   }

   public int angleDeg() {
      if (this.isZero())
         throw new ArithmeticException ("angle is undefined for zero");
      double a = this.getRe();
      double b = this.getIm();
      return (int) (Math.atan2 (b,a) * 360 / (2 * Math.PI));
   }

   public static Complex byAngle(double angle) {
      double a = Math.cos(angle);
      double b = Math.sin(angle);
      return new Complex(a, b);
   }

   public static double dist(Complex a, Complex b) {
      return a.minus(b).module();
   }

   public static int distInt(Complex a, Complex b) {
      return (int) a.minus(b).module();
   }

   public Complex reduceToNorm1 () {
      return this.divideBy(this.module());
   }

   public void reduceRe(double value) {
      this.re -= value;
   }

   public void reduceIm(double value) {
      this.im -= value;
   }

   public void addRe(double value) {
      this.re += value;
   }

   public void addIm(double value) {
      this.im += value;
   }

   public static int angle (Complex c1, Complex middle, Complex c3) {
      Complex diffc1 = c1.minus(middle).reduceToNorm1();
      Complex diffc2 = c3.minus(middle).reduceToNorm1();

      double dist = diffc2.minus(diffc1).module();
      return  (int) (Math.asin(dist/2) * 360 / (2 * Math.PI));
   }

   public static double angleRad (Complex c1, Complex middle, Complex c3) {
      Complex diffc1 = c1.minus(middle).reduceToNorm1();
      Complex diffc2 = c3.minus(middle).reduceToNorm1();

      double dist = diffc2.minus(diffc1).module();
      return  2 * Math.asin(dist/2);
   }

   public Complex rotate(double angle) {
      return this.times(new Complex(Math.cos(angle), Math.sin(angle)));
   }


   public static double scal(Complex complex1, Complex complex2) {
      return complex1.getRe() * complex2.getRe() + complex1.getIm() * complex2.getIm();
   }
}
