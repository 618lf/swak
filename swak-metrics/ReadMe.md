# 系统指标
目标：对系统指标进行收集、上報

# DB.pool.Wait
A Timer instance collecting how long requesting threads to getConnection() are waitting for
a connection(or timeout exception) from the pool

# DB.pool.Usage
A Histogram instance collecting how long each connection is used before being returned to the pool.
This is the "out of pool" or "in-use" time.

# DB.pool.TotalConnections
A CachedGauge, refreshed on demand at 1 second resolution, indication the total number of the connections
in the pool.

# DB.pool.IdeaConnections
A CacheGuage, refreshed on demand 1 senond resolution, indication the idea number of the connections 
int the pool.

# DB.pool.ActiveConnections
A CachedGauge, refreshed on demand at 1 second resolution, indicating the number of active (in-use) connections in the pool.

# DB.pool.PendingConnections
A CachedGauge, refreshed on demand at 1 second resolution, indicating the number of threads awaiting connections from the pool.