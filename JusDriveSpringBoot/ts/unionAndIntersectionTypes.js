"use strict";
//union and intersection types
// Union and intersection types in TypeScript allow you to create complex types by combining multiple types together.
let value;
value = "Hello"; // valid
value = 42; // valid
// This type combines the properties of both Person and Employee.
let employee = {
    name: "John Doe", // valid
    age: 30, // valid
    id: 101, // valid
    department: "HR" // valid
};
// This type allows a variable to hold either a Person or an Employee.
let user;
user = { name: "Alice", age: 28 }; // valid, a Person
user = { id: 105, department: "IT" }; // valid, an Employee
