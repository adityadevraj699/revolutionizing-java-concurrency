# 🧠 Conceptual Questions: Java Concurrency & Virtual Threads

This file covers fundamental questions and core concepts related to Java's concurrency evolution, Project Loom, and virtual threads.

---

## 1. What is Project Loom and what is its role in Java?

Project Loom is an OpenJDK project introduced to simplify and enhance concurrency in Java. It provides:

- **Virtual Threads** – lightweight, scalable threads managed by JVM
- **Structured Concurrency** – managing related tasks as a unit
- **Scoped Values** – a safer alternative to ThreadLocal

It makes concurrent programming easier and more efficient without requiring complex reactive programming.

---

## 2. What are Virtual Threads? How are they different from Platform Threads?

Virtual threads are lightweight threads introduced in Java 21 (preview). Unlike platform threads that are managed by the operating system, virtual threads are managed by the JVM.

| Feature              | Platform Thread           | Virtual Thread               |
|----------------------|---------------------------|-------------------------------|
| Managed by           | Operating System          | Java Virtual Machine          |
| Memory Usage         | ~1MB per thread           | ~4KB per thread               |
| Thread Creation Cost | High                      | Very low                      |
| Scalability          | Thousands of threads      | Millions of threads           |

**Example:**

```java
// Platform thread
Thread t1 = new Thread(() -> {
    System.out.println("Platform thread");
});
t1.start();

// Virtual thread
Thread.startVirtualThread(() -> {
    System.out.println("Virtual thread");
});



3. What is Structured Concurrency?
Structured concurrency is a model where multiple related tasks (child threads) are managed as a single unit (parent task). If any child task fails, the whole structure can be cancelled or managed together.

Benefits:

Safer thread lifecycle management

Easier error propagation

Cleaner code

Example:
try (var scope = StructuredTaskScope.ShutdownOnFailure.open()) {
    Future<String> user = scope.fork(() -> fetchUserData());
    Future<String> order = scope.fork(() -> fetchOrderData());
    scope.join(); // Wait for all
    System.out.println(user.resultNow() + " - " + order.resultNow());
}


4. What are Scoped Values? How are they different from ThreadLocal?
Scoped values are temporary data holders introduced in Java 21. They offer a safer, cleaner alternative to ThreadLocal.
| Feature       | ThreadLocal              | ScopedValue                  |
| ------------- | ------------------------ | ---------------------------- |
| Lifetime      | Tied to thread lifecycle | Tied to code scope           |
| Risk of leaks | Yes                      | No                           |
| Performance   | Moderate                 | Better (contextually scoped) |

Example:
ScopedValue<String> userId = ScopedValue.newInstance();
ScopedValue.where(userId, "u123").run(() -> {
    System.out.println("User ID: " + userId.get());
});


5. When are Virtual Threads most effective?
Virtual threads are most effective in I/O-bound tasks where threads wait for external input/output (e.g., file read, database query).

Ideal scenarios:

Handling thousands of web/API requests

Chat systems or messaging apps

College ERP portals (result login, registration)

IRCTC ticket booking during high load

Not suitable for:

CPU-heavy tasks like encryption or image processing


6. What is the difference between I/O-bound and CPU-bound tasks?
| Type      | Description                | Examples                                  |
| --------- | -------------------------- | ----------------------------------------- |
| I/O-bound | Spends most time waiting   | HTTP requests, DB queries, file I/O       |
| CPU-bound | Consumes constant CPU time | Encryption, video processing, simulations |

I/O-bound example:
Thread.sleep(200); // simulating network delay

CPU-bound example:
boolean isPrime(int n) {
    for (int i = 2; i <= Math.sqrt(n); i++) {
        if (n % i == 0) return false;
    }
    return true;
}


7. How has Java concurrency evolved over time?
| Java Version | Key Feature Introduced                                 | Purpose                                 |
| ------------ | ------------------------------------------------------ | --------------------------------------- |
| Java 1.0     | Thread, Runnable                                       | Basic multithreading                    |
| Java 5       | ExecutorService                                        | Thread pooling                          |
| Java 7       | Fork/Join Framework                                    | Divide-and-conquer parallelism          |
| Java 8       | CompletableFuture                                      | Asynchronous programming                |
| Java 21      | Virtual Threads, Structured Concurrency, Scoped Values | Modern concurrency model (Project Loom) |


8. What is the goal of Project Loom?
Decouple Java threads from OS threads.

Let developers write simple, blocking code that performs like async code.

Allow applications to scale to millions of threads.

Reduce the complexity of using CompletableFuture, reactive streams, and callbacks.


9. Why were traditional threads inefficient in high-concurrency environments?
Each thread used ~1MB memory

OS-level thread switching is expensive

High risk of OutOfMemoryError under large-scale load

Not suitable for applications like:

IRCTC during Tatkal booking

University portals during result declaration

Flash sale systems (Flipkart, Amazon)


10. Is it possible to use virtual threads with existing Java code?
Yes. Virtual threads are fully compatible with existing Java APIs like:

Runnable, Callable

ExecutorService

Synchronous I/O operations

You can write blocking-style code that still scales, without needing complex changes.


