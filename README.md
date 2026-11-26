# japate
### Java Parsing and Templating

It is my claim that 99% of the business software I've written in my career is pulling in data in one or more formats, doing some light computation and aggregation, and then writing out the results in one or more formats. Those sources and destinations may involve databases and APIs and virtual file systems. But assuming drivers exist for whatever you're reading from and writing to, it's rarely more interesting than read-process-write. Or, at least, it didn't need to be more interesting.

But we quickly get into a world where the reading and writing bits attempt a hostile takeover of our patterns and practices. You want a popular ORM for your database connectivity? Great, you'll just need a tired, bloated, "IoC" mechanism to pollute your codebase. How about a one-size-fits-all universal API generator? Then you'll obviously want to expose the inerds of your object model for all to see and you to maintain into perpetuity.

I have argued for some time that if we just had the means to fuse reliable simple parsers with reliable simple templating, then we could have reliable simple solutions without all the complexity. Complex solutions make simple things sexy and hard things impossible. Simple solutions make things. 

Whether Japate is simple remains to be seen. Right now, it's perhaps best described as the fever dream of a jaded software developer obsessed with the idea of emergent language. Judge wisely for yourself.

## The Foundation

### Collections and Iteration (collect)

#### Reiterable

Reiterable is a lightweight map/reduce pattern built on top of the standard Java Iterable. It's fairly easy to get into the ecosystem.

    // re() wraps any Iterable
    Reiterable<String> example = Reiterable.re(List.of("a", "b", "c"));

    // alternatively, Reiterable is a functional interface (like Iterable)
    Reiterable<String> alternate = someList::iterator;

There are a few other static methods that will return a Reiterable and Reiterable methods return Reiterable, where appropriate.

#### BreakIterable

For reaasons I cannot explain, Java's BreakIterator is not actually an Iterator. BreakIterable attempts to rectify this horrible oversight. And it throws in a character escape method for free.

    // Iterates over grapheme clusters
    Reiterable<String> chars = BreakIterable.characters("abc"); 

    // or words or sentences or lines, with an optional Locale
    Reiterable<String> words = BreakIterable.words(Locale.UK, "Hello, World!");

#### Multimap/Node

These are singly linked list implementations that are meant to be created and then used without modification. They allow for efficient merges, at the cost of invalidating the merged objects. It's recommended that these not be used as their implementation and nesting behavior may change over time.

### Reflection (incept)

#### Incept

Java reflection is excellent at what it does, namely a runtime view of the compile-time product of Java source code. But sometimes we don't know the nature of the underlying code, or we'd like to remain blissfully unaware of changes to the code structure. Most object interactions can be made to mimic a sort of static method call. Incept attempts to make all Java field/method/constructor/enum invocation look like static method invocation. These are wrapped as Actions.

    // this would attempt to get the value of a field named "name", or invoke a no-args method named "name" or "getName"
    Incept.of(someObject).getAction("name").invoke();

    // this would attempt to set the value of a static field named "tag" with the value "Strong", 
    // or invoke a one-arg method named "tag" or "setTag"
    Incept.ofType(SomeClass.class).getAction("tag").invoke("Strong");

Incept was a fun way of solving the template-specific issue of extracting arbitrary properties from arbitrary objects. But in the course of creating this package I happened upon a more useful, more general purpose, and quite frankly more awesome way of doing the same thing better. So, Incept is here because it is needed for now, but it will go as soon as it can be replaced.

### Testing

#### When (when)

Java testing frameworks are prolific, and I'm sure we hardly need another. But I find myself wanting substantially better ways of expressing tests, especially interacting with exceptions. Enter When:

    // you can capture the result of an operation by assigning the expectation.
    String result = When.you(doSomething()).expect("some result");
    
    // you have a lot more flexibility in standard asserts on exceptions.
    When.youDo(() -> doSomething()).expectFailure(InvocationException.class).withMessage("Whoops!")
        .causedBy(NoSuchArgumentException.class).withMessage("babaGhanoush");

    // expect takes a consumer, so you have a lot more flexibility when expressing non-deterministic results
    When.you(doSomethingInteresting()).expect(a -> a.name().length() > 5 && a.age() > 21);

## Parsing

Finally, the good stuff. Not that the other stuff isn't good, but it's not called Java Iteration, Reflection, and Testing. 