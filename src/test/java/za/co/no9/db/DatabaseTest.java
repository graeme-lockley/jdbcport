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

import static org.junit.Assert.assertNotNull;

public class DatabaseTest {
    private Database database;

    @Before
    public void setUp() throws IOException, FixtureException {
        Fixtures fixtures = Fixtures.process(FixturesInput.fromLocation("resource:databaseSetup.yaml"));

        database = Database.useConnection(fixtures.handler(JDBCHandler.class).connection());
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test(expected = java.sql.SQLException.class)
    public void should_throw_an_exception_of_the_table_does_not_exist() throws SQLException {
        database.useTable("UnknownTable");
    }

    @Test
    public void should_find_table_if_it_exists() throws SQLException {
        Table people = database.useTable("people");

        assertNotNull(people);
    }
}
