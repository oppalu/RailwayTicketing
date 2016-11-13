import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by phoebegl on 2016/11/9.
 */
public class TestMysql {

    @Test
    public void testSelectTicket() {
        MysqlStart s = new MysqlStart();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        s.selectTicket("徐州东","上海虹桥","二等座",d);
    }

    @Test
    public void testOrderTicket() {
        MysqlStart s = new MysqlStart();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        s.orderTicket("glue","G101","徐州东","上海虹桥","二等座",d);
    }

    @Test
    public void testRemove() {
        MysqlStart s = new MysqlStart();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        s.removeTicket("G101","徐州东","上海虹桥","二等座",d);
    }

}
