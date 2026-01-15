using System;

class Program
{
    static void Main()
    {
        // Console.WriteLine("Hello, Jerry");
        // for (int i = 0; i < 3; i++)
        // {
        //     printABC();
        //     Thread.Sleep(500);
        // }

        A();
        A();
        A();
        Console.ReadKey();
    }

        static void A()
    {
        B();
        C();
    }
    static void B()
    {
        Console.WriteLine("B");
    }
    static void C()
    {
        Console.WriteLine("C");
    }

    // static void printABC()
    // {
    //     Console.WriteLine("A");
    //     Console.WriteLine("B");
    //     Console.WriteLine("C");
    // }


}