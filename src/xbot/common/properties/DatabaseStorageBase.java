package xbot.common.properties;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import java.util.Date;

/**
 *
 * @author John
 */
public abstract class DatabaseStorageBase implements ITableProxy {

    private static Logger log = Logger.getLogger(DatabaseStorageBase.class);

    private String dbUrlPreFormat = "jdbc:derby:%1s;create=true";
    
    protected PropertyManager propertyManager;
   
    private final String dbUrl;

    private Connection conn;

    public DatabaseStorageBase(String databaseDirectory) {

        dbUrl = String.format(dbUrlPreFormat, databaseDirectory);
        
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            log.error("Ran into a SQL problem - could not open a connection to the database! No properties will be loaded or persisted!!");
            log.error(e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Ran into a general exception, so could not open a connection to the database! No properties will be loaded or persisted!!");
            log.error(e.toString());
            e.printStackTrace();
        }        
        
        // create properties if not exists
        if (propertiesTableExists() == false)
        {
            createPropertiesTable();
        }
    }
    
    public void setDouble(String key, double value){
        saveProperty("double", key, Double.toString(value));
    }
    
    public void setBoolean(String key, boolean value){
        saveProperty("boolean", key, Boolean.toString(value));
    }
    
    public void setString(String key, String value){
        saveProperty("string", key, value);
    }
    
    public Double getDouble(String key){
        String value = loadProperty(key);
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
    
    public Boolean getBoolean (String key){
        String value = loadProperty(key);
        // See the comments on parseBoolean() as to why we have to do this.
        return parseBoolean(value);
    }
    
    // The default Java boolean parser is good, but makes too many assumptions. Any variation of "true"
    // comes back as true, and LITERALLY ANYTHING ELSE (including null) is considered false. Since we want
    // to keep null as "I didn't find this property", we need to more explicitly parse true and false.
    private Boolean parseBoolean(String value)
    {
        if (value.toLowerCase().equals("true"))
        {
            return true;
        }
        
        if (value.toLowerCase().equals("false"))
        {
            return false;
        }
        
        return null;
    }
    
    public String getString (String key){
        String value = loadProperty(key);
        if (value.length() == 0)
        {
            return null;
        }
        return value;
    }
    
    public void clear(){
        obliterateStorage();
    }
    
    private void saveProperty(String type, String name, String value) {
       
        try {            
            PreparedStatement sta = conn.prepareStatement("UPDATE PROPERTIES SET TYPE= ?, VALUE= ? WHERE NAME = ?");
            sta.setString(1, type);
            sta.setString(2,  value);
            sta.setString(3, name);
            
            int count = sta.executeUpdate();

            if (count == 0) {
                // Looks like this isn't currently in the database. We need to add it instead.
                PreparedStatement insert = conn.prepareStatement("INSERT INTO PROPERTIES VALUES (?, ?, ?)");
                insert.setString(1, name);
                insert.setString(2, type);
                insert.setString(3, value);
                
                count = insert.executeUpdate();
            }
        } catch (SQLException e) {
            log.warn("Unable to save property " + name + "!");
            log.warn(e.toString());
            e.printStackTrace();
        }
    }
    
    private String loadProperty(String name)
    {
        try {
            PreparedStatement sta = conn.prepareStatement("SELECT * FROM PROPERTIES WHERE NAME = ?");
            sta.setString(1, name);
            ResultSet rs = sta.executeQuery();
            
            if (rs.next())
            {
                String value = rs.getString("Value");
                rs.close();
                return value;
            }
            rs.close();
        } catch (SQLException e) {
            log.warn("Unable to load property " + name + "!");
            log.warn(e.toString());
            e.printStackTrace();
        }
        return "";        
    }

    /**
     * @return True if the PROPERTIES table is present in the Database; False otherwise
     */
    private boolean propertiesTableExists() {
        try {
            DatabaseMetaData md = conn.getMetaData();
            // The following method returns tables that match a given pattern. Since we don't care about
            // Catalog, Schema, or Types (we only care about the TableNamePattern), we leave those
            // fields null.
            ResultSet tables = md.getTables(null, null, "PROPERTIES", null);

            if (tables.next()) {
                tables.close();
                return true;
            }
            tables.close();
            return false;

        } catch (SQLException e) {
            log.warn("Ran into problems when checking to see if the PROPERTIES table existed.");
            log.warn(e.toString());
            e.printStackTrace();
        }
        return false;
    }

    private boolean createPropertiesTable() {
        
        Statement sta;
        try {
            sta = conn.createStatement();
            String payload = "CREATE TABLE PROPERTIES (Name VARCHAR(100), Type VARCHAR(20), Value VARCHAR(50), UNIQUE (Name))";
            int count = sta.executeUpdate(payload);
            return true;
        } catch (SQLException e) {
            log.warn("Could not create the properties table! Properties will not be saved!!");
            log.warn(e.toString());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean obliterateStorage() {
        try {
            
            if (propertiesTableExists()) {
                Statement sta = conn.createStatement();
                String payload = "DROP TABLE PROPERTIES";
                String payload2 = "DROP TABLE PROPERTIES_HISTORIC";

                int response = sta.executeUpdate(payload);
                int response2 = sta.executeUpdate(payload2);

                if (response == 0 && response2 == 0) {
                    return true;
                }
                
                
                // something went wrong
                return false;
            } else {
                // table does not exist, nothing to obliterate
                return true;
            }
        } catch (SQLException e) {
            log.warn("Something went wrong when attempting to drop the PROPERTIES table!");
            log.warn(e.toString());
            e.printStackTrace();
            return false;
        }

    }

    
    protected void saveHistoricalDatabase() {

        try {
            // we need to be more resilient here, and only create if table doesn't exist.
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            // create table if it doesn't exist
            Statement sta = conn.createStatement();
            String payload = "CREATE TABLE PROPERTIES_HISTORIC (Date DATE, Time TIME, Version INT, Name VARCHAR(100), Type VARCHAR(20), Value VARCHAR(50))";
            int count = sta.executeUpdate(payload);
            sta.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            // create table if it doesn't exist
            Statement sta = conn.createStatement();
            String payloadvs = "SELECT distinct(version) FROM PROPERTIES_HISTORIC ORDER BY Version DESC FETCH FIRST 1 ROWS ONLY";
            ResultSet rsvs = sta.executeQuery(payloadvs);
            int version = 1;
            while (rsvs.next()) {
                version = rsvs.getInt("Version") + 1;
            }
            String payload = "SELECT * FROM PROPERTIES";
            ResultSet rs = sta.executeQuery(payload);
            
            Date date = new Date();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            java.sql.Time sqlTime = new java.sql.Time(date.getTime());
            while (rs.next()) {
                String name = rs.getString("Name");
                String type = rs.getString("Type");
                String value = rs.getString("Value");
                PreparedStatement insert = conn.prepareStatement("INSERT INTO PROPERTIES_HISTORIC VALUES (?, ?, ?, ?, ?, ?)");
                insert.setDate(1, sqlDate);
                insert.setTime(2, sqlTime);
                insert.setInt(3, version);
                insert.setString(4, name);
                insert.setString(5, type);
                insert.setString(6, value);
                int count = insert.executeUpdate();
            }
            sta.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
        }

        
    }
    
    protected String loadHistoricalVersions() {
        int tablenum = 0;
        String versionString = "";
        DatabaseMetaData metadata = null;
        try {
            metadata = conn.getMetaData();
            ResultSet result = metadata.getTables(null, null, "PROPERTIES_HISTORIC", null);
            tablenum = result.getFetchSize();            
        } catch (SQLException e) {
            e.printStackTrace();
            return versionString = "There are no historic properties to show";
        }
        try{
            if (tablenum == 1) {
                Statement sta = conn.createStatement();
                String payloadvs = "SELECT distinct Version, Date, Time FROM PROPERTIES_HISTORIC ORDER BY Version DESC FETCH FIRST 10 ROWS ONLY";
                ResultSet rs = sta.executeQuery(payloadvs);
                while (rs.next()) {
                    String version = rs.getString("Version");
                    String date = rs.getString("Date");
                    String time = rs.getString("Time");
                    if (versionString == ""){
                        versionString = version + "#" + date + " " + time;
                    }else{
                        versionString += ";" + version + "#" + date + " " + time;
                    }
                }
                return versionString;
            }
            return versionString = "There are no historic properties to show"; 
        } catch (SQLException e){
            e.printStackTrace();
            return versionString = "There are no historic properties to show";
        }
    }
}
