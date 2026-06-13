"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
let a = 50;
let b = 20;
console.log(add(a, b));
function add(a, b) {
    return a + b;
}
let student = [
    { roll: 101, name: 'a', course: 'l' },
    { roll: 102, name: 'b', course: 'm' },
    { roll: 103, name: 'c', course: 'n' },
    { roll: 103, name: 'd', course: 'o' }
];
let nums = [1, 2, 3, 4, 5];
let result1 = student.forEach((x) => console.log("this is" + x.roll));
let result2 = nums.map((x) => x * 2);
let res = nums.reduce((acm, x) => (acm + x));
var color;
(function (color) {
    color[color["red"] = 0] = "red";
    color[color["blue"] = 1] = "blue";
    color[color["orange"] = 2] = "orange";
})(color || (color = {}));
let c = color.blue;
console.log(c);
console.log(res);
console.log(result2);
var z = 14;
if (z == 14) {
    let z = 20;
    console.log(z);
}
console.log(z);
for (let i of nums) {
    console.log(i);
}
function getItems(items) {
    return new Array().concat(items);
}
console.log(getItems(nums));
