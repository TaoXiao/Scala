package cn.gridx.java.lang.context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ping on 9/21/2014.
 */
public class CalculationContext implements Serializable {
    public static final ThreadLocal userThreadLocal = new ThreadLocal();

    public static void setCurrentContext(CalculationContext context) {
        userThreadLocal.set(context);
        DateTimeZone.setDefault(context.getTimeZone());
    }

    public static void unset() {
        userThreadLocal.remove();
    }

    public static CalculationContext getCurrentContext() {
        return (CalculationContext) userThreadLocal.get();
    }

    /////////////////////////////////////////////////////////////////////////////

    private DateTimeZone timeZone;
    private boolean redisCache;
    private HashMap<String,Object> option;  //may need String, DateTime

    public CalculationContext(DateTimeZone zone, HashMap<String,Object> option, boolean redis) {
        setTimeZone(zone);
        setRedisCache(redis);
        setOptionMap(option);
    }

    public DateTimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(DateTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isRedisCache() {
        return redisCache;
    }

    public void setRedisCache(boolean redisCache) {
        this.redisCache = redisCache;
    }

    private Map<String,Object> getOption() {
        if (option != null)
            return option;
        else {
            setOptionMap(new HashMap<String,Object>());
            return getOption();
        }
    }

    private void setOptionMap(HashMap<String,Object> option) {
        if (option == null)
            setOptionMap(new HashMap<String,Object>());
        else
            this.option = option;
    }

    public void setOption(String key, Object value){
        Map map = getOption();
        if (value==null)
            map.remove(key);
        else
            map.put(key, value);
    }

    public Object getOption(String key) {
        Map map = getOption();
        Object o = map.get(key);
        return o;
    }


    public String getOptionString(String key) {
        Map map = getOption();
        Object o = map.get(key);
        if (o != null)
            return o.toString();
        else return null;
    }

    public DateTime getOptionDateTime(String key) {
        Map map = getOption();
        Object o =map.get(key);
        if (o!=null && o instanceof DateTime)
            return ((DateTime)o);
        else return null;
    }

}

