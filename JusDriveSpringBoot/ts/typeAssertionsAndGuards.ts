//Type Assertions and Guards
// TypeScript provides a way to assert the type of a variable using the `as` keyword
// or by using type guards. Type assertions tell the compiler to treat a variable as a specific type, while type guards are functions that check the type of a variable at runtime.
// Type assertions are useful when you know more about the type of a variable than TypeScript does.
// Type guards are useful when you want to narrow down the type of a variable based on certain conditions.
// Type assertions can be done using the `as` keyword or by using angle brackets syntax.

// Type guards can be implemented using the `typeof` operator for primitive types or using `instanceof` for class instances.
// TypeScript also provides user-defined type guards, which are functions that return a boolean indicating whether a variable is of a specific type.
// Type assertions and guards are powerful features that help you work with types more effectively in TypeScript.

// Type Assertions
// Type assertions allow you to tell the TypeScript compiler that you know more about the type of a variable than it does.
// You can use the `as` keyword or angle brackets syntax to assert a type.

// Example of type assertion using `as` keyword
let someValue: any = "Hello, TypeScript!";
let strLength: number = (someValue as string).length;                                           

console.log(strLength); // Output: 20

// Example of type assertion using angle brackets syntax
let anotherValue: any = 42;
let numValue: number = (<number>anotherValue) + 10;
console.log(numValue); // Output: 52




// Type Guards
// Type guards are functions or expressions that check the type of a variable at runtime.
// They help narrow down the type of a variable based on certain conditions.
// Example of type guard using `typeof`

function isString(value: any): value is string {
    return typeof value === "string";
}


function processValue(value: any) {
    if (isString(value)) {
        console.log("The value is a string with length:", value.length);
    } else {
        console.log("The value is not a string.");
    }
}
processValue("Hello, TypeScript!"); // Output: The value is a string with length: 20
// Example of type guard using `instanceof`