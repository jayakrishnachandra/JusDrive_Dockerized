//union and intersection types
// Union and intersection types in TypeScript allow you to create complex types by combining multiple types together.

// A union type allows a variable to hold values of multiple types, while an intersection type combines multiple types into one.
// Union types are defined using the `|` operator, while intersection types are defined using the `&` operator.
// Union types are useful when you want to allow a variable to hold values of different types, while intersection types are useful when you want to create a type that has all the properties of multiple types.




//// Example of union type:


type StringOrNumber = string | number;                      // This type allows a variable to hold either a string or a number.
let value: StringOrNumber;
value = "Hello"; // valid
value = 42; // valid

// value = true; // invalid, TypeScript will throw an error





// Example of intersection type:

type Person = {
    name: string;
    age: number;
};
type Employee = {
    id: number;
    department: string;
};


type EmployeeDetails = Person & Employee;
// This type combines the properties of both Person and Employee.


let employee: EmployeeDetails = {
    name: "John Doe",   // valid
    age: 30,            // valid
    id: 101,            // valid
    department: "HR"    // valid
};



type User = Person | Employee;                    //this type is a union of Person and Employee.
// This type allows a variable to hold either a Person or an Employee.

let user: User;
user = { name: "Alice", age: 28 }; // valid, a Person
user = { id: 105, department: "IT" }; // valid, an Employee
