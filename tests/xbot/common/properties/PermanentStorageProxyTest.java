package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.OffRobotDatabaseStorage;

public class PermanentStorageProxyTest extends BaseWPITest {

    private String testFolder = "./TeamDatabase";
    
    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testSaveAndLoad() {
    	
    	DatabaseStorageBase p = propertyManager.permanentStore;
    	
    	p.setDouble("fancyname", 1.23);
    	p.setBoolean("flag", true);
    	p.setString("phrase", "What time is it?");
        
        assertEquals(1.23, p.getDouble("fancyname"), 0.1);
        assertEquals(true, p.getBoolean("flag"));
        assertEquals("What time is it?", p.getString("phrase"));        
    }
    
    @Test
    public void testHistoricSavingValue() {
         DoubleProperty dbl2 = propertyManager.createEphemeralProperty("weight", 2.3);
         BooleanProperty bool2 = propertyManager.createEphemeralProperty("isFalse", true);
         StringProperty str2 = propertyManager.createEphemeralProperty("robotname", "xbot");
         
         DoubleProperty dbl3 = propertyManager.createPersistentProperty("height", 4.8);
         BooleanProperty bool3 = propertyManager.createPersistentProperty("isAwesome", true);
         StringProperty str3 = propertyManager.createPersistentProperty("team", "488");
        
         assertSame(null, propertyManager.permanentStore.getDouble("weight"));
         assertSame(null, propertyManager.permanentStore.getBoolean("isFalse"));
         assertSame(null, propertyManager.permanentStore.getString("robotname"));
         
         assertSame(null, propertyManager.permanentStore.getDouble("height"));
         assertSame(null, propertyManager.permanentStore.getBoolean("isAwesome"));
         assertSame(null, propertyManager.permanentStore.getString("team"));
        
         assertEquals(2.3,dbl2.get(), 0.001);
         assertEquals(true,bool2.get());
         assertEquals("xbot",str2.get());
         assertEquals(4.8,dbl3.get(), 0.001);
         assertEquals(true,bool3.get());
         assertEquals("488",str3.get());
        
        propertyManager.saveHistorical();
        
        assertEquals(0.5, propertyManager.permanentStore.getDouble("speed").doubleValue(), 0.001);
        assertEquals(false,propertyManager.permanentStore.getBoolean("isTrue").booleanValue());
        assertEquals("test2",propertyManager.permanentStore.getString("string"));
    }
    
    @Test
    public void loadNothing()
    {
    	// No exceptions should be thrown.
    	propertyManager.permanentStore.loadFromDisk();
    }
    
    public void testClear() {
        DatabaseStorageBase p = propertyManager.permanentStore;
        
        p.setDouble("fancyname", 1.23);
        p.setBoolean("flag", true);
        p.setString("phrase", "What time is it?");
        // need to verify that information was loaded
        assertEquals(p.getDouble("fancyname"), 1.23, .01);
        
        p.clear();
        
        boolean result = p.getDouble("fancyname") == null;
        
        assertEquals("Should get null back when the table is clear!", result, true);
    }
    
    @After
    public void cleanUp()
    {
    	// We need a way to obliterate the database locally so tests don't leak. Can't delete the files themselves,
    	// because the database process still has a handle on some of them.
    	 assertEquals(true, ((OffRobotDatabaseStorage)propertyManager.permanentStore).obliterateStorage());
    }
}
