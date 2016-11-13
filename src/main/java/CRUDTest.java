/**
 * Created by phoebegl on 2016/11/10.
 * mysql高并发测试
 */
public class CRUDTest {

    public static void main(String[] args) {
        Thread t1 = new Thread(new Thread1());
        Thread t2 = new Thread(new Thread2());
        t1.start();
        t2.start();
    }
}
