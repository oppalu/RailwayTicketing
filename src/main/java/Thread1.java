import java.util.Calendar;
import java.util.Date;

/**
 * Created by phoebegl on 2016/11/10.
 */
public class Thread1 implements Runnable {

    MysqlStart s = new MysqlStart();
    Calendar c = Calendar.getInstance();
    Date d = c.getTime();

    public void run() {
        try {
            for(int i=0;i<20;i++) {
                s.selectTicket("上海虹桥","南京南","一等座",d);
                s.orderTicket("glue","G104","上海虹桥","南京南","一等座",d);
                Thread.sleep(800);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
