import org.junit.Test;
import util.Mongodb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by phoebegl on 2016/11/9.
 */
public class TestMongodb {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testConnect() {
        Mongodb.connect();
    }

    @Test
    public void testSelect() {
        MongodbStart s = new MongodbStart();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        s.selectTicket("徐州东","南京南","一等座",d);
    }

    @Test
    public void testOrder() {
        MongodbStart s = new MongodbStart();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        s.orderTicket("glue","G101","徐州东","上海虹桥","二等座",d);
    }
}
