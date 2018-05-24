# swak
添加这个基准测试的项目，用来记录每个组件的性能。以方便来做系统的性能分析。

# database
结论： 数据库的性能和执行的线程数量关系比较大，和连接池基本无关系（20 个连接池）
1 threads 20 connects
Benchmark                                        Mode  Cnt      Score      Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10     35.081 ±    7.601  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10     31.227 ±   11.140  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  16510.378 ± 1939.286  ops/s
Database.jdbc_query                             thrpt   10  11931.517 ± 1790.330  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10     32.604 ±    8.830  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10     30.381 ±   14.947  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  15925.088 ±  722.604  ops/s
Database.mybatis_query                          thrpt   10  11748.403 ± 2449.956  ops/s

4 threads 20 connects
Benchmark                                        Mode  Cnt      Score      Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10     69.417 ±   14.223  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10     70.288 ±   15.577  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  42424.630 ± 2421.523  ops/s
Database.jdbc_query                             thrpt   10  39363.294 ± 4322.713  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10     66.939 ±   23.972  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10     69.183 ±   16.828  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  26354.383 ± 2536.851  ops/s
Database.mybatis_query                          thrpt   10  34122.332 ± 5618.941  ops/s

10 threads 20 connects
Benchmark                                        Mode  Cnt      Score       Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10    167.624 ±    43.704  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10    177.180 ±    28.887  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  20620.363 ±  1420.965  ops/s
Database.jdbc_query                             thrpt   10  50717.685 ±  3693.202  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10    170.585 ±    29.516  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10    185.733 ±    20.395  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  22843.152 ±  7117.388  ops/s
Database.mybatis_query                          thrpt   10  37469.705 ± 10129.749  ops/s

10 threads 40 connects
Benchmark                                        Mode  Cnt      Score      Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10    184.124 ±   19.805  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10    165.953 ±   30.574  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  21951.004 ± 6886.248  ops/s
Database.jdbc_query                             thrpt   10  50823.382 ± 4817.972  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10    177.520 ±   32.427  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10    174.592 ±   34.345  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  20536.738 ± 5274.655  ops/s
Database.mybatis_query                          thrpt   10  39935.129 ± 9918.222  ops/s

20 threads 20 connects
Benchmark                                        Mode  Cnt      Score       Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10    347.423 ±    65.410  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10    341.143 ±    53.413  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  29278.849 ±  1538.105  ops/s
Database.jdbc_query                             thrpt   10  56686.393 ±  5394.273  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10    371.792 ±    40.404  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10    349.457 ±    69.732  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  26950.054 ± 11132.979  ops/s
Database.mybatis_query                          thrpt   10  42867.593 ±  8742.580  ops/s

40 threads 20 connects
Benchmark                                        Mode  Cnt      Score       Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10    681.389 ±    98.507  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10    580.945 ±   242.731  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  24170.683 ±  1852.092  ops/s
Database.jdbc_query                             thrpt   10  54456.383 ± 12986.494  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10    701.068 ±   136.048  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10    661.667 ±   150.415  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  19957.711 ±  1905.066  ops/s
Database.mybatis_query                          thrpt   10  41594.710 ±  6118.830  ops/s

40 threads 40 connects
Benchmark                                        Mode  Cnt      Score       Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10    660.666 ±   130.159  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10    695.455 ±   173.816  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  23595.643 ±  2636.638  ops/s
Database.jdbc_query                             thrpt   10  59646.336 ± 10346.645  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10    633.371 ±   236.972  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10    631.211 ±   255.921  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  22235.265 ±  3220.966  ops/s
Database.mybatis_query                          thrpt   10  41524.146 ±  6550.296  ops/s

100 threads 20 connects
Benchmark                                        Mode  Cnt      Score       Error  Units
Database.jdbc_insert_InnoBDB                    thrpt   10   1753.698 ±   421.760  ops/s
Database.jdbc_insert_InnoBDB_no_primary_key     thrpt   10   1504.249 ±   781.257  ops/s
Database.jdbc_insert_MYISAM                     thrpt   10  30724.194 ±  2989.765  ops/s
Database.jdbc_query                             thrpt   10  53471.134 ± 13385.745  ops/s
Database.mybatis_insert_InnoBDB                 thrpt   10   1755.026 ±   450.663  ops/s
Database.mybatis_insert_InnoBDB_no_primary_key  thrpt   10   1674.278 ±   342.738  ops/s
Database.mybatis_insert_MYISAM                  thrpt   10  28172.040 ±  3760.703  ops/s
Database.mybatis_query                          thrpt   10  38353.745 ±  6000.068  ops/s