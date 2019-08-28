local job = redis.call('get', KEYS[1]);
if job then
   if tonumber(job) == -1 then
      redis.call('set', KEYS[1], 0);
      return '1'
   end
else 
  redis.call('set', KEYS[1], 0);
  return '1'
end
return '0'