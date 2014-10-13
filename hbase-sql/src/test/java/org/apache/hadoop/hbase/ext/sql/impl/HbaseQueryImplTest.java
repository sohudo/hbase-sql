package org.apache.hadoop.hbase.ext.sql.impl;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.hadoop.hbase.ext.sql.HbaseQuery;
import org.junit.Assert;
import org.junit.Test;

public class HbaseQueryImplTest {
    HbaseQuery hbaseQuery = new HbaseQueryImpl();

    @Test
    public void testSelectAsterisk() throws SQLSyntaxErrorException, IOException {
        String sql = "SELECT * FROM b_month";
        HbaseQuery hbaseQuery = new HbaseQueryImpl();
        List<DynaBean> rows = hbaseQuery.select(sql,"diqu");
        printBean(rows,"全表");
        Assert.assertEquals(43, rows.size());
    }

    @Test
    public void testWhere() throws SQLSyntaxErrorException, IOException {
        String sql = "SELECT * FROM b_month WHERE month = 3 and year=2014 ";
        HbaseQuery hbaseQuery = new HbaseQueryImpl();
          List<DynaBean> rows = hbaseQuery.select(sql,"diqu");
          printBean(rows,"条件");
        Assert.assertEquals(22, rows.size());
    }

    @Test
    public void testLimit() throws SQLSyntaxErrorException, IOException {
        String sql = "SELECT * FROM b_month limit 3 offset 2";
        HbaseQuery hbaseQuery = new HbaseQueryImpl();
        List<DynaBean> rows = hbaseQuery.select(sql,"diqu");
        printBean(rows,"Limit");
        Assert.assertEquals(3, rows.size());
    }

    private static void printBean(List<DynaBean> beans,String bz) {
    	if (beans.size()==0) {
    		System.out.println("------------"+bz+" 无记录---------------");
    		return;
    	}
        DynaProperty[] properties = beans.get(0).getDynaClass()
                .getDynaProperties();
        StringBuilder str = new StringBuilder();
        for (DynaProperty property : properties) {
            str.append(property.getName()).append("\t");
        }
        str.append("\n----------------------------------\n");

        for (DynaBean bean : beans) {
            for (DynaProperty property : properties) {
                str.append(bean.get(property.getName())).append("\t");
            }
            str.append("\n");
        }
        System.out.println("------------"+bz+"---------------");
        System.out.print(str);
        System.out.println("----------------------------------");
        System.out.println("记录数:"+beans.size());
    }
}
