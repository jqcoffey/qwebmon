package com.criteo.qwebmon.drivers

object JdbcHelpers {

  /**
    * Applies a function of type A => R on an AutoCloseable object of type A.
    *
    * @param autoCloseable the object with the AutoCloseable trait
    * @param fn the mapping function
    * @tparam A
    * @tparam R
    * @return
    */
  def withAutoCloseable[A <: AutoCloseable, R](autoCloseable: A)(fn: (A => R)): R = {
    try {
      fn(autoCloseable)
    } finally {
      autoCloseable.close()
    }
  }
}
