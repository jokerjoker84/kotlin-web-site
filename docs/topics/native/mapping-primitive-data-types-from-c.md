[//]: # (title: Mapping primitive data types from C – tutorial)

<tldr>
    <p>This is the first part of the <strong>Mapping Kotlin and C</strong> tutorial series.</p>
    <p><img src="icon-1.svg" width="20" alt="First step"/> <strong>Mapping primitive data types from C</strong><br/>
       <img src="icon-2-todo.svg" width="20" alt="Second step"/> <a href="mapping-struct-union-types-from-c.md">Mapping struct and union types from C</a><br/>
       <img src="icon-3-todo.svg" width="20" alt="Third step"/> <a href="mapping-function-pointers-from-c.md">Mapping function pointers</a><br/>
       <img src="icon-4-todo.svg" width="20" alt="Fourth step"/> <a href="mapping-strings-from-c.md">Mapping strings from C</a><br/>
    </p>
</tldr>

> The C libraries import is in [Beta](native-c-interop-stability.md). All Kotlin declarations
> generated by the cinterop tool from C libraries should have the `@ExperimentalForeignApi` annotation.
>
> Native platform libraries shipped with Kotlin/Native (like Foundation, UIKit, and POSIX)
> require opt-in only for some APIs.
>
{style="note"}

Let's explore which C data types are visible in Kotlin/Native and vice versa and examine advanced
C interop-related use cases of Kotlin/Native and [multiplatform](gradle-configure-project.md#targeting-multiple-platforms)
Gradle builds.

In this tutorial, you'll:

* [Learn about data types in the C language](#types-in-c-language)
* [Create a C Library that uses those types in exports](#create-a-c-library)
* [Inspect generated Kotlin APIs from a C library](#inspect-generated-kotlin-apis-for-a-c-library)

You can use the command line to generate a Kotlin library, either directly or with a script file (such as `.sh` or `.bat` file).
However, this approach doesn't scale well for larger projects that have hundreds of files and libraries.
Using a build system simplifies the process by downloading and caching the Kotlin/Native
compiler binaries and libraries with transitive dependencies, as well as by running the compiler and tests.
Kotlin/Native can use the [Gradle](https://gradle.org) build system through the [Kotlin Multiplatform plugin](gradle-configure-project.md#targeting-multiple-platforms).

## Types in C language

The C programming language has the following [data types](https://en.wikipedia.org/wiki/C_data_types):

* Basic types: `char, int, float, double` with modifiers `signed, unsigned, short, long`
* Structures, unions, arrays
* Pointers
* Function pointers

There are also more specific types:

* Boolean type (from [C99](https://en.wikipedia.org/wiki/C99))
* `size_t` and `ptrdiff_t` (also `ssize_t`)
* Fixed width integer types, such as `int32_t` or `uint64_t` (from [C99](https://en.wikipedia.org/wiki/C99))

There are also the following type qualifiers in the C language: `const`, `volatile`, `restrict`, `atomic`.

Let's see which C data types are visible in Kotlin.

## Create a C library

In this tutorial, you won't create a `lib.c` source file, which is only necessary if you want to
compile and run your C library. For this setup, you'll only need a `.h` header file that is required for running
the [cinterop tool](native-c-interop.md).

The cinterop tool generates a Kotlin/Native library (a `.klib` file) for each set of `.h` files. The generated library
helps bridge calls from Kotlin/Native to C. It includes Kotlin declarations that correspond to the definitions from the
`.h` files.

To create a C library:

1. Create an empty folder for your future project.
2. Inside, create a `lib.h` file with the following content to see how C functions are mapped into Kotlin:

   ```c
   #ifndef LIB2_H_INCLUDED
   #define LIB2_H_INCLUDED

   void ints(char c, short d, int e, long f);
   void uints(unsigned char c, unsigned short d, unsigned int e, unsigned long f);
   void doubles(float a, double b);
   
   #endif
   ```

   The file doesn't have the `extern "C"` block, which is not needed for this example but may be necessary if you use C++
   and overloaded functions. See this [Stackoverflow thread](https://stackoverflow.com/questions/1041866/what-is-the-effect-of-extern-c-in-c)
   for more details.

3. Create the `lib.def` [definition file](native-definition-file.md) with the following content:

   ```c
   headers = lib.h
   ```

4. It can be helpful to include macros or other C definitions in the code generated by the cinterop tool. This way,
   method bodies are also compiled and fully included in the binary. With this feature, you can create a runnable example
   without needing a C compiler.

   To do that, add implementations to the C functions from the `lib.h` file to a new `interop.def` file after the `---`
   separator:

   ```c
   
   ---
    
   void ints(char c, short d, int e, long f) { }
   void uints(unsigned char c, unsigned short d, unsigned int e, unsigned long f) { }
   void doubles(float a, double b) { }
   ```

The `interop.def` file provides everything necessary to compile, run, or open the application in an IDE.

## Create a Kotlin/Native project

> See the [Get started with Kotlin/Native](native-get-started.md#using-gradle) tutorial for detailed first steps
> and instructions on how to create a new Kotlin/Native project and open it in IntelliJ IDEA.
>
{style="tip"}

To create project files:

1. In your project folder, create a `build.gradle(.kts)` Gradle build file with the following content:

    <tabs group="build-script">
    <tab title="Kotlin" group-key="kotlin">

    ```kotlin
    plugins {
        kotlin("multiplatform") version "%kotlinVersion%"
    }
    
    repositories {
        mavenCentral()
    }
    
    kotlin {
        macosArm64("native") {    // macOS on Apple Silicon
        // macosX64("native") {   // macOS on x86_64 platforms
        // linuxArm64("native") { // Linux on ARM64 platforms 
        // linuxX64("native") {   // Linux on x86_64 platforms
        // mingwX64("native") {   // on Windows
            val main by compilations.getting
            val interop by main.cinterops.creating
        
            binaries {
                executable()
            }
        }
    }
    
    tasks.wrapper {
        gradleVersion = "%gradleVersion%"
        distributionType = Wrapper.DistributionType.BIN
    }
    ```

    </tab>
    <tab title="Groovy" group-key="groovy">

    ```groovy
    plugins {
        id 'org.jetbrains.kotlin.multiplatform' version '%kotlinVersion%'
    }
    
    repositories {
        mavenCentral()
    }
    
    kotlin {
        macosArm64("native") {    // Apple Silicon macOS
        // macosX64("native") {   // macOS on x86_64 platforms
        // linuxArm64("native") { // Linux on ARM64 platforms
        // linuxX64("native") {   // Linux on x86_64 platforms
        // mingwX64("native") {   // Windows
            compilations.main.cinterops {
                interop 
            }
        
            binaries {
                executable()
            }
        }
    }
    
    wrapper {
        gradleVersion = '%gradleVersion%'
        distributionType = 'BIN'
    }
    ```

    </tab>
    </tabs>

   The project file configures the C interop as an additional build step.
   Check out the [Multiplatform Gradle DSL reference](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-dsl-reference.html) to learn about different
   ways you can configure it.

2. Move your `interop.def`, `lib.h`, and `lib.def` files to the `src/nativeInterop/cinterop` directory.
3. Create a `src/nativeMain/kotlin` directory. This is where you should place all the source files, following Gradle's
   recommendations on using conventions instead of configurations.

   By default, all the symbols from C are imported to the `interop` package.

4. In `src/nativeMain/kotlin`, create a `hello.kt` stub file with the following content:

    ```kotlin
    import interop.*
    import kotlinx.cinterop.ExperimentalForeignApi

    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        println("Hello Kotlin/Native!")
      
        ints(/* fix me*/)
        uints(/* fix me*/)
        doubles(/* fix me*/)
    }
    ```

You'll complete the code later as you learn how C primitive type declarations look from the Kotlin side.

## Inspect generated Kotlin APIs for a C library

Let's see how C primitive types are mapped into Kotlin/Native and update the example project accordingly.

Use IntelliJ IDEA's [Go to declaration](https://www.jetbrains.com/help/rider/Navigation_and_Search__Go_to_Declaration.html)
command (<shortcut>Cmd + B</shortcut>/<shortcut>Ctrl + B</shortcut>) to navigate to the following generated API
for C functions:

```kotlin
fun ints(c: kotlin.Byte, d: kotlin.Short, e: kotlin.Int, f: kotlin.Long)
fun uints(c: kotlin.UByte, d: kotlin.UShort, e: kotlin.UInt, f: kotlin.ULong)
fun doubles(a: kotlin.Float, b: kotlin.Double)
```

C types are mapped directly, except for the `char` type, which is mapped to `kotlin.Byte` as it's usually an 8-bit
signed value:

| C                  | Kotlin        |
|--------------------|---------------|
| char               | kotlin.Byte   |
| unsigned char      | kotlin.UByte  |
| short              | kotlin.Short  |
| unsigned short     | kotlin.UShort |
| int                | kotlin.Int    |
| unsigned int       | kotlin.UInt   |
| long long          | kotlin.Long   |
| unsigned long long | kotlin.ULong  |
| float              | kotlin.Float  |
| double             | kotlin.Double |

## Update Kotlin code

Now that you've seen the C definitions, you can update your Kotlin code. The final code in the `hello.kt` file may look
like this:

```kotlin
import interop.*
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun main() {
    println("Hello Kotlin/Native!")
  
    ints(1, 2, 3, 4)
    uints(5u, 6u, 7u, 8u)
    doubles(9.0f, 10.0)
}
```

To verify that everything works as expected, run the `runDebugExecutableNative` Gradle task [in your IDE](native-get-started.md#build-and-run-the-application)
or use the following command to run the code:

```bash
./gradlew runDebugExecutableNative
```

## Next step

In the next part of the series, you'll learn how struct and union types are mapped between Kotlin and C:

**[Proceed to the next part](mapping-struct-union-types-from-c.md)**

### See also

Learn more in the [Interoperability with C](native-c-interop.md) documentation that covers more advanced scenarios.