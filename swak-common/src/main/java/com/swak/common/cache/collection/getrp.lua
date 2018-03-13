if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then  
  return nil  
else  
  local hongBao = redis.call('rpop', KEYS[1]);  
  if hongBao then  
    local x = cjson.decode(hongBao);  
    x['userId'] = KEYS[4];  
    local re = cjson.encode(x);  
    redis.call('hset', KEYS[3], KEYS[4], KEYS[4]);  
    redis.call('lpush', KEYS[2], re);  
    return re;
  end  
end  
return nil