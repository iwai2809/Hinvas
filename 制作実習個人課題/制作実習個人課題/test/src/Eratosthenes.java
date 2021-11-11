import java.util.Scanner;


public class Eratosthenes {

        
    public static boolean[] numbers = new boolean[1];
        public static long eratosthenes(int p) {

            numbers[2] = true;
                for (int i = 3; i < numbers.length; i+=2) {
                    if (check(i)) {
                        numbers[i] = true;
                    }
                }
                
                long sum=add();
                return sum;
        }

        public static boolean check(int n) {
            for (int i = 2; i <= (int)Math.sqrt(n); i++) {
                if (numbers[i] && n % i == 0) {
                    return false;
                }
            }
            return true;
        }
        public static long add() {
            long sum=2;
            for (int i = 3; i < numbers.length; i += 2) {
                if (numbers[i]) {
                    sum += i;
                }
            }
            return sum;
        }
    public static void main(String[] args) throws Exception {
        System.out.println("このプログラムは200万以下の全ての素数の和を求めます");
        System.out.println("2~200万までの整数を入力してください");
        Scanner n1=new Scanner(System.in);
        int num1=n1.nextInt();

        if(num1<2||num1>2000000){
            System.out.println("2~200万までの整数を入力してください");
            
        }else{
            numbers = new boolean[num1];
            long startTime = System.nanoTime();//計測開始
            long test=eratosthenes(num1);
            long endTime = System.nanoTime();//計測終了
            System.out.println(test);
            System.out.println((endTime-startTime)/1000000000f+"秒"); 
        }
        n1.close();
    }
}
