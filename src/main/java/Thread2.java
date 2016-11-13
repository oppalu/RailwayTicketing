import java.util.Calendar;
import java.util.Date;

/**
 * Created by phoebegl on 2016/11/10.
 */
public class Thread2 implements Runnable {

    MysqlStart s = new MysqlStart();
    Calendar c = Calendar.getInstance();
    Date d = c.getTime();

    public void run() {

        try {
            for(int i=0;i<20;i++) {
                s.selectTicket("上海虹桥","徐州东","一等座",d);
                s.orderTicket("oppalu","G104","上海虹桥","徐州东","一等座",d);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
