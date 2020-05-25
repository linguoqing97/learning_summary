package com.jdh.fuhsi.portal.util;

import com.jdh.fuhsi.portal.exception.DuplicationException;
import com.jdh.log.LogTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类(脚手架)
 * @author zz
 * @date 2020/3/20 15:02
 **/
@Component
public class RedisTools {

    /**
     * 指定过期时间
     * @param key 键
     * @param time 时间(单位:秒)
     * @return boolean 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            LogTools.error("指定过期时间失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间
     * @param key 键
     * @return long 时间(单位:秒，返回0代表为永久有效)
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     * @param key 键
     * @return long 时间(单位:秒，返回0代表为永久有效)
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return boolean 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            LogTools.error("判断redis_key是否存在失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除key
     * @param key 键
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 获取key
     * @param key 键
     * @return Object
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置key
     * @param key 键
     * @param value 值
     * @return boolean 是否设置成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LogTools.error("设置redis_key失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 设置key(含过期时间)
     * @param key 键
     * @param value 值
     * @param time 时间(单位:秒)
     * @return boolean 是否设置成功
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LogTools.error("设置redis_key(含过期时间)失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 设置key(含过期时间)
     * @param key 键
     * @param value 值
     * @param time 时间(单位:秒)
     * @return boolean 是否设置成功
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LogTools.error("设置key(含过期时间)失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取分布式锁
     * @param key 分布式锁ID
     * @throws DuplicationException e 获取锁失败异常
     */
    public void lock (String key) throws DuplicationException {
        if (!tryLock(key)) {
            throw new DuplicationException();
        }
    }

    /**
     * 尝试获取分布式锁
     * @param key 分布式锁ID
     * @return boolean 是否成功获取锁
     */
    public Boolean tryLock(String key) {
        Boolean tryLock = (Boolean) stringRedisTemplate.execute(new RedisCallback<Object>() {
            long expireTime = System.currentTimeMillis() + LOCK_EXPIRE_TIME + 1;
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Boolean acquire = redisConnection.setNX(key.getBytes(), String.valueOf(expireTime).getBytes());
                if (acquire) {
                    return Boolean.TRUE;
                }
                byte[] value = redisConnection.get(key.getBytes());
                if (Objects.nonNull(value) && value.length > 0) {
                    long oldTime = Long.parseLong(new String(value));
                    if (oldTime < System.currentTimeMillis()) {
                        byte[] oldValue = redisConnection.getSet(key.getBytes(), String.valueOf(System.currentTimeMillis()+ LOCK_EXPIRE_TIME + 1).getBytes());
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
                return Boolean.FALSE;
            }
        });
        if (!tryLock) {
            return Boolean.FALSE;
        }
        threadLocal.set(key);
        return Boolean.TRUE;
    }

    /**
     * 解锁分布式锁
     * @param key 分布式锁ID
     */
    public void unlock (String key) {
        try {
            if (!StringUtils.isBlank(key) && key.equals(threadLocal.get())) {
                stringRedisTemplate.delete(key);
            }
        } finally {
            threadLocal.remove();
        }
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final long LOCK_EXPIRE_TIME = 30000;

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

}

