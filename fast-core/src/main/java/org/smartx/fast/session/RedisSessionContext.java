package org.smartx.fast.session;

import org.apache.commons.lang3.StringUtils;
import org.smartx.fast.bean.ApiRequest;
import org.smartx.fast.bean.Client;
import org.smartx.fast.bean.SessionUser;
import org.smartx.redis.template.HashRedisTemplate;

import java.text.MessageFormat;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author kext
 * @since 1.0
 */
public class RedisSessionContext implements SessionContext {

    @Resource
    private HashRedisTemplate hashRedisTemplate;

    /**
     * 判断session user是否存在
     */
    @Override
    public boolean existSessionUser(ApiRequest request) {
        SessionUser user = request.getSessionUser();
        if (user != null) {
            String key =
                    MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
            return hashRedisTemplate.exists(key);
        }
        return false;
    }

    /**
     * 判断session id是否存在
     */
    @Override
    public boolean existSessionId(ApiRequest request) {
        SessionUser user = request.getSessionUser();
        if (user != null) {
            String key =
                    MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
            return hashRedisTemplate.hexists(key, SessionUser.SID);
        }
        return false;
    }

    /**
     * 判断是否登录
     */
    @Override
    public boolean validLogin(String uid, String sid) {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(sid)) {
            return false;
        }
        String key = MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), uid);
        return hashRedisTemplate.exists(key) && sid.equals(hashRedisTemplate.hget(key, SessionUser.SID));
    }

    @Override
    public String getSidByUid(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return null;
        }
        String key = MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), uid);
        if (hashRedisTemplate.exists(key)) {
            return hashRedisTemplate.hget(key, SessionUser.SID);
        }
        return null;
    }

    /**
     * 判断session user是否相同
     */
    @Override
    public boolean isSameSessionUser(ApiRequest request) {
        SessionUser user = request.getSessionUser();
        if (user != null) {
            String key =
                    MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
            if (hashRedisTemplate.exists(key)) {
                String sid = hashRedisTemplate.hget(key, SessionUser.SID);
                return user.getSid().equals(sid);
            }
        }
        return false;
    }

    /**
     * put session user
     */
    @Override
    public void putSessionUser(SessionUser user) {
        String key = MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
        this.hashRedisTemplate.hset(key, SessionUser.SID, user.getSid());
    }

    /**
     * destroy session user
     */
    @Override
    public void destroySessionUser(ApiRequest request) {
        SessionUser user = request.getSessionUser();
        if (user != null) {
            String key =
                    MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
            this.hashRedisTemplate.del(key);
        }
    }

    /**
     * destroy session id
     */
    @Override
    public void destroySessionId(ApiRequest request) {
        SessionUser user = request.getSessionUser();
        if (user != null) {
            String key =
                    MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), user.getUid());
            this.hashRedisTemplate.hdel(key, SessionUser.SID);
        }
    }

    /**
     * put client
     */
    @Override
    public void putClient(Client client, String uid) {
        String key = MessageFormat.format(SessionKeyEnum.USER_SESSION.getKey(), uid);
        this.hashRedisTemplate.hset(key, "client", client);
    }

}
