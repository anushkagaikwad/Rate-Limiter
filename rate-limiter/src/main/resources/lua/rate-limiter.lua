local key = KEYS[1]
local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])

-- Drop entries outside the current window
redis.call("ZREMRANGEBYSCORE", key, 0, now - window)

-- Count what's still in the window
local count = redis.call("ZCARD", key)

if count >= limit then
    return 0
end

-- Record this request and refresh TTL so idle keys expire
redis.call("ZADD", key, now, now)
redis.call("PEXPIRE", key, window)
return 1
