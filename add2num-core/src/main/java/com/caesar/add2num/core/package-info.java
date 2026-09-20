/**
 * Addition of arbitrarily large non-negative integers held as decimal strings.
 *
 * <p>The entry point is {@link com.caesar.add2num.core.MyBigNumber}. The package deliberately has
 * no dependency outside the JDK, so it can be embedded in any application without dragging in a
 * logging framework or a utility library.
 *
 * <pre>{@code
 * MyBigNumber calculator = new MyBigNumber();
 *
 * String total = calculator.sum("1234", "897");            // "2131"
 *
 * SumResult traced = calculator.sumWithTrace("1234", "897");
 * traced.steps().forEach(step -> System.out.println(step.describe()));
 * // 4 + 7 + 0 = 11 -> write 1, carry 1
 * // 3 + 9 + 1 = 13 -> write 3, carry 1
 * // 2 + 8 + 1 = 11 -> write 1, carry 1
 * // 1 + 0 + 1 = 2  -> write 2, carry 0
 * }</pre>
 */
package com.caesar.add2num.core;
