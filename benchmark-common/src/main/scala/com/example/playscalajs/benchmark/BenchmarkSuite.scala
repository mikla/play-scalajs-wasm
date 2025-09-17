package com.example.playscalajs.benchmark

import scala.scalajs.js

trait BenchmarkSuite {

  def fibonacciRecursive(n: Int): Int = {
    if (n <= 1) n
    else fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)
  }

  def fibonacciIterative(n: Int): Int = {
    if (n <= 1) return n
    var prev = 0
    var curr = 1
    for (_ <- 2 to n) {
      val temp = curr
      curr = prev + curr
      prev = temp
    }
    curr
  }

  def primeCheck(n: Int): Boolean = {
    if (n <= 1) return false
    if (n <= 3) return true
    if (n % 2 == 0 || n % 3 == 0) return false

    var i = 5
    while (i * i <= n) {
      if (n % i == 0 || n % (i + 2) == 0) return false
      i += 6
    }
    true
  }

  def countPrimes(limit: Int): Int = {
    (2 to limit).count(primeCheck)
  }

  def matrixMultiply(size: Int): Array[Array[Double]] = {
    val a = Array.ofDim[Double](size, size)
    val b = Array.ofDim[Double](size, size)
    val c = Array.ofDim[Double](size, size)

    for (i <- 0 until size; j <- 0 until size) {
      a(i)(j) = i + j
      b(i)(j) = i - j
    }

    for (i <- 0 until size; j <- 0 until size; k <- 0 until size) {
      c(i)(j) += a(i)(k) * b(k)(j)
    }

    c
  }

  def quickSort(arr: Array[Int]): Array[Int] = {
    if (arr.length <= 1) arr
    else {
      val pivot = arr(arr.length / 2)
      Array.concat(
        quickSort(arr.filter(_ < pivot)),
        arr.filter(_ == pivot),
        quickSort(arr.filter(_ > pivot))
      )
    }
  }

  def stringManipulation(iterations: Int): String = {
    var result = ""
    for (i <- 0 until iterations) {
      result += s"Item$i-"
    }
    result
  }

  def collectionOperations(size: Int): Int = {
    val list = (1 to size).toList
    list
      .filter(_ % 2 == 0)
      .map(_ * 2)
      .flatMap(x => List(x, x + 1))
      .reduce(_ + _)
  }

  def measureTime(name: String, iterations: Int = 1)(block: => Any): Double = {
    val start = js.Date.now()
    for (_ <- 0 until iterations) {
      block
    }
    val end = js.Date.now()
    (end - start) / iterations
  }
}