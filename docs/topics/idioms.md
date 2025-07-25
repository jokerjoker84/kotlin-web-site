[//]: # (title: Idioms)

A collection of random and frequently used idioms in Kotlin. If you have a favorite idiom, contribute it by sending a pull request.

## Create DTOs (POJOs/POCOs)

```kotlin
data class Customer(val name: String, val email: String)
```

provides a `Customer` class with the following functionality:

* getters (and setters in case of `var`s) for all properties
* `equals()`
* `hashCode()`
* `toString()`
* `copy()`
* `component1()`, `component2()`, ..., for all properties (see [Data classes](data-classes.md))

## Default values for function parameters

```kotlin
fun foo(a: Int = 0, b: String = "") { ... }
```

## Filter a list

```kotlin
val positives = list.filter { x -> x > 0 }
```

Or alternatively, even shorter:

```kotlin
val positives = list.filter { it > 0 }
```

Learn the difference between [Java and Kotlin filtering](java-to-kotlin-collections-guide.md#filter-elements).

## Check the presence of an element in a collection

```kotlin
if ("john@example.com" in emailsList) { ... }

if ("jane@example.com" !in emailsList) { ... }
```

## String interpolation

```kotlin
println("Name $name")
```

Learn the difference between [Java and Kotlin string concatenation](java-to-kotlin-idioms-strings.md#concatenate-strings).

## Read standard input safely

```kotlin
// Reads a string and returns null if the input can't be converted into an integer. For example: Hi there!
val wrongInt = readln().toIntOrNull()
println(wrongInt)
// null

// Reads a string that can be converted into an integer and returns an integer. For example: 13
val correctInt = readln().toIntOrNull()
println(correctInt)
// 13
```

For more information, see [Read standard input.](read-standard-input.md)

## Instance checks

```kotlin
when (x) {
    is Foo -> ...
    is Bar -> ...
    else   -> ...
}
```

## Read-only list

```kotlin
val list = listOf("a", "b", "c")
```
## Read-only map

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 3)
```

## Access a map entry

```kotlin
println(map["key"])
map["key"] = value
```

## Traverse a map or a list of pairs

```kotlin
for ((k, v) in map) {
    println("$k -> $v")
}
```

`k` and `v` can be any convenient names, such as `name` and `age`.

## Iterate over a range

```kotlin
for (i in 1..100) { ... }  // closed-ended range: includes 100
for (i in 1..<100) { ... } // open-ended range: does not include 100
for (x in 2..10 step 2) { ... }
for (x in 10 downTo 1) { ... }
(1..10).forEach { ... }
```

## Lazy property

```kotlin
val p: String by lazy { // the value is computed only on first access
    // compute the string
}
```

## Extension functions

```kotlin
fun String.spaceToCamelCase() { ... }

"Convert this to camelcase".spaceToCamelCase()
```

## Create a singleton

```kotlin
object Resource {
    val name = "Name"
}
```

## Use inline value classes for type-safe values

```kotlin
@JvmInline
value class EmployeeId(private val id: String)

@JvmInline
value class CustomerId(private val id: String)
```

If you accidentally mix up `EmployeeId` and `CustomerId`, a compilation error is triggered.

> The `@JvmInline` annotation is only needed for JVM backends.
>
{style="note"}

## Instantiate an abstract class

```kotlin
abstract class MyAbstractClass {
    abstract fun doSomething()
    abstract fun sleep()
}

fun main() {
    val myObject = object : MyAbstractClass() {
        override fun doSomething() {
            // ...
        }

        override fun sleep() { // ...
        }
    }
    myObject.doSomething()
}
```

## If-not-null shorthand

```kotlin
val files = File("Test").listFiles()

println(files?.size) // size is printed if files is not null
```

## If-not-null-else shorthand

```kotlin
val files = File("Test").listFiles()

// For simple fallback values:
println(files?.size ?: "empty") // if files is null, this prints "empty"

// To calculate a more complicated fallback value in a code block, use `run`
val filesSize = files?.size ?: run { 
    val someSize = getSomeSize()
    someSize * 2
}
println(filesSize)
```

## Execute an expression if null

```kotlin
val values = ...
val email = values["email"] ?: throw IllegalStateException("Email is missing!")
```

## Get first item of a possibly empty collection

```kotlin
val emails = ... // might be empty
val mainEmail = emails.firstOrNull() ?: ""
```

Learn the difference between [Java and Kotlin first item getting](java-to-kotlin-collections-guide.md#get-the-first-and-the-last-items-of-a-possibly-empty-collection).

## Execute if not null

```kotlin
val value = ...

value?.let {
    ... // execute this block if not null
}
```

## Map nullable value if not null

```kotlin
val value = ...

val mapped = value?.let { transformValue(it) } ?: defaultValue 
// defaultValue is returned if the value or the transform result is null.
```

## Return on when statement

```kotlin
fun transform(color: String): Int {
    return when (color) {
        "Red" -> 0
        "Green" -> 1
        "Blue" -> 2
        else -> throw IllegalArgumentException("Invalid color param value")
    }
}
```

## try-catch expression

```kotlin
fun test() {
    val result = try {
        count()
    } catch (e: ArithmeticException) {
        throw IllegalStateException(e)
    }

    // Working with result
}
```

## if expression

```kotlin
val y = if (x == 1) {
    "one"
} else if (x == 2) {
    "two"
} else {
    "other"
}
```

## Builder-style usage of methods that return Unit

```kotlin
fun arrayOfMinusOnes(size: Int): IntArray {
    return IntArray(size).apply { fill(-1) }
}
```

## Single-expression functions

```kotlin
fun theAnswer() = 42
```

This is equivalent to

```kotlin
fun theAnswer(): Int {
    return 42
}
```

This can be effectively combined with other idioms, leading to shorter code. For example, with the `when` expression:

```kotlin
fun transform(color: String): Int = when (color) {
    "Red" -> 0
    "Green" -> 1
    "Blue" -> 2
    else -> throw IllegalArgumentException("Invalid color param value")
}
```

## Call multiple methods on an object instance (with)

```kotlin
class Turtle {
    fun penDown()
    fun penUp()
    fun turn(degrees: Double)
    fun forward(pixels: Double)
}

val myTurtle = Turtle()
with(myTurtle) { //draw a 100 pix square
    penDown()
    for (i in 1..4) {
        forward(100.0)
        turn(90.0)
    }
    penUp()
}
```

## Configure properties of an object (apply)

```kotlin
val myRectangle = Rectangle().apply {
    length = 4
    breadth = 5
    color = 0xFAFAFA
}
```

This is useful for configuring properties that aren't present in the object constructor.

## Java 7's try-with-resources

```kotlin
val stream = Files.newInputStream(Paths.get("/some/file.txt"))
stream.buffered().reader().use { reader ->
    println(reader.readText())
}
```

## Generic function that requires the generic type information

```kotlin
//  public final class Gson {
//     ...
//     public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
//     ...

inline fun <reified T: Any> Gson.fromJson(json: JsonElement): T = this.fromJson(json, T::class.java)
```

## Swap two variables

```kotlin
var a = 1
var b = 2
a = b.also { b = a }
```

## Mark code as incomplete (TODO)
 
Kotlin's standard library has a `TODO()` function that will always throw a `NotImplementedError`.
Its return type is `Nothing` so it can be used regardless of expected type.
There's also an overload that accepts a reason parameter:

```kotlin
fun calcTaxes(): BigDecimal = TODO("Waiting for feedback from accounting")
```

IntelliJ IDEA's kotlin plugin understands the semantics of `TODO()` and automatically adds a code pointer in the TODO tool window. 

## What's next?

* Solve [Advent of Code puzzles](advent-of-code.md) using the idiomatic Kotlin style.
* Learn how to perform [typical tasks with strings in Java and Kotlin](java-to-kotlin-idioms-strings.md).
* Learn how to perform [typical tasks with collections in Java and Kotlin](java-to-kotlin-collections-guide.md).
* Learn how to [handle nullability in Java and Kotlin](java-to-kotlin-nullability-guide.md).
