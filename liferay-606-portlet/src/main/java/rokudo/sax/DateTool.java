package rokudo.sax;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
	static private SimpleDateFormat format_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	static private SimpleDateFormat format_ggmmaaaa = new SimpleDateFormat("dd-MM-yyyy");

	static public String toGG_MM_AAAA(String strDate) {
        Date date;
		try {
			date = format_ISO8601.parse(strDate);
	        strDate = format_ggmmaaaa.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strDate;
	}
}
