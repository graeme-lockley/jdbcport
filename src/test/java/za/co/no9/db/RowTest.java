package za.co.no9.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RowTest {
    private Database database;

    @Before
    public void setUp() throws IOException, FixtureException {
        Fixtures fixtures = Fixtures.process(FixturesInput.fromLocation("resource:populateDatabase.yaml"));

        database = Database.useConnection(fixtures.handler(JDBCHandler.class).connection());
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void should_confirm_a_single_row_is_returned_and_the_values_are_correct() throws SQLException {
        Row acmeConsulting = acmeConsulting();

        assertEquals("ACME Consulting", acmeConsulting.get("name"));
        assertEquals(2L, acmeConsulting.get("id"));
        assertEquals(new Timestamp(114, 0, 6, 14, 22, 11, 0), acmeConsulting.get("creation_date"));
        assertEquals(3, acmeConsulting.size());
    }

    @Test
    public void should_confirm_a_single_row_update() throws SQLException {
        Row acmeConsulting = acmeConsulting();

        acmeConsulting.put("name", "New ACME Consulting");
        acmeConsulting.store();

        acmeConsulting = acmeConsulting();

        assertEquals("New ACME Consulting", acmeConsulting.get("name"));
    }

    @Test
    public void should_confirm_a_single_row_deletion() throws SQLException {
        Row acmeConsulting = acmeConsulting();

        acmeConsulting.delete();

        assertEquals(0, database.useTable("employers").where("id = 2").size());
    }

    @Test
    public void should_be_able_to_insert_a_row() throws SQLException {
        Date now = new Date();

        Map<String, Object> acmeWholesaleState = new HashMap<>();

        acmeWholesaleState.put("name", "ACME Wholesale");
        acmeWholesaleState.put("creation_date", now);

        Row acmeWholesale = database.useTable("employers").add(acmeWholesaleState);

        assertEquals(3L, acmeWholesale.get("ID"));
        assertEquals("ACME Wholesale", acmeWholesale.get("name"));
        assertEquals(now, acmeWholesale.get("creation_date"));
    }

    private Row acmeConsulting() throws SQLException {
        Collection<Row> employers = database.useTable("employers").where("id = 2");
        return (Row) employers.toArray()[0];
    }
}
