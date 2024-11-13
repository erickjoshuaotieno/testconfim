import java.util.Scanner;

public class ThirdIndex {
    public static void main(String[] args){
        Scanner scanner= new Scanner(System.in);
        System.out.println("Entre String");
        String one=scanner.nextLine();

        one.replace("a", "b");
        System.out.println(one);

    }
}
