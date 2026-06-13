import { user } from "./interface";
import { student } from "./interface";


let a : number = 50;

let b:number = 20;

console.log(add(a,b));



function add(a : number, b:number):number
{
    return a+b;

}


let student:any[] = [
    {roll : 101 , name : 'a', course : 'l'},
    {roll : 102, name : 'b', course : 'm'},
    {roll : 103, name : 'c', course : 'n'},
    {roll : 103, name : 'd', course : 'o'}
  ]


  let nums: number[] = [1,2,3,4,5];


let result1 = student.forEach((x) => console.log("this is"+ x.roll));
let result2 = nums.map((x) => x*2);

let res = nums.reduce((acm , x) => (acm+x));


enum color
{
    red,blue,orange
}


let c :color = color.blue;

console.log(c);


console.log(res);

console.log(result2);



var  z:number = 14;

if(z == 14)
{
    let z:number = 20;
    console.log(z);
}

console.log(z);




for(let i of nums)
{
    console.log(i);
}



function getItems<T>(items :T[]) : T[]
{

    return new Array<T>().concat(items);
}


console.log(getItems(nums));

  

