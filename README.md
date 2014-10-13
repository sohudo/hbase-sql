hbase-sql
=========

通过sql来查询hbase上的数据
--------
##简介
由于https://code.google.com/p/hbase-sql/上的项目不能下载，我导入到github，并在基础上进行修改

###如何简化从hbase中查询数据
为了兼容以前从关系型数据库中查询数据的接口, 让hbase可以通过sql语句来查询其中的数据.

hive有这样的功能, 他支持通过类似sql语句的语法来操作hbase中的数据, 但是速度太慢了, 因为hive本身就不是用来查询数据的, hive是数据仓库, 做数据分析的, 不适合我们的应用场景.

hbase本身提供的api中, 只有scan是用来查询数据的, 因此我们需要将sql语句转成scan 参考<<利用hbase的coprocessor机制来在hbase上增加sql解析引擎–(一)原因&架构>>发现是可行的

###总体架构为

sql语句 --sql解析器--> sql语法节点(对象) -> scan -> hbase -> ResultScanner -> List<DynaBean>
例如一个简单的sql语句

select a, b from table1 where a = 1 and b = 2
我们通过sql解析器可以得到sql语句的各个部分, 再调用hbase api中相应的语句来达到相同的效果

// 要查询的表<br>
HTable table = new HTable(conf, "table1");<br>
// 要查询的字段<br>
Scan scan = new Scan();<br>
scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("a"));<br>
scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("b"));<br>
// where条件<br>
// a = 1<br>
SingleColumnValueFilter a = new SingleColumnValueFilter(Bytes.toBytes("cf"),<br>
        Bytes.toBytes("a"), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(1)));<br>
filterList.addFilter(filter);<br>
// b = 2<br>
SingleColumnValueFilter b = new SingleColumnValueFilter(Bytes.toBytes("cf"),<br>
        Bytes.toBytes("b"), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(2)));<br>
// and<br>
FilterList filterList = new FilterList(Operator.MUST_PASS_ALL, a, b);<br>
scan.setFilter(filterList);<br>
##目前支持的功能
具体细节请参考单元测试

###1. 从oracle数据库中导入表数据到hbase

OracleDataLoader.loadTable("TABLE_NAME", new String[] { "PK_COLUMN_NAME" });<br>
###2. 通过SQL语句来查询hbase中的表数据

List<DynaBean> rows = HbaseQuery.select("SQL");<br>
目前支持的SQL语句<br>

SELECT * FROM b_month                       /* 查询所有数据 */<br>
SELECT A, B FROM b_month                   /* 只查询某些列 */<br>
SELECT * FROM b_month WHERE A = 1 and B = 2 /* 过滤条件只能是AND逻辑, 而且是等于关系 */<br>
SELECT * FROM b_month limit 3 offset 2      /* 分页 */<br>
##如何使用
###1. 在Download中下载最新版的hbase-sql.jar, 将其放在lib中.

注意项目lib的依赖<br>
commons-beanutils-core-1.8.0.jar<br>
commons-configuration-1.6.jar<br>
commons-dbutils-1.5.jar<br>
commons-lang-2.5.jar<br>
commons-logging-1.1.1.jar<br>
hadoop-core-1.2.1.jar<br>
hbase-0.94.21.jar<br>
jsqlparser-0.7.0.jar<br>
log4j-1.2.16.jar<br>
ojdbc14-10.2.0.5.jar<br>
protobuf-java-2.4.0a.jar<br>
slf4j-api-1.4.3.jar<br>
slf4j-log4j12-1.4.3.jar<br>
zookeeper-3.4.6.jar<br>

###2. 配置hbase-site.xml
在项目的src中配置好hbase-site.xml, 否则无法连接到hbase来体验hbase-sql的功能

###3. 测试

List<DynaBean> rows = new HbaseQueryImpl().select("select * from report1");
System.out.println(rows.size());
##TODO
支持更复杂的SQL查询语句
