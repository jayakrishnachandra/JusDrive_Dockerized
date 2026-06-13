"use strict";
//explain
// The `user` type is an intersection type that combines the properties of both `person` and `employee` interfaces.
// This means that a `user` must have all properties from both interfaces.
//type means
// A `type` in TypeScript is a way to define a custom type that can be a combination of other types, including primitive types, interfaces, or other types. It can also represent union types, intersection types, and more complex structures.
let user1 = { name: "jay", age: 25 };
let employee1 = { id: 101, department: "HR" };
let user2 = { name: "john" };
console.log(user2); //{ name: 'john' }
let user3 = user1;
console.log(user3); //{ name: 'jay', age: 25 }
//let and const not required for class membrs
// -------------------------------------------------- Array Destructuring --------------------------------------------------
let numbers = [1, 2, 3, 4, 5];
let [first, second, ...rest] = numbers;
console.log(first); // 1
console.log(second); // 2
console.log(rest); // [3, 4, 5]
