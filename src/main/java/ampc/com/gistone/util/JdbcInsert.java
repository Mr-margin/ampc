package ampc.com.gistone.util;

import java.sql.Connection;  
import java.sql.Date;
import java.sql.DriverManager;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.sql.Statement;  
import java.text.SimpleDateFormat;
import java.util.List;

import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TEmissionDetailWithBLOBs;


public class JdbcInsert {
    public static void main(List<TEmissionDetailWithBLOBs> TElist)  
    {  
        //调用连接数据库的操作  
        Connection con = createConnection(TElist);      
          
                  
    }  
      
    /** 
     * JDBC 建立 SQL Server数据库连接 
     */  
    private static Connection createConnection(List<TEmissionDetailWithBLOBs> TElist) {  
          
        String driver = "oracle.jdbc.OracleDriver";    //驱动标识符
        String url = "jdbc:oracle:thin:@192.168.4.215:1521:ORCL"; //链接字符串
        // url ="jdbc:oracle:thin:@10.0.30.64:1521:orcl";  // 连接远程的数据库可以这么写
        String user = "ampc";//数据库的用户名
        String password = "orcl";  //数据库的密码
        Connection connection  = null;
        Statement sta = null ;
        try {
            //正式加载驱动  
            Class.forName(driver);
            //开始连接  
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Success !");
              
            //向数据库中执行SQL语句  
            String sql = "insert into T_EMISSION_DETAIL (EMISSION_DATE, CODE, EMISSION_DETAILS,CODELEVEL,PM25,PM10,SO2,\"NOx\",VOC,CO,NH3,BC,OC,PMFINE,PMC)values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            final int batchSize = 5000;
            int count = 0;
            for (TEmissionDetailWithBLOBs tEmissionDetail: TElist) {
                ps.setDate(1, new java.sql.Date(tEmissionDetail.getEmissionDate().getTime()));
                ps.setString(2, tEmissionDetail.getCode());
                ps.setString(3, tEmissionDetail.getEmissionDetails());
                ps.setString(4, tEmissionDetail.getCodelevel());
                ps.setBigDecimal(5,tEmissionDetail.getPm25());
                ps.setBigDecimal(6,tEmissionDetail.getPm10());
                ps.setBigDecimal(7,tEmissionDetail.getSo2());
                ps.setBigDecimal(8,tEmissionDetail.getNox());
                ps.setBigDecimal(9,tEmissionDetail.getVoc());
                ps.setBigDecimal(10,tEmissionDetail.getCo());
                ps.setBigDecimal(11,tEmissionDetail.getNh3());
                ps.setBigDecimal(12,tEmissionDetail.getBc());
                ps.setBigDecimal(13,tEmissionDetail.getOc());
                ps.setBigDecimal(14,tEmissionDetail.getPmfine());
                ps.setBigDecimal(15,tEmissionDetail.getPmc());
                ps.addBatch();
                if(++count % batchSize == 0||count==TElist.size()){
                    ps.executeBatch();
                }
            }
        }catch (Exception e){
            System.out.println("Connection Fail !");
            e.printStackTrace(); 
        }
          
        /** 
         * 关闭数据库 
         * @param connection 
         */  
        finally  
        {  
            try {  
                  
                if (null != sta)  
                {  
                    sta.close() ;  
                    sta = null;  
                    System.out.println("Statement 关闭成功");  
                }  
                  
                if (null != connection)  
                {  
                    connection.close() ;  
                    connection = null;  
                    System.out.println("Connection 关闭成功");  
                }  
                  
            } catch (Exception e) {  
                  
                e.printStackTrace() ;  
            }         
              
        }         
        return connection ;  
    }  
}
