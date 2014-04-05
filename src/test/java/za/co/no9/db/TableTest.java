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

import static org.junit.Assert.assertEquals;

public class TableTest {
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
    public void should_retrieve_all_rows_from_a_table() throws SQLException {
        Table employers = database.useTable("employers");

        assertEquals(2, employers.all().size());
    }

    @Test
    public void should_retrieve_a_single_row_from_a_table() throws SQLException {
        Table employers = database.useTable("employers");

        assertEquals(1, employers.where("name = 'ACME Consulting'").size());
    }
}
