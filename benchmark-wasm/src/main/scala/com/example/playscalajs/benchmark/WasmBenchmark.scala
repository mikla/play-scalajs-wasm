package com.example.playscalajs.benchmark
import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.JSON

object WasmBenchmark extends BenchmarkSuite {

  def runBenchmarks(): js.Dictionary[Double] = {
    val results = js.Dictionary[Double]()

    results("fibonacci_recursive_35") = measureTime("Fibonacci Recursive", 5) {
      fibonacciRecursive(35)
    }

    results("fibonacci_iterative_10000") = measureTime("Fibonacci Iterative", 100) {
      fibonacciIterative(10000)
    }

    results("count_primes_10000") = measureTime("Count Primes", 5) {
      countPrimes(10000)
    }

    results("matrix_multiply_100") = measureTime("Matrix Multiply 100x100", 5) {
      matrixMultiply(100)
    }

    results("quicksort_10000") = measureTime("QuickSort 10000", 10) {
      val arr = Array.fill(10000)(scala.util.Random.nextInt(10000))
      quickSort(arr)
    }

    results("string_manipulation_1000") = measureTime("String Manipulation", 10) {
      stringManipulation(1000)
    }

    results("collection_operations_1000") = measureTime("Collection Ops", 10) {
      collectionOperations(1000)
    }

    results
  }

  def main(args: Array[String]): Unit = {
    println("[WasmBenchmark] Module loaded and main() called")

    def runBenchmarksWithProgress(): Unit = {
      val resultsElement = dom.document.getElementById("benchmark-results-wasm")

      if (resultsElement != null) {
        println("[WasmBenchmark] Found results element, starting benchmarks")
        resultsElement.innerHTML = "<div style='color: #9900cc;'>Starting WebAssembly benchmarks...</div>"

        js.timers.setTimeout(100) {
          try {
            resultsElement.innerHTML = "<div style='color: #9900cc;'>Running performance tests...</div>"
            val results = runBenchmarks()

            println(s"[WasmBenchmark] Benchmarks complete: ${JSON.stringify(results)}")

            resultsElement.innerHTML = ""
            val pre = dom.document.createElement("pre")
            pre.textContent = JSON.stringify(results, space = 2)
            resultsElement.appendChild(pre)

            dom.window.asInstanceOf[js.Dynamic].wasmBenchmarkResults = results

            val event = js.Dynamic.literal(
              detail = results,
              bubbles = true,
              cancelable = true
            )
            dom.document.dispatchEvent(
              js.Dynamic.newInstance(js.Dynamic.global.CustomEvent)("wasm-benchmark-complete", event).asInstanceOf[dom.Event]
            )
          } catch {
            case e: Exception =>
              println(s"[WasmBenchmark] Error: ${e.getMessage}")
              resultsElement.innerHTML = s"<div style='color: red;'>Error: ${e.getMessage}</div>"
          }
        }
      } else {
        println("[WasmBenchmark] Results element not found, waiting...")
      }
    }

    if (dom.document.readyState.toString == "complete" || dom.document.readyState.toString == "interactive") {
      runBenchmarksWithProgress()
    } else {
      dom.window.addEventListener("load", { _: dom.Event =>
        runBenchmarksWithProgress()
      })
    }
  }
}