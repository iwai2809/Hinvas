import java.math.BigDecimal;
import java.util.Scanner;

public class Sieve_of_Eratosthenes {//エラトステネスの篩(仮)
    //boolean型配列の初期値はfalse
    public static boolean[] prove_numbers=new boolean[0];//素数ならばtrue、それ以外にはfalseが入る

    public static long eratosthenes() {

        prove_numbers[2]=true;
            for (int i=3;i<prove_numbers.length;i+=2) {
                if (Prime_check(i)) {
                    prove_numbers[i]=true;//素数ならばtrue
                }
            }
            
        long result=add();
        return result;//素数の和
        
    }

    public static boolean Prime_check(int n) {
        for (int i=2;i<=(int)Math.sqrt(n);i++) {//篩い落とし作業は、入力された数字の平方根に達するまで続ける
            if (prove_numbers[i]&& n%i==0) {
                return false;
            }
        }
        return true;
    }

    public static long add() {//numbers配列のtrueになっている要素番号(素数)を足している
        long sum=2;
        for (int i=3;i<prove_numbers.length;i+=2) {
            if (prove_numbers[i]) {
                sum+=i;
            }
        }
        return sum;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("このプログラムは2~200万以下の全ての素数の和を求めます");

        boolean loop=true;

        while(loop){//2~200万の整数が出るまで繰返し
            try {
                System.out.println("2～200万までの整数を入力してください");

                Scanner n1=new Scanner(System.in);
                int num1=n1.nextInt();//整数入力

                if(num1<2||num1>2000000){
                System.out.println("範囲外なのでもう一度やり直してください");
            
                }else{
                    prove_numbers = new boolean[num1+1];//+1しないと範囲外になる場合がある 例：2など

                    long startTime=System.nanoTime();//計測開始
                    long test=eratosthenes();
                    long endTime=System.nanoTime();//計測終了

                    System.out.println("2～200万までの整数で、"+num1+"以下の素数の和="+test);
                    System.out.println("-----処理速度結果(指数表記ナシver)-----");

                    System.out.println(BigDecimal.valueOf(endTime-startTime)+"ナノ秒"); //処理速度結果(ナノ秒)
                    System.out.println(BigDecimal.valueOf((endTime-startTime)/Math.pow(10, 6))+"ミリ秒"); //処理速度結果(ミリ秒)
                    System.out.println(BigDecimal.valueOf((endTime-startTime)/Math.pow(10, 9))+"秒");//処理速度結果(秒)

                    System.out.println("-----処理速度結果(指数表記アリver)-----");

                    System.out.println((endTime-startTime)+"ナノ秒"); //処理速度結果(ナノ秒)
                    System.out.println((endTime-startTime)/Math.pow(10, 6)+"ミリ秒"); //処理速度結果(ミリ秒)
                    System.out.println((endTime-startTime)/Math.pow(10, 9)+"秒"); //処理速度結果(秒)
                    
                    n1.close();//scannerを閉じる
                    loop=false;//ループを抜ける
                }
            } catch (Throwable e) {
                //TODO: handle exception
                StackTraceElement[] ste=e.getStackTrace();
                System.out.println(e.getClass().getName()+": "+ e.getMessage());
                System.out.println("エラー発生個所："+ ste[ste.length-1]); // 最後の要素の例外発生場所のみ表示
                loop=false;
            }

        }
        
    }
}
