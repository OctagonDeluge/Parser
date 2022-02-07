import java.sql.*;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccess {

    private String host = "jdbc:postgresql://localhost:5432/";
    private String db_url;
    private String username;
    private String password;
    private static final Logger logger = Logger.getLogger("logger");

    public Connection getDBConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(db_url, username, password);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Got an exception: ", e);
        }
        return connection;
    }

    public void addStatistics(String websiteUrl, LinkedHashMap<String, Integer> wordStatistics) throws SQLException {
        logger.info("Started to push to database");

        Connection connection = getDBConnection();
        PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO website (url) VALUES (?)");
        prepStatement.setString(1, websiteUrl);
        prepStatement.execute();
        logger.info("Pushing website data to database");

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT website_id_seq.last_value FROM website_id_seq");
        result.next();

        LinkedHashMap<String, Integer> statistic = new LinkedHashMap<>(wordStatistics);
        for (String key :
                wordStatistics.keySet()) {
            prepStatement = connection.prepareStatement(
                        "INSERT INTO statistic (word, total, website_id)" +
                            " VALUES ((?), (?), (?))");
            prepStatement.setString(1, key);
            prepStatement.setInt(2, statistic.get(key));
            prepStatement.setInt(3, result.getInt(1));
            prepStatement.execute();
        }
        logger.info("Pushing word statistics to database");
    }

    public void setupWorkspace() throws SQLException {
        createTables();
        createSequences();
        logger.info("Workspace is ready");
    }

    private void createTables() throws SQLException {
        Connection connection = getDBConnection();
        Statement statement = connection.createStatement();
        statement.execute("create table website (" +
                " id int not null " +
                " constraint website_pk " +
                " primary key, " +
                " url text" +
                ")");
        logger.info("Table 'website' has been created");
        statement.execute("create table statistic (" +
                " id int not null " +
                " constraint statistic_pk " +
                " primary key, " +
                " word varchar(1000), " +
                " total int, " +
                " website_id int" +
                ")");
        logger.info("Table 'statistic' has been created");
    }

    private void createSequences() throws SQLException {
        Connection connection = getDBConnection();
        Statement statement = connection.createStatement();
        statement.execute(
                    "CREATE SEQUENCE website_id_seq;" +
                        "ALTER TABLE website ALTER COLUMN id SET DEFAULT nextval('website_id_seq');" +
                        "ALTER SEQUENCE website_id_seq OWNED BY website.id;"
        );
        logger.info("Sequence for table 'website' has been created");
        statement.execute(
                    "CREATE SEQUENCE statistic_id_seq;" +
                        "ALTER TABLE statistic ALTER COLUMN id SET DEFAULT nextval('statistic_id_seq');" +
                        "ALTER SEQUENCE statistic_id_seq OWNED BY statistic.id;"
        );
        logger.info("Sequence for table 'statistic' has been created");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabaseName(String dbname) {
        this.db_url = host + dbname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}


